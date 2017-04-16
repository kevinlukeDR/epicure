// 	events:{
// 		'click .fa-heart-1': "favIt", 
// 		'click .fa-heart-2': "favIt2", 
// 	},

// 	favIt: function (){
// 		$('.fa-heart-1').toggleClass('fav');
// 	}

// })

directory.CandidateView = Backbone.View.extend({
    initialize: function() {},

    render: function() {
        this.$el.html(this.template(this.model.attributes));
        console.log(this.model);
        _.each(this.model.models, function(jobresult) {
            $('#candidate-list', this.el).append(new directory.CandidateListView({
                model: jobresult
            }).render().el);

        }, this);
        // _.each(this.model.models, function(jobresult) {
        //     $('#resultsContainer', this.el).append(new directory.CandidateDetailView({
        //         model: jobresult
        //     }).render().el);
        // }, this);
        $('#candidate-table', this.el).DataTable();
        return this;
    }
});

directory.CandidateListView = Backbone.View.extend({
    // initialize: function(){
    //     console.log("initialize job list view");
    // },
    tagName: 'tr',
    render: function() {
        this.$el.html(this.template(this.model.attributes));
        return this;
    },

    events: {
        'click #viewDetailButton': 'getResume',
        'click #viewDetailLink': 'getResume'
    },

    getResume: function() {
        var modalview = new directory.CandidateDetailView({
            model: this.model
        });
        $('.get-resume').html(modalview.render().el);
    }
});

directory.CandidateDetailView = Backbone.Modal.extend({
    cancelEl: '.cancel',
    events: {
        'click .fa-heart': "favIt",
    },
    favIt: function() {
        $('.fa-heart-1').toggleClass('fav');
    }
})