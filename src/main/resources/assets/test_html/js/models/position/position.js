//This is the model for each position (jobDetails)
directory.Position = Backbone.Model.extend({
	urlRoot:"/api/job/getbyid/",
	initialize: function(){
		this.similar = new directory.SimilarCollection();
		this.similar.url = "/api/job/relevant/" + this.id ;
	},


    parse: function(response){
    	return response.data[0];
    }
});

directory.Similar = Backbone.Model.extend({
});

directory.SimilarCollection = Backbone.Collection.extend({
	model: directory.Similar,
	// url: "/api/job/getbyid/",
	parse: function(response){
		return response.data.jobs
	}

})