directory.SavedJobs = Backbone.Model.extend({
	urlRoot: '/api/job/favorite/',
	parse: function(response){
		return {
			id: response.jobId,
			jobName: response.jobName,
			date: response.date
		}
	}
})

// directory.JobDetails = directory.AppliedJob.extend({
// });
// directory.ApplyStatus =directory.AppliedJob.extend({

// });



directory.SavedJobsCollection = Backbone.Collection.extend({
	
    model: directory.SavedJobs,
    url: '/api/job/favorite',
 	parse: function(response){
    	return response.data.favored_jobs;
    }
});