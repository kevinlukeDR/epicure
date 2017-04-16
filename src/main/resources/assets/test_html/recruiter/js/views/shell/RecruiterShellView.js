define([
	'jquery',
	'underscore',
	'backbone',
	'views/shell/SidebarView',
	'models/login/UserModel',
	'bootstrap'
], function($, _, Backbone, SidebarView, UserModel) {
	var self = this;
	var RecruiterShellView = Backbone.View.extend({
		el: $("#top"),
		initialize: function() {
			var templateStr = window.util.loadTemplateSync("templates/Shell/RecruiterShellView.html");
			this.template = _.template(templateStr);
			// this.model = new UserModel();
			// this.model.fetch();
			// console.log(this.model);
			this.model.on('change', this.render, this);
			this.listenTo(this.model, 'change', this.render);
		},
		render: function() {
			this.$el.html(this.template(this.model.attributes));
			//render sidebar
			// this.sidebar = new SidebarView({el: this.$("#sidebar-content")});
			this.sidebar = new SidebarView();
			this.sidebar.render();

			return this;
		},

		events: {
			'mouseover #shopping-cart': 'show'
		},
		destroy: function() {
			this.undelegateEvents();
			this.$el.removeData().unbind();
			this.remove();
		}
	})
	return RecruiterShellView;
});