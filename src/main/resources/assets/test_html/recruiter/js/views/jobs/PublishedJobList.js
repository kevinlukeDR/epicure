define([
	'jquery',
	'underscore',
	'backbone',
	'util',
	'views/jobs/PublishedJobDetailView'
], function($, _, Backbone, util,DraftDetailView) {
	var PublishedJobList = Backbone.View.extend({
		tagName: 'tr',
		initialize: function() {
			var templateStr = window.util.loadTemplateSync("templates/Jobs/PublishedJobList.html");
			this.template = _.template(templateStr);

			this.listenTo(this.model, "destroy", this.remove, this);
		},
		render: function() {
			this.$el.html(this.template(this.model.attributes));
			// console.log(this.model);
			// this.model.id = this.model.attributes.details.id;
			return this;
		},
		events: {
			'click #edit': 'edit',
			'click #close': 'close',
		},
		edit: function(){
			console.log(this.model);
			var detail = new PulishedJobDetailView({model: this.model});
			detail.render();
			Backbone.history.navigate("published/" + this.model.id);
		},
		close: function(e){
			e.preventDefault();
			var self = this;
			var r = confirm('Are you sure you want to close ths job? This action cannot be undo.');
			if (r == true) {
				var url = '/api/job/close/' + self.model.id;
				$.ajax({
					url: url,
					type: 'PUT',
					success: function(resp){
						if (resp.metadata.status == 'ok') {
							console.log('success');
							self.model.destroy();
						}
					}
				});
				
			}
		}

	});
	return PublishedJobList;
});