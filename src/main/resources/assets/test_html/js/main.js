require.config({
    paths: {
        jquery:'lib/jquery.min',
        underscore: 'lib/underscore-min',
        backbone: 'lib/backbone',
        backbone-validation: 'lib/backbone-validation-min',
        jqueryUI: 'lib/jquery-ui.min',
        select2: 'lib/select2.full.min',
        templates: '../templates',
        util: 'util'
    },
    shim: {
         underscore: {
            exports: '_'
         },
         backbone: {
            deps: ['underscore', 'jquery'],
            exports: 'Backbone'
         }
    }
});
require([
    //load app module and pass it to our definition function
    'app',
], function(App){
    App.initialize();
});



directory.Router = Backbone.Router.extend({
    initialize: function(){
        directory.shellView = new directory.ShellView();
        $('body').html(directory.shellView.render().el);
        $('body').click(function(){
            $('.dropdown').removeClass("open");
        });
        this.$content = $()
    }
})

