define([
	'app',
	'moment'
], function(app, moment) {
	var Draft = Backbone.Model.extend({
		urlRoot: '/api/job/draft/',

		toJSON: function() {
			var json = Backbone.Model.prototype.toJSON.call(this);
			json.startDate = moment(this.get('startDate'));
			console.log(json.startDate);
			return json;
		},

		parse: function(resp) {
			// var attr = resp && _.clone(resp) || {};
			if (resp.data) {
				var attr = resp.data.jobs && _.clone(resp.data.jobs) || {};
			}else{
				var attr = resp && _.clone(resp) || {};
			}

			if (attr.startDate) {
				attr.startDate = new Date(attr.startDate).toISOString().slice(0, 10);
			}
			return attr;
		},
		fetch: function(options) {
			var opts = _.extend({
				url: '/api/job/' + this.id
			}, options || {});
			return Backbone.Model.prototype.fetch.call(this, opts);
		}

	})
	return Draft;
});