directory.ChangeEmailView = Backbone.View.extend({

	render: function() {
		this.$el.html(this.template());
		return this;
	},
	events: {
		'blur #new-email': 'checkEmail',
		'click #submitButton': "submit",
	},
	checkEmail: function() {
		$('.email .glyphicon').remove();
		$('#email-error').remove();
		$('div.email').removeClass("has-success");
		$('div.email').removeClass("has-error");


		var email = $('#new-email').val();
		var valid = false;
		if (email) {
		$.ajax({
				url: "/api/candidateLogin/test/" + email
			})
			.done(function(data) {
				console.log(data);
				 if (data.metadata.status == "ok") {
					$('div.email').addClass("has-success");
					$('div.email').append("<span class='glyphicon glyphicon-ok form-control-feedback' aria-hidden='true'></span>");
					$("#submitButton").prop("disabled", false);
					valid = true;
				} else {
					$('div.email').addClass("has-error");
					$('div.email').append("<span class='glyphicon glyphicon-remove form-control-feedback' aria-hidden='true'></span>");
					$('div.email').append('<span class="help-block" id="email-error">Sorry this email has been registered.</span>')
					$("#submitButton").prop("disabled", true);
					valid = false;
				}
			});
			}
		return valid;
	},


	valid: function() {
		var form = $('#resetEmail-form');
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
				email: {
					required: true,
					email: true,
				},
				confirm: {
					required: true,
					email: true,
					equalTo: "#new-email"
				}
			},
			messages: {
				confirm: {
					equalTo: "Email not match"
				}
			}
		});
		return (form.valid());
	},


	submit: function(e) {
		e.preventDefault();
		var email = $('#new-email').val();
		// console.log (this.checkEmail());
		var self = this;
		// var valid = (this.valid()) && (this.checkEmail());
		// console.log(valid);
		if (this.valid()) {
			var $btn = $(e.currentTarget);
			$btn.text('Loading...').prop("disabled", true);
			var data = new FormData();
			data.append('email', email);

			var url = '/api/candidate/changeemail';
			var link = "changeEmailSuccess/" + email;
			$.ajax({
				url: url,
				method: "PUT",
				contentType: false,
				processData: false,
				data: data,
				success: function(data) {
					console.log(data);
					if (data == "Same") {
						$btn.text('Submit').prop("disabled", false);
						$('.alert-danger').html('Email address is same with the old one').removeClass('hidden');
					} else {
						$.cookie('access_token', "");
						$.cookie('user_type', "");

						directory.router.navigate(link, {
							trigger: true
						});
						window.location.reload();
					}
				}
			})
		} else {
			alert("error")
		}
	},

});

directory.ChangeEmailSuccessView = Backbone.View.extend({
	initialize: function(attr) {
		this.email = attr;
	},
	render: function() {
		this.$el.html(this.template());
		$('.mt8', this.el).text('We have sent you a confirmation message to ' + this.email);
		return this;
	},

	events: {
		'click #resend': 'resend'
	},

	resend: function(e) {
		e.preventDefault();
		$('#time').text('01:00');
		var $btn = $(e.currentTarget);
		$btn.text('Waiting...').prop("disabled", true);

		var oneMinutes = 60,
			display = $('#time');

		var self = this;
		$.ajax({
			url: '/api/candidate/resend/?email=' + self.email,
			type: 'GET'
		})
		// .done(function() {
			$('.alert-success').removeClass('hidden');
			directory.startTimer(oneMinutes, display);
			window.setTimeout(function() {
				$('.alert-success').addClass('hidden');
				$btn.text('RESEND').prop("disabled", false);
			}, 60000);

		// });


	},


})