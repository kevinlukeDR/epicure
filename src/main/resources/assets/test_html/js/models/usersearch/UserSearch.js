directory.SearchReq = Backbone.Model.extend({
    defaults: {
        "metadata": {
            total: "1",
            currentPage: "1"
        },

        "position": "",
        "city": "%",
        "subject": "%",
        "grade": "%"
    },

    initialize: function() {
        this.on('change', function() {
            console.log('search again');
        });
    }
})