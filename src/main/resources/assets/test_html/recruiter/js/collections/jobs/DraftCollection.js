define([
	'app',
	'models/jobs/Draft'
], function(app, Draft) {
	var DraftCollection= Backbone.Collection.extend({
		url: '/api/job/draft/',
		model: Draft,
		parse: function(response){
			return response.data.jobs
		}
	})
	return DraftCollection;
});