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
], function($, _, Backbone, util,Draft,Published, datepicker, validate, serializeObject) {
	var DraftDetailView = Backbone.View.extend({
		el: $("#content"),
		initialize: function() {
			var templateStr = window.util.loadTemplateSync("templates/Jobs/DraftDetailView.html");
			this.template = _.template(templateStr);
			console.log(this.model);
		},
		render: function() {
			this.$el.html(this.template(this.model.attributes));

			this.renderDate();
			return this;
		},
		events: {
			'click #saveBtn': 'saveNow',
			"click #publishBtn": "postJob",
			'keyup textarea': 'limit',
		},
		renderDate: function() {
			$('.date-input', this.el).datepicker();
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
				focusInvalid: true,
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
			var self = this;
			if (position) {
				$('.help-block').remove();
				$('#position-area').removeClass("has-error");
				var form = $('#job-post').serializeObject();
				console.log(form);
				
				self.model.save(form, {
					success: function(model) {
						console.log(model);
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
						if (response.metadata.error) {
							$btn.text('Login').prop("disabled", false);
							$('#job-error').text(response.metadata.error.error_message).removeClass('hidden');
						} else {
							alert('success')
						}
					}
				})
			}else{
				$(".has-error > input").focus();
			}
		},
		destroy: function() {
			this.undelegateEvents();
			this.$el.removeData().unbind();
			this.remove();
		}
	});
	return DraftDetailView;
});