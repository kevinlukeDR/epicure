//get all candidates in the pool
define([
	"app",
	"models/candidate/CandidateModel"
], function(app, CandidateModel) {

	var CandidateCollection = Backbone.Collection.extend({
		url: '/api/recruiter/all',
		model: CandidateModel,
		parse: function(response) {
			return response.data.candidates;
		}
	});

	return CandidateCollection;
});