// Filename: testSubRouter.js
define([
    'jquery',
    'underscore',
    'backbone',
    'backbone_sub',
    '../exampleModule/views/welcome/WelcomeView',
    '../exampleModule/views/box/BoxView'
    //'testModule/views/first/FirstView'    
], function ($, _, Backbone, Backbone_sub, WelcomeView) {

    var TestSubRouter = Backbone.SubRoute.extend({
        routes: {
            "": "showWelcome",
            "box": "showBox"
            
        },
    
        showWelcome : function(path) {

            // Call render on the module we loaded in via the dependency array
            var welcomeView = new WelcomeView();
            welcomeView.render();

        },

        showBox : function () {            
            var showBoxView = new ShowBoxView();
            showBoxView.render();
        }
       
    });

    var initialize = function () {

        var test_router = new TestSubRouter('example');
        
        //var welcomeView = new WelcomeView();
        //   welcomeView.render();
            
  /*
        test_router.on('route:showWelcome', function () {

            // Call render on the module we loaded in via the dependency array
            var welcomeView = new WelcomeView();
            welcomeView.render();

        });

        test_router.on('route:showBox', function () {            
            var showBoxView = new ShowBoxView();
            showBoxView.render();
        });
      */
    };
    return {
        initialize: initialize
    };
});
