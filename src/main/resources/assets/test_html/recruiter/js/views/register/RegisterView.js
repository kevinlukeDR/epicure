define([
	'jquery',
	'underscore',
	'backbone',
	'util',
	'views/register/SuccessView',
	'jquerySerializeObject',
	'jqueryValidate',
], function($, _, Backbone, util, SuccessView, serializeObject, validate) {
	var RegisterView = Backbone.View.extend({
		el: $("#login"),
		initialize: function() {
			var templateStr = window.util.loadTemplateSync("templates/Register/RegisterView.html");

			this.template = _.template(templateStr);

		},
		render: function() {
			this.$('#wrap').empty();
			this.$el.html(this.template());
			return this;
		},

		events: {
			"blur #inputEmail": "checkEmail",
			"click #notSubmit": "contactEmail",
			"change #countryCode": "getCountryCode",
			'keyup textarea': 'limitLength',
			'click #registerButton': 'register'
		},

		contactEmail: function(e) {
			var email = $('#inputEmail').val();
			$('#contactEmail').val(email);
		},

		getCountryCode: function() {
			var countryCode = $('#countryCode').val();
			$('#phoneNumber').val(countryCode);
			$('#phoneNumber').focus();
		},

		checkEmail: function() {
			$('.email .glyphicon').remove();
			$('#email-error').remove();
			$('div.email').removeClass("has-success");
			$('div.email').removeClass("has-error");

			var email = $('#inputEmail').val();
			if (email) {
				$.ajax({
						url: "/api/recruiter/test/" + email
					})
					.done(function(data) {
						console.log(data);
						if (data.metadata.status == "ok") {
							$('div.email').addClass("has-success");
							$('div.email').append("<span class='glyphicon glyphicon-ok form-control-feedback' aria-hidden='true'></span>");
							$("#registerButton").prop("disabled", false);
						} else {
							$('div.email').addClass("has-error");
							$('div.email').append("<span class='glyphicon glyphicon-remove form-control-feedback' aria-hidden='true'></span>");
							$('div.email').append('<span class="help-block" id="email-error">Sorry this email has been registered.</span>')
							$("#registerButton").prop("disabled", true);
						}
					});
			}
		},

		register: function(e) {
			e.preventDefault();
			var $btn = $(e.currentTarget);

			var form = $('#recruiter-register-form');
			// form validateion
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
					fname: {
						required: true,
						maxlength: 45
					},
					lname: {
						required: true,
						maxlength: 45
					},
					email: {
						required: true,
						email: true,
						maxlength: 45
					},
					password: {
						required: true,
						minlength: 6,
						maxlength: 30
					},
					confirmPassword: {
						required: true,
						equalTo: "#inputPassword"
					},
					phone: {
						required: true,
						maxlength: 45
					},
					contactEmail: {
						required: true,
						email: true
					},
					title: {
						required: true,
					},
					companyName: {
						required: true,
						minlength: 3,
						maxlength: 255
					},
					companyAddress: {
						required: true,
						maxlength: 255
					},
					companySize: {
						required: true,
					},
					foundTime: {
						required: true,
						digits: true,

					},
					companyWebsite: {
						url: true
					},
					// companyLogo: {
					// 	accept: "image/*"
					// },
					// companyPic: {
					// 	accept: "image/*"
					// },
					description: {
						required: true,
						maxlength: 5000
					}

				},
				messages: {
					fname: {
						required: "First Name is required."
					},
					lname: {
						required: "Last Name is required."
					},
					email: {
						required: "Email Address is requied."
					},
					password: {
						rangelength: "Password must be between 6-30 characters and cannot include any spaces."
					},
					confirmPassword: {
						equalTo: "Password does not match."
					},
					companyName: {
						minlength: "Please enter the full name of your company."
					},
					foundTime: {
						rangelength: "Please enter 4 digits year."
					},
					companyLogo: {
						accept: "Please attach only image files."
					},
					companyPic: {
						accept: "Please attach only image files."
					}
				}
			});

			var data = new FormData(form[0]);
			data.delete('confirmPassword');
			var self = this;
			var url = '/api/recruiter/register';
			if (form.valid() === true) {
				// var data = form.serializeObject();

				$btn.text('Loading...').prop("disabled", true);

				$.ajax({
					url: url,
					method: "POST",
					contentType: false,
					processData: false,
					data: data,
					success: function(data) {
						console.log(data);
						if (data.metadata.error) {
							$btn.text('Loading...').prop("disabled", true);
							$('#feedback').text(data.metadata.error.error_message).removeClass('hidden');
						} else {
							$btn.text('Loading...').prop("disabled", true);
							var successView = new SuccessView({
								el: self.$(".container")
							});
							successView.render();
						}
					}
				});
			}
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
	});

	return RegisterView
});