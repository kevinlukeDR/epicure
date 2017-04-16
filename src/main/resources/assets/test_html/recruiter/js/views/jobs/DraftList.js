define([
	'jquery',
	'underscore',
	'backbone',
	'util',
	'views/jobs/DraftDetailView'
], function($, _, Backbone, util,DraftDetailView) {
	var DraftList = Backbone.View.extend({
		tagName: 'tr',
		className:'draft',
		initialize: function() {
			var templateStr = window.util.loadTemplateSync("templates/Jobs/DraftList.html");
			this.template = _.template(templateStr);

			this.listenTo(this.model, "destroy", this.remove, this);
		},
		render: function() {
			this.$el.html(this.template(this.model.attributes));
			return this;
		},
		events: {
			'click #delete': 'delete',
			'click #edit': 'edit',
			'dblclick .draft':'edit'
		},
		edit: function(){
			console.log(this.model);
			var detail = new DraftDetailView({model: this.model});
			detail.render();
			Backbone.history.navigate("drafts/" + this.model.id);
		},
		delete: function() {
			var self = this;
			var r = confirm("Are you sure you want delete this draft?");
			if (r == true) {
				self.model.destroy();
			}
		},

	});
	return DraftList;
});