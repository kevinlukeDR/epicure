var directory = {

    views: {},
    models: {},
    collections: {},

    loadTemplates: function(views, callback) {

        var deferreds = [];

        $.each(views, function(index, view) {
            if (directory[view]) {
                deferreds.push($.get('templates/' + view + '.html', function (data) {
                    directory[view].prototype.template = _.template(data);
                }, 'html'));
            }
            // } else {
            //     alert(view + " not found");
            // }
        });
        $.when.apply(null, deferreds).done(callback);
    },

    getJson: function(url, data, callback) {
        // shift arguments if data argument was ommited
        if ($.isFunction(data)) {
            callback = data;
            data = null;
        }

        return $.ajax({
            type: "GET",
            url: url,
            data: data,
            dataType: "application/json",
            success: callback
                //                    async: false
        });
    },
    redirectToLogin: function() {
        window.location.hash = 'login';
    },
    tonotfound: function(){
        window.location.hash = '404';
    },

    checkAuthorized: function() {
        //some action to check authorization
    },
    convertDate: function(formData) {
        formData.val(new Date(formData.val()).getTime());
    },

    startTimer: function(duration, display) {
        var timer = duration,
            minutes, seconds;
        setInterval(function() {
            minutes = parseInt(timer / 60, 10);
            seconds = parseInt(timer % 60, 10);

            minutes = minutes < 10 ? "0" + minutes : minutes;
            seconds = seconds < 10 ? "0" + seconds : seconds;

            display.text(minutes + ":" + seconds);

            if (--timer < 0) {
                timer = 0;
            }
        }, 1000);
    },

    diffInDays: function(d1, d2) {
        var t2 = d2.getTime();
        var t1 = d1.getTime();
        return parseInt((t2 - t1) / (24 * 3600 * 1000));
    },


    arrangeJson: function(data) {
        var initMatch = /^([a-z0-9]+?)\[/i;
        var first = /^\[[a-z0-9]+?\]/i;
        var isNumber = /^[0-9]$/;
        var bracers = /[\[\]]/g;
        var splitter = /\]\[|\[|\]/g;

        for (var key in data) {
            if (initMatch.test(key)) {
                data[key.replace(initMatch, '[$1][')] = data[key];
            } else {
                data[key.replace(/^(.+)$/, '[$1]')] = data[key];
            }
            delete data[key];
        }


        for (var key in data) {
            this.processExpression(data, key, data[key]);
            delete data[key];
        }
        // return data;

    },
    processExpression: function(dataNode, key, value) {
        var initMatch = /^([a-z0-9]+?)\[/i;
        var first = /^\[[a-z0-9]+?\]/i;
        var isNumber = /^[0-9]$/;
        var bracers = /[\[\]]/g;
        var splitter = /\]\[|\[|\]/g;

        var e = key.split(splitter);
        if (e) {
            var e2 = [];
            for (var i = 0; i < e.length; i++) {
                if (e[i] !== '') {
                    e2.push(e[i]);
                }
            }
            e = e2;
            if (e.length > 1) {
                var x = e[0];
                var target = dataNode[x];
                if (!target) {
                    if (isNumber.test(e[1])) {
                        dataNode[x] = [];
                    } else {
                        dataNode[x] = {}
                    }
                }
                this.processExpression(dataNode[x], key.replace(first, ''), value);
            } else if (e.length == 1) {
                dataNode[e[0]] = value;
            } else {
                alert('This should not happen...');
            }
        }
    }
};

Backbone.Validation.configure({
    forceUpdate: true
});