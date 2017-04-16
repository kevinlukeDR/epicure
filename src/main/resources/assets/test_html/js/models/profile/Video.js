directory.Video = Backbone.Model.extend({
	url: '/api/video/',
	parse: function(response){
    	return response.data.video;
    }
});