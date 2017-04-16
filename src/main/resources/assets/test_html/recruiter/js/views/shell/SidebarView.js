define([
	'jquery',
	'underscore',
	'backbone',
	'metisMenu',
	'bootstrap',

], function($, _, Backbone,metisMenu) {
	var SidebarView = Backbone.View.extend({
		// className: 'row row-offcanvas row-offcanvas-left',
		el:  $("#left"),
		initialize: function() {
			var templateStr = window.util.loadTemplateSync("templates/Shell/SidebarView.html");
			this.template = _.template(templateStr);
		},
		render: function() {
			this.$el.html(this.template());
			$('#menu', this.el).metisMenu();
			return this;
		},
		events: {
			'click [data-toggle=offcanvas]': 'sidebarCollapse'
		},
		sidebarCollapse: function() {
			$('.row-offcanvas').toggleClass('active');
			$('.collapse').toggleClass('in').toggleClass('hidden-xs').toggleClass('visible-xs');
		},
		selectMenuItem: function(menuItem) {
			$('.nav li').removeClass('active');
			if (menuItem) {
				$('.' + menuItem).addClass('active');
			}
		},
		noSelectMenu: function() {
			$('.nav li').removeClass('active');
		}
	})
	return SidebarView;
});