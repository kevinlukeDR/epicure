directory.JobTagView = Backbone.View.extend({
	initialize: function(attrs) {
		this.job_id = attrs;
	},

	render: function() {
		this.$el.html(this.template());
		
		// $('input[name="visa"]',this.el).tagsinput();
		this.renderTag();
		return this;
	},

	events: {
		"click #submitBtn": function(e) {
			e.preventDefault();
			this.submit();
		}
	},

	renderTag: function() {
		var ages = ['Below 30','30 to 40', '40 to 50', 'Above 50'];
		var nationalities = ['United States','Australia','Canada','UK','South Africa','New Zealand','Ireland','Others'];
		var ageName = new Bloodhound({
			  datumTokenizer: Bloodhound.tokenizers.whitespace,
			  queryTokenizer: Bloodhound.tokenizers.whitespace,
			  local: ages
			});
		var nName = new Bloodhound({
			  datumTokenizer: Bloodhound.tokenizers.whitespace,
			  queryTokenizer: Bloodhound.tokenizers.whitespace,
			  local: nationalities
		});

		ageName.initialize();
		nName.initialize();
		$('input[name="age"]', this.el).tagsinput({
			maxTags: 5,
			trimValue: true,
			typeaheadjs: {
				source:ageName.ttAdapter()
			},
			freeInput:false
		});
		$('input[name="nationalities"]', this.el).tagsinput({
			maxTags: 8,
			tagClass: 'label label-success',
			typeaheadjs: {
				source:nName.ttAdapter()
			},
			freeInput:false
		});
		$('input[name="visa"]', this.el).tagsinput({
			maxTags: 3,
			tagClass: 'label label-primary',
			typeahead: {
				source: ['Z-Visa','Travel Visa','No Visa']
			},
			freeInput:false
		});
	},

	submit: function() {
		var nationalities = $('input[name="nationalities"]').tagsinput('items');
		var visas = $('input[name="visa"]').tagsinput('items');
		var age = $('input[name="age"]').tagsinput('items');
		// console.log(JSON.stringify(nationalities));
		var formValues = {
			'job_id': this.job_id,
			'nationalities': JSON.parse(JSON.stringify(nationalities)),
			'visa': JSON.parse(JSON.stringify(visas)),
			'age': JSON.parse(JSON.stringify(age))
		};

		var tag = new directory.Tag(formValues);
		tag.save(null,{
			 success: function(model, response) {
			 	console.log(response);
			 	console.log(model);
               if (response.metadata.error) {
               		alert('error');
               }else{
               	alert('Success! Thank you!');
               	window.location.hash = 'recruiter/jobManagement';
               }
            }
		})
		
		
		
	}


})