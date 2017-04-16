define([
  'jquery',
  'underscore',
  'backbone',
  'util',
  'collections/jobs/PublishedCollection',
  'views/jobs/PublishedJobList',
  'jqueryDataTables'
], function($, _, Backbone,util,PublishedCollection,PublishedJobList, DataTable){
	var PublishedJobsView = Backbone.View.extend({
		el: $("#content"),
		initialize: function() {
			var templateStr = window.util.loadTemplateSync("templates/Jobs/PublishedJobsView.html");
			this.template = _.template(templateStr);
			this.collection = new PublishedCollection();
		},

		render: function(){
			this.$el.html(this.template());
			var self = this;
			this.collection.fetch().done(function(){
				self.collection.each(function(publish){
					var publishlist = new PublishedJobList({model: publish});
					self.$('#published-list', this.el).append(publishlist.render().el)
				},this);

				$('#published-table', self.el).DataTable();
			});
			return this;
		},
	});
  return PublishedJobsView;
});
