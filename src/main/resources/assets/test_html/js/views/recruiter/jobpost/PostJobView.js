directory.PostJobView = Backbone.View.extend({
	initialize: function(attrs) {
		this.id = attrs;
	},
	render: function() {
		this.$el.html(this.template(this.model.attributes));
		$('.date-input', this.el).datepicker();
		// $('#salary', this.el).popover();
		return this;
	},

	events: {
		'click #saveBtn':'saveNow',
		"click #postJobBtn": "postJob",
		'keyup #additional': 'limit',
		'keyup #aboutSchool': 'limitLength',
	},

	limit: function() {
		var length = $('#additional', this.el).val().length;
		var maxLength = 1000;
		var length = maxLength - length;

		if (length < 0) {
			$('#chars2', this.el).addClass('red-text');
			$('#chars2', this.el).text(length)
		} else {
			$('#chars2', this.el).removeClass('red-text').text(length);
		}
	},
	limitLength: function() {
		var length = $('#aboutSchool', this.el).val().length;
		var maxLength = 5000;
		var length = maxLength - length;

		if (length < 0) {
			$('#chars', this.el).addClass('red-text');
			$('#chars', this.el).text(length)
		} else {
			$('#chars', this.el).removeClass('red-text').text(length);
		}
	},
	postJob: function(event) {
		event.preventDefault();
		var self = this;
		var form = $('#job-post');
		var data = new FormData(form[0]);

		var url = "/api/job/insert/";


		$.ajax({
			url: url,
			method: "POST",
			contentType: false,
			processData: false,
			data: data,
			statusCode: {
				403: function() {
					//access denied
					window.location.hash = 'denied';
				}
			},
			success: function(data) {
				console.log(data);
				// alert("Post Job Success!");
				if (data.error) { // If there is an error, show the error messages
					$('#feedback').text(data.error.text).show();
				} else {
					var job_id = data;

					window.location.hash = 'recruiter/postJob/' + job_id;

				}
			}
		});
	},
	saveNow: function(event) {
		event.preventDefault();
		var self = this;
		var form = $('#job-post');
		var data = new FormData(form[0]);

		var url = "/api/job/insert/";


		$.ajax({
			url: url,
			method: "POST",
			contentType: false,
			processData: false,
			data: data,
			statusCode: {
				403: function() {
					//access denied
					window.location.hash = 'denied';
				}
			},
			success: function(data) {
				console.log(data);
				// alert("Post Job Success!");
				if (data.error) { // If there is an error, show the error messages
					$('#feedback').text(data.error.text).show();
				} else {
					alert('saved');

					// window.location.hash = 'recruiter/postJob/' + job_id;

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