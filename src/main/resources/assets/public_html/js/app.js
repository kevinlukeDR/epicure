// Filename: app.js
define([
    'jquery',
    'underscore',
    'backbone',
    'polyglot',
    'router', // Request router.js
    'jqdock',
    'babyUtil'
], function ($, _, Backbone, Polyglot, Router, jqDock, BabyUtil) {

    var that = this;
    
    //create a event buss 
   // var pubSub = _.extend({},Backbone.Events);
  
    var initialize = function () {

        
       var locale = window.userInfo.lang || 'en';
        // Gets the language file.
        window.BabyUtil.loadJsonSync('locales/' + locale + '.json', function (data) {
            // Instantiates polyglot with phrases.
            that.polyglot = new that.Polyglot({phrases: data});
        });

        //load locale css file
        var $head = $("head");
        var $headlinklast = $head.find("link[rel='stylesheet']:last");
        var linkElement = "<link rel='stylesheet' href='css/" + locale + "/styles.css' type='text/css' media='screen'>";
        if ($headlinklast.length) {
            $headlinklast.after(linkElement);
        }
        else {
            $head.append(linkElement);
        }

        //initialize menu
        $("#menu").append("<a href='#' ><img src='imgs/home.png' alt='" +
                that.polyglot.t('menu.home') + "' title='" + that.polyglot.t('menu.home') + "' /></a>");
        $("#menu").append("<a href='#/myBaby' ><img src='imgs/baby-laughing-icon.png' alt='" +
                that.polyglot.t('menu.myBaby') + "' title='" + that.polyglot.t('menu.myBaby') + "' /></a>");
        $("#menu").append("<a href='#/myAccount'><img src='imgs/setting.png' alt='" +
                that.polyglot.t('menu.myAccount') + "' title='" + that.polyglot.t('menu.myAccount') + "' /></a>");
        $("#menu").append("<a href='#/example'><img src='imgs/pig-152x152.png' alt='" +
                that.polyglot.t('menu.example') + "' title='" + that.polyglot.t('menu.example') + "' /></a>");
        $("#menu").append("<a href='#/example'><img src='imgs/test-tube.png' alt='" +
                that.polyglot.t('menu.experiment') + "' title='" + that.polyglot.t('menu.experiment') + "' /></a>");
        

        // set up a handler for the docksleep event...
        var onDockSleep = function () { //scope (this) is the #menu element
            var dock = $('.jqDock', this); //this is the actual dock
            //slide the dock off the top of the window, fading out as it goes...
            dock.animate({top: -1 * (dock.height()), opacity: 0}, 600);
            //Note : the original menu element (#menu) and the dock wrapper
            //(div.jqDockWrap) are still in place.
            //bind a one-off mousemove event to the original menu element...
            $(this).one('mousemove', function () {
                //slide the dock back into the window, fading in at the same time...
                dock.stop().animate({opacity: 1, top: 0}, 600);
                //nudge the dock to reset the idle timer without waiting for
                //animation to finish...
                $(this).trigger('docknudge');
                return false;
            });
            //don't let the dock go to sleep...
            return false;
        };
        // set up the options to be used for jqDock...
        dockOptions =
                {
                    labels: true, // add labels (defaults to 'bc')
                    idle: 6000, // set idle timeout to 6 seconds
                    onSleep: onDockSleep, // handler declared above
                    capSizing: true,
                    size: 60,
                    sizeMax: 120,
                    align: 'middle' // horizontal menu, with expansion DOWN from a fixed TOP edge
                };
        // ...and apply...
        $('#menu').jqDock(dockOptions);
        
        /*
        //handle logout, non-authorized event. 
         that.listenTo(Backbone.Events,"logout",that.logoutFn);
         
         that.listenTo(Backbone.Events,"destroyApp",that.destroyFn);
         
         that.listenTo(Backbone.Events, 'babyApp:nonauthorized', that.loginFn);
         */

        // Pass in our Router module and call it's initialize function
        Router.initialize();

        return that;
    };
    
    var loginFn = function(){       
        // for Views, If you use listenTo, the event listener will automatically be
        // disabled when your view is removed
        //show login view.
        Router.displayLogin();
    };
    
    var logoutFun = function(){
        
        
    };
    
    var destroy = function(){
        
        
    };
    
    return {
        initialize: initialize,
        login: loginFn,
        logout:  logoutFun,
        destroy: destroy
        
    };
});
