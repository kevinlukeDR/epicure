directory.VideoView = Backbone.View.extend({
	initialize: function() {
		this.listenTo(this.model, "change", this.render, this);
	},
	render: function() {
		this.$el.html(this.template());
		if (this.model.attributes.path === undefined) {
			$('.have', this.el).addClass('hidden');
		} else {
			this.playVideo();

		}
		this.loadQ();
		this.renderStrength();
	},

	events: {
		"click .play": function(e) {
			e.preventDefault();
			this.playVideo();
		},
		"click .replace": function(e) {
			e.preventDefault();
			this.replaceVideo();
		},
		"click .open-recording": function(e) {
			e.preventDefault();
			this.takeVideo();
		},
		'click #upload': 'expendUpload',
		'click #submit-button': 'getData',
		'drop #drop-zone': 'drop',
		'dragover #drop-zone': 'drag',
		'dragleave #drop-zone': 'dragl',
	},
	renderStrength: function() {
		var strength = new directory.Strength();
		var self = this;
		strength.fetch({
			success: function(model, response) {
				if (response.metadata.status == "ok") {
					$('#profile-strength', self.el).html(new directory.ProfileStrView({
						model: model
					}).render().el)
				}
			}
		})

	},

	loadQ: function() {
		$('#interview-questions', this.el).load('docu/InterviewQ.html');
	},
	playVideo: function() {
		$('.question-container', this.el).addClass('hidden');
		$('.video-button-container', this.el).html('<span class="video-play replace"><i class="fa fa-video-camera" aria-hidden="true"></i>Replace Video</span>');
		var url = "https://s3.us-east-2.amazonaws.com/esl-public/" + this.model.attributes.path;
		$('.video-container', this.el).html('<video width="100%" height="100%" controls ><source src="' +
			url + '" type="video/webm" codecs="vp8, vorbis"></video>');

	},
	replaceVideo: function() {
		$('.video-button-container').html('<span class="video-play play"><i class="fa fa-play-circle-o" aria-hidden="true"></i>Play Video</span>');
		$('.video-container', this.el).empty();
		$('.question-container', this.el).removeClass('hidden');
	},
	takeVideo: function() {
		$('#upload-container').addClass('hidden');
		var modal = new directory.RecordingModal();
		$('.recording-modal').html(modal.render().el);
		$('#recordingModal').modal('show');
		$('#recordingModal').on('hidden.bs.modal', function() {
			modal.childView.cancelClicked();
			Backbone.history.loadUrl();
		})
	},

	expendUpload: function(e) {
		e.preventDefault();
		$('#upload-container').removeClass('hidden');
	},

	getData: function(e) {
		e.preventDefault();
		var $btn = $(e.currentTarget);
		
		var form = $('#upload-video-form');
		form.validate({
            errorElement: 'span',
            errorClass: 'help-block',
            highlight: function(element, errorClass, validClass) {
                $(element).closest('.form-group').addClass("has-error");
            },
            unhighlight: function(element, errorClass, validClass) {
                $(element).closest('.form-group').removeClass("has-error");
            },
            rules: {
            	video: {
            		required: true,
            		extension: "mov|mp4",
            	}
            },
            messages: {
            	video:{
            		required: 'Please attach a file',
            		extension: 'Please attach only .mov or .mp4 files'
            	}
            }
        });

		var data = new FormData(form[0]);
		console.log(form.valid());
		if (form.valid() === true) {
			$btn.text('Loading...').prop("disabled", true);
			this.upload(data);
		}else{
			$btn.text('Submit').prop("disabled",false);
		}
	},
	upload: function(file){
		var url = "/api/video/";
		var self = this;
		$.ajax({
			url: url,
			method: "POST",
			contentType: false,
			processData: false,
			data: file,
			dataType: "json",
			success: function(data) {
				console.log(data);
				self.model.set('path', data.data.path);
				Backbone.history.loadUrl();
			}
		});
	},
	drop: function(e) {
		e.preventDefault();
		$(e.currentTarget).addClass('upload-drop-zone');
		console.log(e.originalEvent.dataTransfer.files)
		var data = new FormData();
		data.append('video', e.originalEvent.dataTransfer.files[0]);
		this.upload(data);
	},

	drag: function(e) {
		e.preventDefault();
		$(e.currentTarget).addClass('drop');
		return false;
	},
	dragl: function(e) {
		e.preventDefault();
		$(e.currentTarget).removeClass('drop');
		return false;
	},

})