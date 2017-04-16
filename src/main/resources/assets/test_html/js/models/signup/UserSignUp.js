directory.UserSignUp = Backbone.Model.extend({
	url: "/api/candidate/insert",

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
			minLength: 6,
			msg: 'Password is not strong enough.'
		}
	}
});