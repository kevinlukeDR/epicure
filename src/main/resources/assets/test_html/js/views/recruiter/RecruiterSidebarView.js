directory.RecruiterSidebarView = Backbone.View.extend({
	tagName: 'div',

	className: 'row row-offcanvas row-offcanvas-left',
	initialize: function() {
		console.log('Initializing Recruiter Sidebar View');
	},
	render: function() {
		this.$el.html(this.template());
		return this;
	},
	selectMenuItem: function(menuItem) {
            $('.nav li').removeClass('active');
            if (menuItem) {
                $('.' + menuItem).addClass('active');
            }
     },
    noSelectMenu: function(){
        $('.nav li').removeClass('active');
     }
})