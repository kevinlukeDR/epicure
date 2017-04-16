(function(window, undefined) {

    var UtilClass = function() {
        //load template
        var loadedTemplates = {};

        var loadTemplate = function(tmpl_name, tmpl_data) {


            if (!loadedTemplates[tmpl_name]) {

                var tmpl_url = tmpl_name;

                $.ajax({
                    url: tmpl_url,
                    method: 'GET',
                    async: false,
                    success: function(data) {
                        loadedTemplates[tmpl_name] = data;
                    }
                });
            }
            return loadedTemplates[tmpl_name];
        };

        var getJson = function(url, data, callback) {
            // shift arguments if data argument was ommited
            if ($.isFunction(data)) {
                callback = data;
                data = null;
            }

            return $.ajax({
                type: "GET",
                url: url,
                data: data,
                success: callback,
                dataType: "json",
                async: false
            });
        };
        var redirectToLogin = function() {
            window.location.hash = 'login';
        };
        var notfound = function() {
            window.location.hash = '404';
        };
        var startTimer = function(duration, display) {
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
        };

        var checkAuthorized = function() {
            var user = new UserModel();
            user.fetch();
            console.log(user);
            if (typeof(Storage) !== "undefined") {
                localStorage.lname = user.attributes.lname;
                localStorage.fname = user.attributes.fname;
            } else {
                // Sorry! No Web Storage support..
            }
        };

        /*
         * ERRORS and ALERT HANDLING
         */

        // Default alert when there is a validation error


        return {
            loadTemplateSync: function(tmpl_name, tmpl_data) {
                return loadTemplate(tmpl_name, tmpl_data);
            },
            loadJsonSync: function(url, data, callback) {
                return getJson(url, data, callback);
            },
            redirectToLogin: redirectToLogin,
            tonotfound: notfound,
            startTimer: startTimer
        };
    };

    window.util = new UtilClass();

})(window);