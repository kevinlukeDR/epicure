directory.ForgetPwView = Backbone.View.extend({
	initialize: function(attrs) {
		this.userType = attrs;
		console.log(this.userType);
	},
	render: function() {
		this.$el.html(this.template());
		$('input[name="type"]', this.el).val(this.userType);
		console.log($('input[name="type"]', this.el).val());
		return this;
	},

	events: {
		"click #resetPwButton": 'checkEmail'
	},


	checkEmail: function(e) {
		e.preventDefault();
		var $btn = $(e.currentTarget);
		var self = this;
		if (self.userType == "Candidate") {
			self.type = 'candidateLogin';
			self.msg = 'This Email has not been registered. <a href="#register">Sign Up</a> now!';
		} else {
			self.type = 'recruiter';
			self.msg = 'This Email has not been registered. <a href="mailto: recruiter@techoversea.com?Subject=I%20would%20love%20to%20post%20jobs">Sign Up</a> now!';
		}

		$('#resetPwButton').attr('disabled', true);
		$('.alert-warning').addClass('hidden');

		var email = $('input[name="email"]').val();
		if (email) {
			$.ajax({
				url: "/api/" + self.type + "/test/" + email
			}).done(function(data) {
				// console.log(data);
				if (data.metadata.error) {
					$btn.text('Loading').prop('disabled', true);
					self.resetPw();
				} else if (data.metadata.status == "ok") {
					$btn.text('Send Email').prop('disabled', false);
					$('.alert-warning').html(self.msg).removeClass('hidden');
				} else if (data.metadata.status == "import") {
					$btn.text('Send Email').prop('disabled', false);
					$('.alert-warning').html(self.msg).removeClass('hidden');
				}
			});
		}

		$btn.text('Send Email').prop('disabled', false);
	},

	resetPw: function() {
		var self = this;
		var email = $('input[name="email"]').val();
		var form = $('#forgetPw-form');
		var data = new FormData(form[0]);
		var url = '/api/email/password';
		$.ajax({
			url: url,
			method: "POST",
			contentType: false,
			processData: false,
			data: data,
			success: function() {
				// alert("success!")
				$('form').remove();
				$('div.tab-content').append('<p>We have sent you a link to ' + email +
					'. If you do not receive the email, please check to be sure this is the Email address you used to sign up your Teach Oversea account.</p><p>Page will be redirected to <a href="#login">Login Page</a></p>');
				window.setTimeout(function() {
					window.location.hash = 'login'
				}, 5000);

			}
		});

		$('#resetPwButton').text('Send Email').prop('disabled', false);
	},

	destroy: function() {
		this.undelegateEvents();
		this.$el.removeData().unbind();
		this.remove();
	}

})