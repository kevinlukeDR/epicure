if (typeof DEBUG === 'undefined') DEBUG = true;

// Require.js allows us to configure shortcut alias
// Their usage will become more apparent futher along in the tutorial.
require.config({
    paths: {
        jquery: 'libs/jquery/jquery-min',
        underscore: 'libs/underscore/underscore-min',
        backbone: 'libs/backbone/backbone',
        backbone_sub: 'libs/backbone/backbone.subroute',
        polyglot: 'libs/polyglot/polyglot',
        templates: '../templates',
        jqdock: 'libs/jdock/jquery.jqdock',
        listener: 'libs/backbone/backbone.listener',
        babyUtil: 'util'
    },
    shim: {
        underscore: {
            exports: '_'
        },
        backbone: {
            deps: ['underscore', 'jquery'],
            exports: 'Backbone'
        },
        listener: {
            deps: ['backbone'],
            exports: 'BackboneListener'
        },
        polyglot: {
            deps:['jquery'],
            exports: 'Polyglot'
        }
    }
});

require([
    // Load our app module and pass it to our definition function
    'app'

], function (App) {
    //before app is loaded. check userInfo first.
    //get or initilize user session object.
        window.BabyUtil.loadJsonSync('temp/user.json', function (user) {
            // Instantiates polyglot with phrases.
            window.userInfo = user;
        });

    
    // The "app" dependency is passed in as "App"
    // Again, the other dependencies passed in are not "AMD" therefore don't pass a parameter to this function
    window.babyApp = App.initialize();
});
