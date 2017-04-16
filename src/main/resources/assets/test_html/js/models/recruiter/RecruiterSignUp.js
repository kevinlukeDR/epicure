directory.RecruiterSignUp = Backbone.Model.extend({
	url: "/api/recruiter/insert/",

	validation: {
		fname: {
			required: true
		},
		lname: {
			required: true
		},

		email: {
			required: true,
			pattern: 'email'
		},
		password:{
			minLength: 8,
			msg: 'Password is not strong enough.'
		},
		phone:{
			required: true
		},
		contactEmail:{
			required: true,
			pattern: 'email'
		},
		title:{
			required: true
		},
		companyName:{
			required: true
		},
		companyAddress:{
			required: true
		},
		companySize:{
			required: true
		},
		foundTime:{
			required: true,
			pattern: 'number',
			length: 4
		},
		companyWebsite:{
			pattern: 'url'
		},
		description: {
			required: true
		}

	}
});