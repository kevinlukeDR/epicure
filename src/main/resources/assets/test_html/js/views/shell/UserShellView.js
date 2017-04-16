directory.UserShellView = Backbone.View.extend({
    initialize: function(){
       this.listenTo(this.model, "change", this.render, this);
       this.listenTo(this.model, "destroy", this.remove, this);
    },
    render: function() {
        // console.log(this.model);
        this.$el.html(this.template(this.model.attributes));
        return this;
    },

    selectMenuItem: function(menuItem) {
            $('.navbar .navbar-nav li').removeClass('active');
            if (menuItem) {
                $('.' + menuItem).addClass('active');
            }
     },
     noSelectMenu: function(){
        $('.navbar .navbar-nav li').removeClass('active');
     },
     destroy: function() {
        this.undelegateEvents();
        this.$el.removeData().unbind();
        this.remove();
    }
});

directory.UserDropDownView = Backbone.View.extend({
    initialize: function(){
       this.listenTo(this.model, "change", this.render, this);
       this.listenTo(this.model, "destroy", this.remove, this);
    },
    render: function() {
        // console.log(this.model);
        this.$el.html(this.template(this.model.attributes));
        return this;
    },
})