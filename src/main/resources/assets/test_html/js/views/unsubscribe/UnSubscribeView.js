directory.UnSubscribeView = Backbone.View.extend({
	render: function() {
		this.$el.html(this.template());
		return this;
	},
	events: {
		"click #unsubscribeBtn": function(e) {
			e.preventDefault();
			this.unsubscribe();
		},

		"change input[type='radio']": 'radioChecked',
		"click #investBtn": function(e) {
			e.preventDefault();
			this.submitInvest();
		}
	},

	unsubscribe: function() {
		var form = $('#unsubscribe-form');
		var data = new FormData(form[0]);
		var url = '/api/email/unsubscribe/';
		form.validate({
			errorElement: 'span',
			errorClass: 'help-block',
			highlight: function(element, errorClass, validClass) {
				$(element).closest('.form-group').addClass("has-error");
			},
			unhighlight: function(element, errorClass, validClass) {
				$(element).closest('.form-group').removeClass("has-error");
			},
			email: {
				required: true,
				email: true,
				maxlength: 45
			},
		});

		if (form.valid()) {
		$.ajax({
			url: url,
			method: "POST",
			contentType: false,
			processData: false,
			data: data,
			success: function(data) {
				console.log(data);
				$('#unsubscribe-area').html('<div class="alert alert-success mt8">You have successfully unsubscribed.</div>');
				// $('.alert-success').removeClass('hidden');
				$('#reason').removeClass('hidden');
			}
		});
	}
	},

	radioChecked: function() {
		$('.unsubscribe-area').addClass('hidden');
		var name = '.' + $("input:checked").val();
		// console.log(name);
		$(name).removeClass('hidden');
	},
	submitInvest: function() {
		var checkVal = $('input:checked').val();
		var classN = '.' + checkVal;
		var value = $(classN).val();

		var data = new FormData();
		var text = value ? value : checkVal;
		text = checkVal + ":" + text;
		// console.log(text);
		data.append('reason', text);

		// console.log(data);
		var url = '/api/email/feedback';

		$.ajax({
			url: url,
			method: "POST",
			contentType: false,
			processData: false,
			data: data,
			success: function(data) {
				// console.log(data);
				if (data.metadata.error) {
					console.log(data.metadata.error.error_message)
				} else {
					$('#submitArea').html('<div class="alert alert-success mt8">Thank you for your feedback.</div>');
				}

			}
		});
	},

	destroy: function() {
		this.undelegateEvents();
		this.$el.removeData().unbind();
		this.remove();
	}
});