// Filename: router.js
define([
    'jquery',
    'underscore',
    'backbone',
    //'backbone_sub',
    //'listener',
    'views/login/LoginPageView',
    'views/home/HomeView',
    'views/myBaby/MyBabyView',
    'views/contributors/ContributorsView',
    'views/footer/FooterView',
    '../exampleModule/testApp'
], function ($, _, Backbone, LoginView, HomeView, MyBabyView, ContributorsView, FooterView, testApp) {

    var currentView = null;
    var that = this;

    //var router_listener = {};
    //_.extend(router_listener, Backbone.Events);
    Backbone.Events.on("babyApp:nonauthorized", function () {
        showLoginView();
    });


    var AppRouter = Backbone.Router.extend({
        routes: {
            // Define some URL routes
            'myBaby': 'showMyBaby',
            'myAccount': 'showContributors',
            'example*subroute': 'invokeTestModules',
            // Default
            '': 'defaultAction'
        }
        
    });

    var fireUnauthorizedEvent = function () {        
        Backbone.Events.trigger('babyApp:nonauthorized');  
    };

    var initialize = function () {

        var app_router = new AppRouter;

        app_router.on('route:showMyBaby', function () {
            

            if (window.BabyUtil.isAuthorized()) {
                // Call render on the module we loaded in via the dependency array
                var myBabyView = new MyBabyView();
                myBabyView.render();
            } else {
                fireUnauthorizedEvent();
            }

        });

        app_router.on('route:showContributors', function () {
            
            // Like above, call render but know that this view has nested sub views which 
            // handle loading and displaying data from the GitHub API  
            if (window.BabyUtil.isAuthorized()) {
                var contributorsView = new ContributorsView();
            } else {
                fireUnauthorizedEvent();
            }
        });

        app_router.on('route:invokeTestModules', function (testModules) {
            
            if (window.BabyUtil.isAuthorized()) {
                // lets display the test page         
                //var testApp = new testApp();
                testApp.initialize();
                //testSubRouter.initialize();
            } else {
                fireUnauthorizedEvent();
            }
        });

        app_router.on('route:defaultAction', function () {           
            if (window.BabyUtil.isAuthorized()) {
                // We have no matching route, lets display the home page 
                var homeView = new HomeView();
                homeView.render();
            } else {
                fireUnauthorizedEvent();
            }
        });

        // Unlike the above, we don't call render on this view as it will handle
        // the render call internally after it loads data. Further more we load it
        // outside of an on-route function to have it loaded no matter which page is
        // loaded initially.
        var footerView = new FooterView();

        Backbone.history.start();
    };

    var showLoginView = function () {

        var loginView = new LoginView();
        // Close and unbind any existing page view
        //if (this.currentView)
        //    this.currentView.close();
        // Establish the requested view into scope        
        loginView.render();
        this.currentView = loginView;
        
    };

    return {
        initialize: initialize,
        displayLogin: showLoginView
    };
});
