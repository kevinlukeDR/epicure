directory.ResetPwView = Backbone.View.extend({
	initialize: function(attrs1, attrs2) {
		this.userType = attrs1;
		this.uuid = attrs2;
        var url = 'api/email/passwordRe?uuid='+ this.uuid+"&type="+ this.userType;
        $.ajax({
            url: url,
            type: 'POST',
            processData: false,

            statusCode: {
                404: function() {
                    window.location.hash = "#404";
                }
            },
        })
            .done(function() {
                console.log("success");
            })
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

	checkPw: function() {
		var self = this;
		$('#resetPwButton').attr('disabled', true);
		$('.alert-warning').addClass('hidden');
		var pw = $('input[name="password"]').val();
		var cpw = $('#confirm').val();
		if (pw.length < 8) {
			$('.alert-warning').html('Your password should be at least 6 characters.').removeClass('hidden');

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
		data.append('type', self.userType);
		data.append('uuid', self.uuid);

		console.log(data);
		var url = '/api/email/password';
		$.ajax({
			url: url,
			method: "PUT",
			contentType: false,
			processData: false,
			data: data,
			success: function(data) {
				console.log(data);
				if (data == "Same") {
					$('.alert-warning').html('Password is same with the old one').removeClass('hidden');
				} else {
					$('form').remove();
					$('div.tab-content').append('<p>Your password has been updated</p><p>Page will be redirected to <a href="#login">Login Page</a></p>');
					window.setTimeout(function() {
						window.location.hash = 'login'
					}, 3000);
				}

			}
		});

		$('#resetPwButton').removeAttr('disabled');
	}


});