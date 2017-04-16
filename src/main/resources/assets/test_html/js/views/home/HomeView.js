directory.HomeView = Backbone.View.extend({
    events: {
        "click #search-button": function(e) {
            e.preventDefault();
            // if ($.cookie('access_token')) {
            //     this.userSearch();
            // } else {
                this.getJobs();
            // }

        }
    },

    render: function() {
        this.$el.html(this.template());
        this.$content = $('#content');
        $('.select-province', this.el).select2({
            allowClear: true
        });

        $('.select-subject', this.el).select2({
            // placeholder: 'Any Subject',
            allowClear: true
        });

        $('.select-grade', this.el).select2({
            // placeholder: 'Any Grade',
            allowClear: true
        });

        var options = {
            useEasing: true,
            useGrouping: true,
            separator: ',',
            decimal: '.',
            prefix: '',
            suffix: ''
        };
        // this.test();
        this.renderFeatured();
        return this;
    },

    renderFeatured: function() {
        var self = this;
        // if (featured) {
        //     console.log('reuse featured job');
        //     featured.destroy();
        // };

        var featured = new directory.FeaturedJobsCollection();
        featured.fetch({
            success: function(model,resp){
                if (resp.metadata.status == "ok") {
                    var featuredView = new directory.FeaturedJobsView({model: model});
                    featuredView.render();
                    $('#feature-jobs', self.el).html(featuredView.el);
                }
            }
        })
        // var featuredView = new directory.FeaturedJobsView();
        // featuredView.render();
        // $('#feature-jobs', this.el).html(featuredView.el);
    },

    test: function() {
        console.log($('#countupJS', this.el).html());
        odometerOptions = {
            auto: false
        };
        var totaljobs = 0;
        $.ajax({
            url: "/api/job/totaljobsnumber",
            method: "GET",
            contentType: false,
            processData: false,
            statusCode: {
                403: function() {
                    //access denied
                    window.location.replace('/test_html/index.html/#denied');
                }
            },
            success: function(data) {
                // console.log(data);
                totaljobs = data;
                var odometer = new Odometer({
                    el: $('.odometer')[0],
                    value: 0,
                    theme: 'plaza',
                    duration: 3000, // Change how long the javascript expects the CSS animation to take
                    animation: 'count'
                });
                odometer.update(72342);
                odometer.render();
                if (data.error) { // If there is an error, show the error messages
                    $('#feedback').text(data.error.text).show();
                }
            }
        });
        $.ajax({
            url: "/api/candidate/totalaccountsnumber",
            method: "GET",
            contentType: false,
            processData: false,
            success: function(data) {
                // console.log(data);
                totaljobs = data;
                var odometer2 = new Odometer({
                    el: $('.odometer')[1],
                    value: 0,
                    theme: 'plaza',
                    duration: 3000, // Change how long the javascript expects the CSS animation to take
                    animation: 'count'
                });
                odometer2.update(356);
                odometer2.render();
                if (data.error) { // If there is an error, show the error messages
                    $('#feedback').text(data.error.text).show();
                }
            }
        });
    },
    getJobs: function() {

        // var url = '/api/job/guest';
        var self = this;
        //for latest api
        var position = $('#search-position').val(),
            city = $('#location').val(),
            subject = $('#subject').val(),
            grade = $('#grade').val();
        var query = "?position=" + '&city=' + city + "&subject=" + subject + '&grade=' + grade;
        window.location.hash = "#search/" + encodeURIComponent(query);

    },

    userSearch: function() {
        // var url = '/api/job/';
        var self = this;
        var position = $('#search-position').val(),
            city = $('#location').val(),
            subject = $('#subject').val(),
            grade = $('#grade').val();
        var query = "?position=" + position + '&city=' + city + "&subject=" + subject + '&grade=' + grade;
        var query = "?position=" + '&city=' + city + "&subject=" + subject + '&grade=' + grade;
        window.location.hash = "#search/user/" + encodeURIComponent(query);
        // var formValues = {
        //     position: $('#search-position').val(),
        //     city: $('#city').val(),
        //     subject: $('#subject').val(),
        //     grade: $('#grade').val()
        // };

        // var searchReq = new directory.SearchReq(formValues);

        // $.ajax({
        //     url: url,
        //     type: 'POST',
        //     dataType: "json",
        //     contentType: "application/json",
        //     data: JSON.stringify(searchReq.toJSON()),
        //     processData: false,
        //     success: function(data) {
        //         // console.log(data.data);
        //         // console.log(data);
        //         var results = JSON.stringify(data.data);
        //         var resultsCollection = new directory.JobResultCollection(JSON.parse(results));
        //         // console.log(JSON.stringify(resultsCollection));

        //         resultsCollection.each(function(jobresult) {
        //             console.log(jobresult);
        //         });


        //         console.log(resultsCollection);
        //         directory.jobBoardView = new directory.JobBoardView({
        //             model: resultsCollection
        //         });
        //         directory.jobBoardView.render();
        //         self.$content.html(directory.jobBoardView.el);

        //         if (data.error) { // If there is an error, show the error messages
        //             console.log("error")
        //         }
        //     }
        // });
    }
});