directory.JobResult = Backbone.Model.extend({
	// parse: function(response){
	//    	return response.data;
	//    }
});
directory.JobRecruiter = Backbone.Collection.extend({
	model: directory.JobResult,
	parse: function(response) {
		return response.data;
	},
})
directory.JobResultCollection = Backbone.PageableCollection.extend({

	model: directory.JobResult,
	mode: "client",
	initialize: function() {
		this.position = '';
		this.city = '';
		this.subject = '';
		this.grade = '';
	},
	// url: '/api/job/guest/?position='+ this.position + '&city='+this.city+'&subject='+ this.subject + '&grade='+this.grade,
	url: '/api/job/guest/?position=&city=&subject=&grade=',
	parse: function(response) {
		return response.data;
	},
	state: {
		firstPage: 1,
		pageSize: 10,
	},
	queryParams: {
		sortKey: "postDate"
	}

});