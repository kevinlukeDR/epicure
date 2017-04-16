directory.SimilarJobListView = Backbone.View.extend({
	tagName: 'li',
	className: 'position-list',

	template: _.template('<h4 class="position-title"><a href="#jobs/<%= id %>"><%= position %></a></h4><p class="position-detail">\
			<strong>Location: </strong><span><%= city %></span><br></p>'),

	initialize: function(){
		this.model.on('change',this.render,this);
		this.model.on('destroy',this.render,this);
		// console.log('initialize similar job list');

	},
	render: function() {
		var data = _.clone(this.model.attributes);
		data.id = this.model.id;
		this.$el.html(this.template(data));
		return this;
	},
})