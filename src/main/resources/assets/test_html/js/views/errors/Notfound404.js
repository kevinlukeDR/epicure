directory.Notfound404 = Backbone.View.extend({
	className: 'error',
	render: function() {
		this.$el.html(this.template());
		return this;
	},
	events: {
		'click #searchBtn': 'search'
	},
	search: function(e) {
		e.preventDefault();
		var position = $('#search').val();
		var query = "?position=" + position + '&city=&subject=&grade=';
		window.location.hash = "#search/" + encodeURIComponent(query);
	}
});