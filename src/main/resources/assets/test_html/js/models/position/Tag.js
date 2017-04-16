directory.Tag = Backbone.Model.extend({
	url:"/api/job/tag/",
    parse: function(response){
    	return response.data;
    }
});