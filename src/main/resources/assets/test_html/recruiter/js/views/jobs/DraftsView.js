define([
  'jquery',
  'underscore',
  'backbone',
  'util',
  'collections/jobs/DraftCollection',
  'views/jobs/DraftList',
  'jqueryDataTables'
], function($, _, Backbone,util, DraftCollection, DraftList,DataTable){
	var DraftsView = Backbone.View.extend({
		el: $("#content"),
		initialize: function() {
			var templateStr = window.util.loadTemplateSync("templates/Jobs/DraftsView.html");
			this.template = _.template(templateStr);
			this.collection = new DraftCollection();
		},
		render: function() {
			this.$el.html(this.template());
			var self = this;
			this.collection.fetch().done(function(){
				self.collection.each(function(draft){
					var draftlist = new DraftList({model: draft});
					self.$('#draft-list', this.el).append(draftlist.render().el)
				},this);

				$('#drafts-table', self.el).DataTable();
			});

			
			return this;
		},
		destroy: function() {
			this.undelegateEvents();
			this.$el.removeData().unbind();
			this.remove();
		}
	});
  return DraftsView;
});
