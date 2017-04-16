define([
  'jquery',
  'underscore',
  'backbone',
  'util'
], function($, _, Backbone,util){

  return {};
});


define([
  'jquery',
  'underscore',
  'backbone',
  'util'
], function($, _, Backbone,util){
	var View = Backbone.View.extend({
		el: $("#content"),
		initialize: function() {
			var templateStr = window.util.loadTemplateSync("templates/");
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
	});
  return View;
});
