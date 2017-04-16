define([
	'jquery',
	'underscore',
	'backbone',
	'util',
	'jqueryDataTables'
], function($, _, Backbone, util, DataTable) {
	var PoolListView = Backbone.View.extend({
		initialize: function() {
			var templateStr = window.util.loadTemplateSync("templates/Pool/PoolListView.html");
			this.template = _.template(templateStr);
		},

		tagName: 'tr',
		render: function() {
			this.$el.html(this.template(this.model.attributes));
			return this;
		},
		events: {
			'click .details-control': 'showDetails'
		},

	})
	return PoolListView;
});