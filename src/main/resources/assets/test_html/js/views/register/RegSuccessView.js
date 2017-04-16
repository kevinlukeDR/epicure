directory.RegSuccessView = Backbone.View.extend({
	initialize: function() {
		console.log('Register Success!');
	},

	render: function() {
		this.$el.html(this.template(this.model.attributes));
		return this;
	},
	events: {
		'click #resend':'resend'
	},

	resend: function(e) {
		e.preventDefault();
		$('#time').text('01:00');
		var $btn = $(e.currentTarget);
		$btn.text('Waiting...').prop("disabled", true);
		var oneMinutes = 60,
			display = $('#time');

		var email = this.model.get("email");
		console.log(email);
		$.ajax({
			url: '/api/candidate/resend/?email=' + email,
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


	}
})