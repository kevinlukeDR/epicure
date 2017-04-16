directory.ApplyView = Backbone.View.extend({
	className: 'content-panel',
	render: function() {

		this.$el.html(this.template());
		this.renderWarning();
		this.getEmail();
		this.renderLatest();
		return this;
	},

	events: {
		'click #replace': 'replace',
		'click #new': 'new',
		'click #upload': 'getData',
		'drop #drop-zone': 'drop',
		'dragover #drop-zone': 'drag',
		'dragleave #drop-zone': 'dragl',
		'click #apply': "apply",
		'keyup textarea': 'limitLength'
	},
	renderWarning: function(){
		var strength = new directory.Strength();
        var self = this;
        strength.fetch({
            success: function(model, response){
            	if (model.attributes.score < 40) {
            		$('.profile-warning',self.el).removeClass('hidden');
            	}else{
            		$('.profile-warning',self.el).addClass('hidden');
            	}
            }
        });
	},
	getEmail: function() {
		var self = this;
		$.ajax({
			url: "/api/candidate/email",
			method: "GET",
			success: function(data) {
				var email = data.data.email;
				console.log(email);
				self.$('#email', this.el).val(email);
			}
		});
	},
	renderLatest: function() {
		var self = this;
		$('#resume-content', this.el).html('<h5>Resume on file: </h5><input type="hidden" name="resumePath" id="resumePath"><div class="row">\
			<div class="col-xs-9"><p id="latest-resume"></p></div><div class="col-xs-3">\
			<span class="text-button" id="new"><i class="fa fa-cloud-upload" aria-hidden="true"></i> New</span></div></div>');
		var resumes = new directory.ResumeLatest();
		resumes.fetch({
			success: function(model, response) {
				if (response.metadata.error) {
					self.new();
				} else {
					var d = new Date(model.attributes.setDate);
					var date = [d.getMonth() + 1, d.getDate(), d.getFullYear()];
					date = date.join('-');
					console.log(date);
					var filename = response.metadata.name;
					$('#latest-resume', this.el).html('<a>' + filename + '</a><span class="text-muted">' + date + '</span>');
					$('#resumePath', this.el).val(model.attributes.path);
				}

			}
		})
	},
	limitLength: function() {
		var length = $('textarea', this.el).val().length;
		var maxLength = 5000;
		var length = maxLength - length;

		if (length < 0) {
			$('#chars', this.el).addClass('red-text');
			$('#chars', this.el).text(length)
		} else {
			$('#chars', this.el).removeClass('red-text').text(length);
		}
	},

	drop: function(e) {
		e.preventDefault();
		$(e.currentTarget).addClass('upload-drop-zone');
		console.log(e.originalEvent.dataTransfer.files);
		var data = new FormData();
		data.append('file', e.originalEvent.dataTransfer.files[0]);
		this.uploadResume(data);
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
	new: function(e) {
		$('#resume-content').addClass('text-center').html('<form enctype="multipart/form-data" id="resume-replace-form">\
				<div class="form-inline"><div class="form-group mr10"><label>Select a resume file to upload <span class=\
				"text-muted">.pdf,.doc,.docx</span></label><input type="file" name="file" class="form-control-file"/></div><button type="submit" class="btn btn-blue"\
			id="upload">Upload</button></div></form><h4>Or drag and drop files below</h4><div class="upload-drop-zone" id="drop-zone">\
			Just drag and drop files here</div>')
	},
	replace: function(e) {
		e.preventDefault();
		var resumeList = new directory.ResumeCollection();
		resumeList.fetch({
			success: function(data) {
				console.log(data);
				_.each(data.models, function(resume) {
					$('#resume-list').append(new directory.ReplaceListView({
						model: resume
					}).render().el);
				}, this);
			}
		})
	},
	getData: function(e) {
		e.preventDefault();
		var $btn = $(e.currentTarget);
		$btn.text('Loading...').prop("disabled", true);


		var form = $('#resume-replace-form');

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
				file: {
					required: true,
					extension: "pdf|doc|docx"
				}
			},
			messages: {
				file: {
					required: 'Please attach a file',
					extension: 'Please attach only pdf, doc or docx files'
				}
			}
		});
		var data = new FormData(form[0]);
		if (form.valid() === true) {
			this.uploadResume(data);
		} else {
			$btn.text('Submit').prop("disabled", false);
		}
	},
	uploadResume: function(file) {
		// $('.alert-profile').addClass('hidden'); //hidden all status message
		var url = "/api/resume/update/";
		var self = this;
		$.ajax({
			url: url,
			method: "PUT",
			contentType: false,
			processData: false,
			data: file,
			dataType: "json",
			success: function(data) {
				console.log(data);
				self.renderLatest();
				// self.model.set('id', data.metadata.resumeId);
				// console.log(data.)
			}
		});
	},
	apply: function(e) {
		e.preventDefault();
		//disable button 
		var $btn = $(e.currentTarget);
		$btn.text('Loading...').prop("disabled", true);


		var job_id = $("input[name='job_id']").val();
		var email = $('#email').val();
		console.log(job_id);
		var formValue = {
			job_id: job_id,
			resumePath: $('#resumePath').val(),
			contactEmail: $('#email').val(),
			additionalMessage: $('additionalMessage').val(),

		};
		console.log(formValue);

		var applyJob = new directory.ApplyJob();
		applyJob.save(formValue, {
			success: function(model, response) {
				$btn.text('Login').prop("disabled", false);
				if (response.metadata.error) {
					alert(response.metadata.error.error_message);
				} else {
					window.location.hash = '#jobs/' + job_id + '/applyReceived';
						// alert('success!')

				}
			}
		})



	}
});


directory.ReplaceListView = Backbone.View.extend({
	tagName: 'li',

	template: _.template('<%= name %><span class="text-muted">Post on</span><%= date %>'),

	render: function() {
		this.$el.html(this.template(this.model.attributes));
		return this;
	}
});