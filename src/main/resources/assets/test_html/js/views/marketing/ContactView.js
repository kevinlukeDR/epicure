directory.ContactView = Backbone.View.extend({
	render: function() {
		this.$el.html(this.template());
		if ($.cookie('access_token')) {
			this.renderInfo();
		}
		return this;
	},
	events: {
		'click #send': 'send'
	},
	renderInfo: function() {
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
		$.ajax({
			url: "/api/profile",
			method: "GET",
			success: function(data) {
				var name = data.fname + " " + data.lname;
				console.log(name);
				self.$('#name', this.el).val(name);
			}
		});
	},
	send: function(e) {
		e.preventDefault();
		var form = $('#contactForm');
		var $btn = $(e.currentTarget);
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
				name: {
					maxlength: 30,
				},
				email: {
					required: true,
					email: true,
				},
				message: {
					maxlength: 255,
				}
			},
		});
		var data = new FormData(form[0]);
		if (form.valid() === true) {
			$btn.text('Sending...').prop('disabled', 'true');
			$.ajax({
				url: '/api/email/contactus',
				type: 'POST',
				contentType: false,
				processData: false,
				data: data,
				success: function(resp) {
					console.log(resp);
					if (resp.metadata.error) {
						alert(resp.metadata.error_message)
					} else {
						$btn.text('Send Message').prop('disabled', false);
						$('#success').show();
						window.setTimeout(function() {
							$('#success').fadeOut('slow');
						}, 7000)
					}
				}
			});
		}else{
			$btn.text('Send Message').prop('disabled', false);
		}
	}



})