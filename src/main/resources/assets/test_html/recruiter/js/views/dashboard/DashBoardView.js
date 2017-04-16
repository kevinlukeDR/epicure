define([
	'jquery',
	'underscore',
	'backbone',
	'util'
], function($, _, Backbone, util) {
	var DashBoardView = Backbone.View.extend({
		el: $("#content"),
		initialize: function() {
			var templateStr = window.util.loadTemplateSync("templates/DashBoard/DashBoardView.html");
			this.template = _.template(templateStr);
		},
		render: function() {
			this.$el.html(this.template());
			return this;
		},
		destroy: function() {
			this.undelegateEvents();
			this.$el.removeData().unbind();
			this.remove();
		}
	})
	return DashBoardView;
});