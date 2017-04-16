directory.FeaturedJobsView = Backbone.View.extend({
	render: function() {
		var self = this;
		this.$el.html(this.template());

		_.each(this.model.models, function(jobresult) {
			$('#featured-content', self.el).append(new directory.FeaturedJobsListView({
				model: jobresult
			}).render().el);

		});
		// this.slickoption();
		// this.carouse();
		return this;
	},
	destroy: function() {
		this.undelegateEvents();
		this.$el.removeData().unbind();
		this.remove();
	},
	carouse: function() {
		// Instantiate the Bootstrap carousel
		$('.multi-item-carousel',this.el).carousel({
			interval: false
		});

		// for every slide in carousel, copy the next slide's item in the slide.
		// Do the same for the next, next item.
		$('.multi-item-carousel .item',this.el).each(function() {
			var next = $(this).next();
			if (!next.length) {
				next = $(this).siblings(':first');
			}
			next.children(':first-child').clone().appendTo($(this));

			if (next.next().length > 0) {
				next.next().children(':first-child').clone().appendTo($(this));
			} else {
				$(this).siblings(':first').children(':first-child').clone().appendTo($(this));
			}
		});
	},
	slickoption: function() {
		$('.autoplay', this.el).slick({
			centerMode: true,
			centerPadding: '60px',
			slidesToShow: 3,
			responsive: [{
				breakpoint: 768,
				settings: {
					arrows: false,
					centerMode: true,
					centerPadding: '40px',
					slidesToShow: 3
				}
			}, {
				breakpoint: 480,
				settings: {
					arrows: false,
					centerMode: true,
					centerPadding: '40px',
					slidesToShow: 1
				}
			}]
		});
	}
});

directory.FeaturedJobsListView = Backbone.View.extend({
	className: 'item',
	render: function() {
		this.$el.html(this.template(this.model.attributes));

		return this;
	},
})