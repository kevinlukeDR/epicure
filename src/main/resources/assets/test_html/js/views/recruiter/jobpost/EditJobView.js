directory.EditJobView = Backbone.View.extend({
	initialize: function(attrs) {
		this.job_id = this.model.attributes.id;
		this.action = this.attributes;
		console.log(this.action);
	},

	render: function() {
		this.$el.html(this.template(this.model.attributes));
		if (this.action == "duplicate") {
			$('#postJobBtn', this.el).addClass('hidden');
			$('#saveNewBtn', this.el).removeClass('hidden');
		};
		$('.date-input', this.el).datepicker();
		$('#salary', this.el).popover();
		$('select[name="subject"]', this.el).val(this.model.attributes.subject);
		$('select[name="eligibleCandidates"]', this.el).val(this.model.attributes.eligibleCandidates.toString());
		$('select[name="classSize"]', this.el).val(this.model.attributes.classSize);
		$('select[name="grade"]', this.el).val(this.model.attributes.grade);
		$('select[name="jobType"]', this.el).val(this.model.attributes.jobType);
		$('select[name="nativeSpeaker"]', this.el).val(this.model.attributes.nativeSpeaker.toString());
		$('select[name="education"]', this.el).val(this.model.attributes.education);
		$('select[name="experience"]', this.el).val(this.model.attributes.experience);
		$('select[name="lunch"]', this.el).val(this.model.attributes.lunch.toString());
		$('select[name="chinese"]', this.el).val(this.model.attributes.chinese.toString());
		$('select[name="schoolType"]', this.el).val(this.model.attributes.schoolType);

		// console.log(this.model);
		return this;
	},

	events: {
		"click #saveNewBtn": function(e) {
			e.preventDefault();
			this.newJob();
		},
		'keyup #additional':'limit',
		'keyup #aboutSchool': 'limitLength',
		"click #postJobBtn": "postJob"
	},
	limit: function(){
		var length = $( '#additional',this.el).val().length;
        var maxLength = 1000;
        var length = maxLength - length;
        
        if (length < 0) {
            $('#chars2', this.el).addClass('red-text');
            $('#chars2', this.el).text(length)
        }else{
            $('#chars2', this.el).removeClass('red-text').text(length);
        }
	},
	limitLength: function(){
        var length = $('#aboutSchool',this.el).val().length;
        var maxLength = 5000;
        var length = maxLength - length;
        
        if (length < 0) {
            $('#chars', this.el).addClass('red-text');
            $('#chars', this.el).text(length)
        }else{
            $('#chars', this.el).removeClass('red-text').text(length);
        }
    },

	newJob: function() {
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

	postJob: function(event) {
		event.preventDefault();
		var self = this;
		var form = $('#job-post');
		var data = new FormData(form[0]);

		var url = "/api/job/update/";
		$.ajax({
			url: url,
			method: "PUT",
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

					window.location.hash = 'recruiter/postJob/' + self.job_id;

				}
			}
		});
	}
});