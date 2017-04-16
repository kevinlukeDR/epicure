directory.MeetTeacherView = Backbone.View.extend({
	render: function() {
        this.$el.html(this.template());
        this.loadArticle();
        return this;
    },

    loadArticle: function(){
    	$('#article',this.el).load('docu/Nicholas.html');
    }
})