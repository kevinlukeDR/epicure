directory.AppliedJob = Backbone.Model.extend({
})

// directory.JobDetails = directory.AppliedJob.extend({
// });
// directory.ApplyStatus =directory.AppliedJob.extend({

// });



directory.AppliedJobCollection = Backbone.Collection.extend({
	
    model: directory.AppliedJob,
    url: '/api/applystatus/getjobs/',
 	parse: function(response){
    	return response.data.jobs;
    }
});