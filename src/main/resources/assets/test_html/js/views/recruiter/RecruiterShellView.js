directory.RecruiterShellView = Backbone.View.extend({
    id: 'top',
    initialize: function(){
        this.model.on('change', this.render, this);
        this.listenTo(this.model,'change',this.render);
        this.model.fetch();
    },
    render: function() {
        this.$el.html(this.template(this.model.attributes));
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