directory.JobBoardView = Backbone.View.extend({
    initialize: function(attr) {
        this.query = attr;
        this.url = '/api/job/guest' + this.query;
    },

    render: function() {
        var self = this;
        this.$el.html(this.template());
        this.renderFirstPage();

        //     _.each(firstpage, function(jobresult) {
        //     $('#job-list', self.el).append(new directory.JobListView({
        //         model: jobresult
        //     }).render().el);
        // }, self);


        return this;
    },
    events: {
        'click .next': 'getNextPage',
        'click .previous': 'getPreviousPage',
        'click .page': 'goto',
        'change #sortFilter': 'sort'
    },
    renderFirstPage: function() {
        var self = this;

        this.results = new directory.JobResultCollection();
        this.results.url = this.url;
        this.results.setSorting('postDate',1);
        this.results.fetch().done(function() {
            self.renderPag(self.results.state.totalPages);
            _.each(self.results.models, function(jobresult) {
                $('#job-list', self.el).append(new directory.JobListView({
                    model: jobresult
                }).render().el);
            }, self);
        })
        // $('.li-pre', this.el).addClass('disabled');

    },

    sort: function(){
        var filter = $('#sortFilter').val();
        var self = this;
        console.log(filter);
         $('#job-list').empty();
        this.results.setSorting(filter,1);
        this.results.fetch().done(function() {
            self.renderPag(self.results.state.totalPages);
            _.each(self.results.models, function(jobresult) {
                $('#job-list', self.el).append(new directory.JobListView({
                    model: jobresult
                }).render().el);
            }, self);
        })
    },
    getNextPage: function(e) {
        e.preventDefault();

        $('#job-list').empty();
        this.results.getNextPage();
        console.log('this page is ' + this.results);


        _.each(this.results.models, function(jobresult) {
            $('#job-list').append(new directory.JobListView({
                model: jobresult
            }).render().el);
        }, self);
        this.check(this.results);
    },
    getPreviousPage: function(e) {
        e.preventDefault();

        $('#job-list').empty();
        this.results = this.results.getPreviousPage();
        console.log('this page is ' + this.results);
        _.each(this.results.models, function(jobresult) {
            $('#job-list').append(new directory.JobListView({
                model: jobresult
            }).render().el);
        }, self);
        this.check(this.results);
    },
    goto: function(e) {
        e.preventDefault();
        var i = Number($(e.currentTarget).text());
        $('#job-list').empty();
        this.results = this.results.getPage(i);
        console.log('this page is ' + this.results);
        _.each(this.results.models, function(jobresult) {
            $('#job-list').append(new directory.JobListView({
                model: jobresult
            }).render().el);
        }, self);
        this.check(this.results);
    },

    renderPag: function(num) {
        var self = this;
        $('#pagination', this.el).html('<li class="li-pre disabled"><a href="#" class="previous" aria-label="Previous"><span aria-hidden="true">&laquo;</span></a></li>');
        for (var i = 1; i < num + 1; i++) {
            $('#pagination', self.el).append('<li><a href="#" class="page">' + i + '</a></li>');
        };
        $('#pagination', this.el).append('<li class="li-next"><a href="#" class="next" aria-label="Next"><span aria-hidden="true">&raquo;</span></a></li>')
    },
    check: function(content){
         if (!content.hasPreviousPage()) {
            $('.li-pre').addClass('disabled');
        } else {
            $('.li-pre').removeClass('disabled');
        }

        if (!content.hasNextPage()) {
            $('.li-next').addClass('disabled');
        } else {
            $('.li-next').removeClass('disabled');
        }
    }

    // events: {
    //     ()
    // }
});

directory.JobListView = Backbone.View.extend({
    tagName: 'li',
    className: 'position-list',
    initialize: function() {
        console.log("initializing job list ");
    },
    render: function() {

        this.$el.html(this.template(this.model.attributes));
        return this;
    },
    events: {
        "click #applyButton": "apply"
    },

    apply: function(event) {
        event.preventDefault();

        var url = '/api/applystatus/apply';
        console.log("apply now");

        var job_id = this.model.attributes.id;
        console.log(job_id);
        var applyInf = {
            job_id: job_id
                // account_id: $.cookie('account_id')
        };
        console.log(applyInf);


        $.ajax({
            url: url,
            type: "PUT",
            dataType: "json",
            contentType: "application/json",
            data: JSON.stringify(applyInf),
            processData: false,
            statusCode: {
                401: function() {
                    alert('Please Login First!')
                }
            },
            success: function(data) {
                console.log(data);
                if (data) {
                    alert("Success!");
                } else {
                    alert("Sorry you have already applied!")
                }
            }
        });
    }

})