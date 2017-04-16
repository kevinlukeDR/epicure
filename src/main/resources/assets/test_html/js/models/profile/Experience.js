directory.Experience = Backbone.Model.extend({
	url: '/api/profile/relevant/',

	parse: function(resp, xhr) {
		return {
			id: resp.data.relavantEx.id,
			company: resp.data.relavantEx.company,
			fromDate: resp.data.relavantEx.fromDate,
			toDate: resp.data.relavantEx.toDate,
			role: resp.data.relavantEx.role,
			responsibility: resp.data.relavantEx.responsibility
		}

	}
});

directory.ExperienceCollection = Backbone.Collection.extend({
	model: directory.Experience,

	parse: function(response) {
		return response.data;
	}
})