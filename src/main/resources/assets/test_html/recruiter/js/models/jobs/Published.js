define([
	'app',
	'moment'
], function(app, moment) {
	var Published = Backbone.Model.extend({
		urlRoot: '/api/job/publish/',
		defaults: {
			'lunch': false,
			'chinese': false
		},
		toJSON: function() {
			var json = Backbone.Model.prototype.toJSON.call(this);
			json.startDate = moment(this.get('startDate'));
			console.log(json.startDate);
			return json;
		},
		parse: function(resp) {
			if (resp.data) {
				var attr = resp.data.job && _.clone(resp.data.job) || {};
			}else{
				var attr = resp && _.clone(resp) || {};
			}
			
			if (attr.details) {
				for (var key in attr.details) {
					if (attr.details.hasOwnProperty(key)) {
						attr[key] = attr.details[key]
					}
				}
				delete attr.details;
			}
			return attr;
		},
		fetch: function(options) {
			var opts = _.extend({
				url: '/api/job/' + this.id
			}, options || {});
			return Backbone.Model.prototype.fetch.call(this, opts);
		}

		
	});
	return Published;
});