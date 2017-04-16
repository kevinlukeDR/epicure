(function (window, undefined) {

    var UtilClass = function () {

        var loadedTemplates = {};

        var loadTemplate = function (tmpl_name, tmpl_data) {


            if (!loadedTemplates[tmpl_name]) {

                var tmpl_url = tmpl_name;

                $.ajax({
                    url: tmpl_url,
                    method: 'GET',
                    async: false,
                    success: function (data) {
                        loadedTemplates[tmpl_name] = data;
                    }
                });
            }
            return loadedTemplates[tmpl_name];
        };

        var getJson = function (url, data, callback) {
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
        
        var checkAuthorized = function() {
            return trues;
            
        };
        
        /*
         * ERRORS and ALERT HANDLING
         */ 
         
        // Default alert when there is a validation error
        var displayValidationErrors = function (messages) {
            for (var key in messages) {
                if (messages.hasOwnProperty(key)) {
                    this.addValidationError(key, messages[key]);
                }
            }
            this.showAlert('Uh oh...', 'Please fix validation errors and try again.', 'alert-error');
        };

        var addValidationError = function (field, message) {
            var controlGroup = $('#' + field).parent().parent();
            controlGroup.addClass('error');
            $('.help-block', controlGroup).html(message);
        };

        var removeValidationError = function (field) {
            var controlGroup = $('#' + field).parent().parent();
            controlGroup.removeClass('error');
            $('.help-block', controlGroup).html('');
        };

        return {
            loadTemplateSync: function (tmpl_name, tmpl_data) {
                return loadTemplate(tmpl_name, tmpl_data);
            },
            loadJsonSync: function (url, data, callback) {
                return getJson(url, data, callback);
            },
            displayValidateionErrors: function(messages){
                return displayValidationErrors(messages);
            },
            addValidationError: function(field, message){
                return addValidationError(field, message);
            },
            removeValidationError: function(field){
                return removeValidationError(field);
            },
            isAuthorized: checkAuthorized

        };
    };

    window.BabyUtil = new UtilClass();

})(window);

