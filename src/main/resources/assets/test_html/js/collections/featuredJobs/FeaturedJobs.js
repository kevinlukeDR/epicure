directory.FeaturedJobs = Backbone.Model.extend({
})



directory.FeaturedJobsCollection = Backbone.Collection.extend({
	
    model: directory.FeaturedJobs,
    url: '/api/job/featured',
 	parse: function(response){
    	return response.data.featured;
    }
});