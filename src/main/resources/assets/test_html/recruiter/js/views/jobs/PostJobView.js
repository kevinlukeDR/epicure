define([
	'jquery',
	'underscore',
	'backbone',
	'util',
	'models/jobs/Draft',
	'models/jobs/Published',
	'bootstrapDatepicker',
	'jqueryValidate',
	'jquerySerializeObject'
], function($, _, Backbone, util, Draft, Published, datepicker, validate, serializeObject) {
	var PostJobView = Backbone.View.extend({
		el: $("#content"),
		initialize: function() {
			var templateStr = window.util.loadTemplateSync("templates/Jobs/PostJobView.html");
			this.template = _.template(templateStr);
		},
		render: function() {
			this.$el.html(this.template());
			$('.date-input', this.el).datepicker();
			return this;
		},

		events: {
			'click #saveBtn': 'saveNow',
			"click #publishBtn": "postJob",
			'keyup textarea': 'limit',
		},
		limit: function(e) {
			var $this = $(e.currentTarget);

			var length = $this.val().length;
			var maxLength = $this.data('limit');
			var $text = $($this.data('text'));
			var length = maxLength - length;

			if (length < 0) {
				$text.addClass('red-text');
				$text.text(length)
			} else {
				$text.removeClass('red-text').text(length);
			}
		},
		formValid: function() {
			var form = $('#job-post');
			form.validate({
				errorElement: 'span',
				errorClass: 'help-block',
				onkeyup: true,
				focusInvalid:true,
				highlight: function(element, errorClass, validClass) {
					$(element).closest('.form-group').addClass("has-error");
				},
				unhighlight: function(element, errorClass, validClass) {
					$(element).closest('.form-group').removeClass("has-error");
				},
				rules: {
					position: {
						required: true,
						maxlength: 65
					},
					city: {
						required: true,
						minlength: 3,
					},
					jobType: {
						required: true,
					},
					contractLen: {
						required: true,
						min: 0
					},
					eligibleCandidates: {
						required: true,
					},
					subject: {
						required: true,
					},
					classSize: {
						required: true,
					},
					startDate: {
						required: true,
					},
					grade: {
						required: true
					},
					aboutSchool: {
						maxlength: 5000
					},
					additonal: {
						maxlength: 1000
					},
					salaryMessage: {
						maxlength: 255
					}
				},
				messages: {
					position: {
						required: 'Title is required.'
					},
					city: {
						required: 'Location is required.',
						minlength: 'Please enter the full name of the city',
					},
					jobType: {
						required: 'Job type is required.',
					},
					contractLen: {
						required: 'Contract Length is required.',
					},

					// eligibleCandidates:{
					// 	required:true,
					// },
					// subject:{
					// 	required:true,
					// },
					// classSize:{
					// 	required:true,
					// },
					startDate: {
						required: 'Please select an estimate start date',
					}
				}
			});

			return form.valid();
		},
		//save drafts
		saveNow: function(e) {
			e.preventDefault();
			var $btn = $(e.currentTarget);
			var position = $('#position').val();
			if (position) {
				$('.help-block').remove();
				$('#position-area').removeClass("has-error");
				var form = $('#job-post').serializeObject();
				console.log(form);
				var draft = new Draft();
				draft.save(form, {
					success: function(model) {
						// console.log(model);
						window.location.hash = '#drafts';
					}
				})
			} else {
				$('#position-area').addClass("has-error").append('<span class="help-block"Position title is required.</span>');
			}
		},
		//publish job
		postJob: function(e) {
			e.preventDefault();
			$('#job-error').empty().addClass('hidden');
			var self = this;
			var $btn = $(e.currentTarget);
			console.log(this.formValid());
			if (this.formValid()) {
				$btn.text('Loading').prop("disabled", true);
				var form = $('#job-post').serializeObject();
				console.log(form);
				var published = new Published();
				published.save(form, {
					success: function(model, response) {
						console.log(model);
						self.undelegateEvents();
						if (response.metadata.error) {
							$btn.text('Login').prop("disabled", false);
							$('#job-error').text(response.metadata.error.error_message).removeClass('hidden');
						} else {
							alert('success');
							window.location.hash = 'published';
						}
					}
				})
			}
			// var form = $('#job-post');
			// var data = new FormData(form[0]);

			// var url = "/api/job/insert/";


			// $.ajax({
			// 	url: url,
			// 	method: "POST",
			// 	contentType: false,
			// 	processData: false,
			// 	data: data,
			// 	statusCode: {
			// 		403: function() {
			// 			//access denied
			// 			window.location.hash = 'denied';
			// 		}
			// 	},
			// 	success: function(data) {
			// 		console.log(data);
			// 		// alert("Post Job Success!");
			// 		if (data.error) { // If there is an error, show the error messages
			// 			$('#feedback').text(data.error.text).show();
			// 		} else {
			// 			var job_id = data;

			// 			window.location.hash = 'recruiter/postJob/' + job_id;

			// 		}
			// 	}
			// });
		},
		destroy: function() {
			this.undelegateEvents();
			this.$el.removeData().unbind();
			this.remove();
		}

	});
	return PostJobView;
});