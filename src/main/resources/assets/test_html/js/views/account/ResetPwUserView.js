directory.ResetPwUserView = Backbone.View.extend({
	initialize: function(attrs) {
		this.userType = attrs;
		console.log(this.userType);
	},
	render: function() {
		this.$el.html(this.template());
		return this;
	},

	events: {
		"blur #confirm": 'checkPw',
		"click #resetPwButton": function(e) {
			e.preventDefault();
			this.resetPw();
		}
	},
	valid: function() {
		var form = $('#resetPw-form');
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
				password: {
					required: true,
					minlength: 8
				},
				confirm: {
					required: true,
					minlength: 8,
					equalsTo: "#new-pw"
				}
			},
		});
		return (form.valid());
	},

	checkPw: function() {
		var self = this;
		$('#resetPwButton').attr('disabled', true);
		$('.alert-warning').addClass('hidden');
		var pw = $('input[name="password"]').val();
		var cpw = $('#confirm').val();
		if (pw.length < 8) {
			$('.alert-warning').html('Your password should be at least 8 characters.').removeClass('hidden');

		} else {
			if (cpw === pw) {
				$('#resetPwButton').removeAttr('disabled');
			} else {
				$('.alert-warning').removeClass('hidden');

			}
		}


	},
	resetPw: function() {
		var self = this;
		var form = $('#resetPw-form');
		var data = new FormData(form[0]);

		console.log(data);
		var url = '/api/' + self.userType + '/password/';
		$.ajax({
			url: url,
			method: "PUT",
			contentType: false,
			processData: false,
			data: data,
			success: function(data) {
				console.log(data);
				if (self.userType == 'candidate') {
				var link = "#logout/user"
			}else{
				var link = "#logout/recruiter"
			}
				if (data == "Same") {
					$('.alert-warning').html('Password is same with the old one').removeClass('hidden');
				} else {
					$('form').remove();
					$('div.tab-content').append('<p>Your password has been updated. <br> You will be redirected to login page in 3 seconds.\
						<a href="'+ link + '">Go to login page</a></p>');
					self.redirect();
				}

			}
		});

		$('#resetPwButton').removeAttr('disabled');
	},

	redirect: function() {
		if (this.userType == 'candidate') {
			window.setTimeout(function() {
				directory.router.navigate("logout/user", {trigger: true})
				// window.location.hash = '#login'
			}, 3000);
		}else{
			window.setTimeout(function() {
				directory.router.navigate("logout/recruiter", {trigger: true})
				// window.location.reload();
				// window.location.hash = '#recruiterLogin'
			}, 3000);
		}

	}
})