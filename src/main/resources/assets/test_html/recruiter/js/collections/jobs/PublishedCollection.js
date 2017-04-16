define([
	'app',
	'models/jobs/Published'
], function(app, Published) {
	var PublishedCollection = Backbone.Collection.extend({
		url: '/api/job/publish/',
		model: Published,
		parse: function(response) {
			return response.data.jobs
		}
	})
	return PublishedCollection;
});