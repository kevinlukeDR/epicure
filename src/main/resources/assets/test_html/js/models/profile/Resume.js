directory.ResumeLatest = Backbone.Model.extend({
	url: '/api/resume/latest',
	parse: function(response){
		return response.data.latest
	}
})

directory.Resume = Backbone.Model.extend({

});

directory.ResumeCollection = Backbone.Collection.extend({
	model: directory.Resume,
	url: '/api/resume/',
	parse:  function(response){
		return response.data.resumes
    }

})