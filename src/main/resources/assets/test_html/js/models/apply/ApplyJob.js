directory.ApplyJob = Backbone.Model.extend({
	url: '/api/applystatus/apply',
	parse: function(response){
    	return response.data.details;
    }
});