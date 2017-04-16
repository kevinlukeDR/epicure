directory.ApplySuccessView = Backbone.View.extend({

	initialize: function(attr) {
		this.job_id = attr;
	},

	render: function() {
		this.$el.html(this.template());
		this.similarJobs();
		return this;
	},
	similarJobs: function() {
		var similar = new directory.SimilarCollection();
		similar.url = "/api/job/relevant/" + this.job_id;

		var self = this;
		similar.fetch({
			success: function(data) {
				// alert('success');
				if (data.length == 0) {
					$('.no-similar', this.el).removeClass('hidden');
				} else {
					_.each(data.models, function(job) {
						$('#similar-job', this.el).append(new directory.SimilarJobListView({
							model: job
						}).render().el);
					}, this);
				}
			}
		});
	},

	destroy: function() {
		this.undelegateEvents();
		this.$el.removeData().unbind();
		this.remove();
	}

})