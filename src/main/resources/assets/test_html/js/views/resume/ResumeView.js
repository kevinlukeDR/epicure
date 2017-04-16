directory.ResumeView = Backbone.View.extend({
	initialize: function() {
		// console.log(this.model);
		this.listenTo(this.model, "change", this.render, this);
		this.listenTo(this.model, "destroy", this.remove, this);
	},
	render: function() {
		this.$el.html(this.template());
		if (this.model.attributes.id === undefined) {
			this.noResume();
		} else {
			$('#resume-list', this.$el).append(new directory.ResumeListView({
				model: this.model
			}).render().el);
		}
		// if (this.collection.models[0].id === undefined) {
		// 	this.noResume();
		// } else {
		// 	_.each(this.collection.models, function(resume) {
		// 		$('ul', this.$el).append(new directory.ResumeListView({
		// 			model: resume
		// 		}).render().el);
		// 	}, this);

		// }
		// this.collection.each(this.renderItem);
		this.renderStrength();
		return this;
	},


	events: {
		'click #replace': 'getData',
		'drop #drop-zone': 'drop',
		'dragover #drop-zone': 'drag',
		'dragleave #drop-zone': 'dragl',

	},

	renderStrength: function(){
        var strength = new directory.Strength();
        var self = this;
        strength.fetch({
            success: function(model, response){
                if (response.metadata.status == "ok") {                   
                    $('#profile-strength', self.el).html(new directory.ProfileStrView({model: model}).render().el)
                }
            }
        })
        
    },
	noResume: function() {
		$('.no-resume', this.el).html('<p class="no-resume">Sorry You have not uploaded any resume yet.</p><form enctype="multipart/form-data" id="resume-form" class="text-center"></form>\
			 <h4>Or drag and drop files below</h4><div class="upload-drop-zone" id="drop-zone">Just drag and drop files here</div>');
		$('#resume-form', this.el).html('<div class="form-inline"><div class="form-group mr10">\
			<label>Select a resume file to upload <span class="text-muted">.pdf,.doc,.docx</span></label><input type="file" name="file" class="form-control-file"/></div><button type="submit" class="btn btn-blue"\
			id="replace">Submit</button></div>');

	},
	renderItem: function(model) {
		console.log(model);
		// model.fetch({
		// 	success: function(data){
		// 		console.log(data);
		// 	}
		// })
		var resumeList = new directory.ResumeListView({
			model: model
		});
		resumeList.delegateEvents();
		resumeList.render();
		$('#resume-list').html(resumeList.el);
	},
	drop: function(e) {
		e.preventDefault();
		$(e.currentTarget).addClass('upload-drop-zone');
		console.log(e.originalEvent.dataTransfer.files)
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
	getData: function(e) {
		e.preventDefault();
		var $btn = $(e.currentTarget);
		
		var form = $('#resume-form');
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
            	file:{
            		required: 'Please attach a file',
            		extension: 'Please attach only pdf, doc or docx files'
            	}
            }
        });

		var data = new FormData(form[0]);
		if (form.valid() === true) {
			$btn.text('Loading...').prop("disabled", true);
			this.uploadResume(data);
		}else{
			$btn.text('Submit').prop("disabled",false);
		}
		
		$btn.text('Submit').prop("disabled",false);
	},

	uploadResume: function(file) {
		$('.alert-profile').addClass('hidden'); //hidden all status message
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
				self.model.set('id', data.metadata.resumeId);
				// console.log(data.)
			}
		});
	}

});

directory.ResumeListView = Backbone.View.extend({
	
	initialize: function() {
		// console.log(this.model);
		this.listenTo(this.model, "change", this.render, this);
		this.listenTo(this.model, "destroy", this.remove, this);
	},
	events: {
		'drop #drop-zone': 'drop',
		'dragover #drop-zone': 'drag',
		'dragleave #drop-zone': 'dragl',
		'click #replaceResume': function(e) {
			e.preventDefault();
			this.replaceResume();
		},
		'click #replace': 'getData',
		'click #viewResume': function(e) {
			e.preventDefault();
			this.viewResume();
		},

		// 'click body': 'removeResumeView'
	},
	render: function() {

		this.$el.html(this.template(this.model.attributes));

		this.renderResume();
		return this;
	},

	renderResume: function() {
		var self = this;
		$.ajax({
			url: '/api/resume/' + this.model.attributes.id,
			type: 'GET',
			contentType: 'application/pdf',
			success: function(data, text, response) {
				var header = response.getResponseHeader('Content-Disposition');
				console.log(decodeURIComponent(header));
				header = decodeURIComponent(header);
				var filename = header.match(/filename =(.+)/)[1];
				$('.filename').text(filename);

				self.$el.append('<embed width="100%" height="650px" src="data:application/pdf;base64,' + data + '" />');
				// console.log(data);
			}
		});
	},
	drop: function(e) {
		e.preventDefault();
		$(e.currentTarget).addClass('upload-drop-zone');
		console.log(e.originalEvent.dataTransfer.files)
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
	replaceResume: function() {
		$('.white-panel', this.el).removeClass('hidden');
		$('#resume-replace-form', this.el).html('<div class="form-inline"><div class="form-group mr10">\
			<label>Select a resume file to upload <span class="text-muted">.pdf,.doc,.docx</span></label>\
			<input type="file" name="file" class="form-control-file"/></div><button type="submit" class="btn btn-blue"\
			id="replace">Submit</button></div>');
	},

	getData: function(e) {
		e.preventDefault()
		var $btn = $(e.currentTarget);
		// $btn.text('Loading...').prop("disabled", true);
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
            	file:{
            		required: 'Please attach a file',
            		extension: 'Please attach only pdf, doc or docx files',
            	}
            }
        });
		var data = new FormData(form[0]);

		if (form.valid()=== true) {
			$btn.text('Loading...').prop("disabled", true);
			this.uploadResume(data);
		}else{
			$btn.text('Submit').prop("disabled",false);
		};
		
		
	},
	uploadResume: function(file) {
		$('.alert-profile').addClass('hidden'); //hidden all status message
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
				self.model.set('id', data.metadata.resumeId);
				// console.log(data.)
			}
		});
	},
	viewResume: function() {
		var self = this;
		var id = this.model.attributes.id;

		$.ajax({
			url: '/api/resume/' + id,
			type: 'GET',
			contentType: 'application/pdf',
			success: function(data, text, response) {
				var header = response.getResponseHeader('Content-Disposition');
				var filename = header.match(/filename =(.+)/)[1];
				var blob = b64toBlob(data, 'application/pdf');

				var link = document.createElement('a');
				document.body.appendChild(link);
				link.href = window.URL.createObjectURL(blob);
				link.download = filename + ".pdf";
				link.click();
			}
		})
	},


	removeResumeView: function() {
		$('embed').remove();
	}
});

function b64toBlob(b64Data, contentType, sliceSize) {
	contentType = contentType || '';
	sliceSize = sliceSize || 512;

	var byteCharacters = atob(b64Data);
	var byteArrays = [];

	for (var offset = 0; offset < byteCharacters.length; offset += sliceSize) {
		var slice = byteCharacters.slice(offset, offset + sliceSize);

		var byteNumbers = new Array(slice.length);
		for (var i = 0; i < slice.length; i++) {
			byteNumbers[i] = slice.charCodeAt(i);
		}

		var byteArray = new Uint8Array(byteNumbers);

		byteArrays.push(byteArray);
	}

	var blob = new Blob(byteArrays, {
		type: contentType
	});
	return blob;
}