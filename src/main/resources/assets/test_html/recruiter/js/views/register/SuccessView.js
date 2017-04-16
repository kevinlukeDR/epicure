define([
	'jquery',
	'underscore',
	'backbone',
	'util'
], function($, _, Backbone, util) {
	var SuccessView = Backbone.View.extend({
		el: $("#login"),
		initialize: function() {
			var templateStr = window.util.loadTemplateSync("templates/Register/SuccessView.html");
			this.template = _.template(templateStr);
		},
		render: function() {
			this.$('#wrap').empty();
			this.$el.html(this.template());
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

			var email = this.model.get("email");
			console.log(email);
			$.ajax({
					url: '/api/candidate/resend/?email=' + email,
					type: 'GET'
				})
				// .done(function() {
			$('.alert-success').removeClass('hidden');
			window.util.startTimer(oneMinutes, display);
			window.setTimeout(function() {
				$('.alert-success').addClass('hidden');
				$btn.text('RESEND').prop("disabled", false);
			}, 60000);
			// });


		}
	})
	return SuccessView;
});