directory.ManageJobView = Backbone.View.extend({
    initialize: function() {
    this.model.on('change', this.render, this);
    },

    render: function() {
        this.$el.html(this.template(this.model.attributes));
        console.log(this.model);
        _.each(this.model.models, function(jobresult) {
            $('#manage-job-list', this.el).append(new directory.ManageJobList({
                model: jobresult
            }).render().el);
        }, this);
        $('#manage-job-table', this.el).DataTable();
        return this;
    }
});

directory.ManageJobList = Backbone.View.extend({
    initialize: function() {
        console.log("initialize job list view");
        this.model.on('change', this.render, this);
    },
    tagName: 'tr',
    render: function() {
        this.$el.html(this.template(this.model.attributes));
        return this;
    },
    events: {
        "click #discardJob": "closeJob"
    },

    closeJob: function() {
        var url = '/api/job/close/' + this.model.id;
        var r = confirm("Are you sure you want to close this job?");
        if (r == true) {
            $.ajax({
                url: url,
                method: "PUT",
                contentType: false,
                processData: false,
                success: function(data) {
                    console.log(data);
                    alert('Your job has been closed.');
                }
            });
        }
    }
})