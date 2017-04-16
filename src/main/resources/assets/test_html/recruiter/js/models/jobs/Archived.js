define([
	'app',
	'moment'
], function(app, moment) {
	var Archived = Backbone.Model.extend({
		urlRoot: '/api/job/close/',
		
		// toJSON: function() {
		// 	var json = Backbone.Model.prototype.toJSON.call(this);
		// 	json.startDate = moment(this.get('startDate'));
		// 	console.log(json.startDate);
		// 	return json;
		// }
	})
	return Archived;
});