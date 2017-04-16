directory.RecruiterLoginView = Backbone.View.extend({
	initialize: function() {
		// console.log('Initializing Recruiter Login View');
	},
	events: {
		'click #loginButton': 'login'
	},

	render: function() {
		this.$el.html(this.template());
		return this;
	},

	keepSignIn: function() {
		if ($('#keepSignIn').is(':checked')) {
			return true
		} else {
			return false
		}
	},
	login: function(e) {
		e.preventDefault();

		//disable button 
		var $btn = $(e.currentTarget);
		$btn.text('Loading...').prop("disabled", true);


		var formValues = {
			email: $('#inputEmail').val(),
			password: $('#inputPassword').val()
		};

		var url = "/api/recruiter/login";
		$.ajax({
			url: url,
			type: 'POST',
			dataType: "json",
			contentType: "application/json",
			data: JSON.stringify(formValues),
			processData: false,
			statusCode: {
				403: function() {
					//access denied
					window.location.hash = 'denied';
				}
			},
			success: function(data) {
				console.log(data);

				//enable button 
          		$btn.text('Login').prop("disabled", false);
				// console.log(data.recruiter);
				if (data.metadata.error) {
					console.log(data.metadata.error);
					$('#feedback').text(data.metadata.error).show();
					// alert(data.metadata.error);
				} else {
					//login successful
					//enable button 
          			$btn.text('Login').prop("disabled", false);
					if (this.keepSignIn) {
						$.cookie('access_token', data.metadata.session_token, {
							expires: 30,
							path: '/'
						});
						$.cookie('user_type', 'Recruiter', {
							expires: 30,
							path: '/'
						});
						$.cookie('id', data.recruiter.id, {
							expires: 30,
							path: '/'
						});
					} else {
						$.cookie('access_token', data.metadata.session_token);
						$.cookie('user_type', 'Recruiter');
						$.cookie('id', data.recruiter.id);
					}
					var id = data.recruiter.id;

					directory.recruiter = new directory.RecruiterProfile(data.recruiter);
					console.log(directory.recruiter);
					window.location.hash = 'recruiter/dashboard';
					window.location.reload();
				}
			}
		})
	}


});