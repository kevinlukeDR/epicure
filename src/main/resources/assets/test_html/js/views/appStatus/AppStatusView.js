directory.AppStatusView = Backbone.View.extend({
	initialize: function(){
        this.model.on("change", this.render, this);
        // if(this.model.length == 0){
        //     $('.no-application',this.el).removeClass('hidden');
        // }
    },

    render: function(){
    	this.$el.html(this.template(this.model.attributes));
        if(this.model.length == 0){
            $('.no-application',this.el).removeClass('hidden');
        };
    	_.each(this.model.models, function(appliedjob) {
            $('#applied-job-list', this.el).append(new directory.AppStatusListView({
                model: appliedjob
            }).render().el);

        }, this);
        this.renderStrength();
    	return this;
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


directory.AppStatusListView = Backbone.View.extend({
	initialize: function(){
        this.model.on("change", this.render, this);
    },
    tagName: 'li',
    render: function() {
        this.$el.html(this.template(this.model.attributes));
        return this;
    }
})