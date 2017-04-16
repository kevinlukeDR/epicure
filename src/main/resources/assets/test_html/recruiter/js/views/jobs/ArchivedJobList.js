define([
	'jquery',
	'underscore',
	'backbone',
	'util',
], function($, _, Backbone, util) {
	var ArchivedJobList = Backbone.View.extend({
		tagName: 'tr',
		initialize: function() {
			var templateStr = window.util.loadTemplateSync("templates/Jobs/ArchivedJobList.html");
			this.template = _.template(templateStr);

		},
		render: function() {
			this.$el.html(this.template(this.model.attributes));
			return this;
		},
		// events: {
		// 	'click #delete': 'delete',
		// 	'click #edit': 'edit'
		// },

		

	});
	return ArchivedJobList;
});