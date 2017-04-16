directory.Strength = Backbone.Model.extend({
	url: '/api/profile/strength/',
	parse: function(response){
    	return response.data
    }
});