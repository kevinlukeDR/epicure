directory.SavedJobsView = Backbone.View.extend({
	initialize: function() {
		// console.log(this.collection)
		// this.listenTo(this.collection, "change", this.render, this);
	},
	render: function() {
		this.$el.html(this.template());

		if (this.collection.isEmpty()) {
			this.noJobs();
		} else {
			_.each(this.collection.models, function(job) {
				$('#saved-job-list', this.el).append(new directory.SavedJobListView({
					model: job
				}).render().el);
			}, this);
		}
		this.renderStrength();
		return this;
	},

	noJobs: function() {
		$('.no-jobs', this.el).removeClass('hidden');
	},
	renderStrength: function(){
        var strength = new directory.Strength();
        var self = this;
        strength.fetch({
            success: function(model, response){
                if (response.metadata.status == "ok") {
                    
                    $('#profile-strength', self.el).html(new directory.ProfileStrView({model: model}).render().el)
                }
            }
        })
        
    },
});

directory.SavedJobListView = Backbone.View.extend({
	initialize: function() {
		console.log(this.model);
		this.model.on("change", this.render, this);
		this.model.on("destroy", this.remove, this);
	},

	tagName: 'li',


	render: function() {
		this.$el.html(this.template(this.model.attributes));
		return this;
	},

	events: {
		'click #fav-button': 'unfav'
	},

	unfav: function(e) {
		var self = this;
		e.preventDefault();
		var $btn = $(e.currentTarget);
		$btn.addClass('unfav');
		// var url = '/api/favorite/' + this.model.attributes.
		setTimeout(function() {
			self.model.destroy();
		}, 3000);
	}
})