define([
  'jquery',
  'underscore',
  'backbone',
  'util',
  'collections/jobs/ArchivedCollection',
  'views/jobs/ArchivedJobList',
  'jqueryDataTables'
], function($, _, Backbone,util,ArchivedCollection,ArchivedJobList,DataTable){
	var ArchivedJobsView = Backbone.View.extend({
		el: $("#content"),
		initialize: function() {
			var templateStr = window.util.loadTemplateSync("templates/Jobs/ArchivedJobsView.html");
			this.template = _.template(templateStr);
			this.collection = new ArchivedCollection();
		},
		render: function() {
			this.$el.html(this.template());
			var self = this;
			this.collection.fetch().done(function(){
				self.collection.each(function(archived){
					var list = new ArchivedJobList({model: archived});
					self.$('#archive-list', this.el).append(list.render().el)
				},this);

				$('#archive-table', self.el).DataTable();
			});

			
			return this;
		},
		destroy: function() {
			this.undelegateEvents();
			this.$el.removeData().unbind();
			this.remove();
		}
	});
  return ArchivedJobsView;
});
