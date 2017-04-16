define([
	'app',
	'models/jobs/Archived'
], function(app, Archived) {
	var ArchivedCollection = Backbone.Collection.extend({
		url: '/api/job/close/',
		model: Archived,
		parse: function(response){
			return response.data.jobs
		}
	})
	return ArchivedCollection;
});