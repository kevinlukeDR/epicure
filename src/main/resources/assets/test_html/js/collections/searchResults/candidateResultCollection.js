directory.CandidateResult = Backbone.Model.extend({
	// parse: function(response){
 //    	return response.data;
 //    }
});

directory.CandidateResultCollection = Backbone.Collection.extend({
	
    model: directory.CandidateResult,

    initialize: function() {
    }
    // parse: function(response){
    // 	return response.data;
    // }
})