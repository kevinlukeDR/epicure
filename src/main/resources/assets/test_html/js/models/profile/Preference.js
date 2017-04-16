directory.Preference = Backbone.Model.extend({
	url:"/api/profile/tag",
	defaults:{
		"location":["Shenzhen"],
		"job_type":["fulltime","parttime","online"],
		"subject":["English"],
		"age_group":["0-6","7-12","13-15","16-18","Higher-Ed","Professional"],
		"school_type":["public","private","language-institute","international-school","university"],
		"contract_length":"25"
	},
    parse: function(response){
    	return response.data.tags;
    }
});