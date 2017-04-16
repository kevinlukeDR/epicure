require.config({
  paths: {
    jquery: 'lib/jquery/jquery.min',
    underscore: 'lib/underscore/underscore-min',
    backbone: 'lib/backbone/backbone',
    backboneModal: 'lib/backbone/backbone.modal',
    backbonePaginator: 'lib/backbone/backbone.paginator.min.js',
    moment: 'lib/moment',
    bootstrap: '../bootstrap/js/bootstrap.min',
    bootstrapTagsinput: 'lib/bootstrap-tagsinput',
    bootstrapDatepicker: 'lib/bootstrap-datepicker',
    jqueryValidate: 'lib/validate/jquery.validate',
    // jqueryAdditional: 'lib/validate/additional-methods',
    jqueryCookie: 'lib/jquery/jquery.cookie',
    jquerySerializeObject: 'lib/jquery/jquery.serializeObject.min',
    jqueryDataTables: 'lib/jquery/jquery.dataTables.min',
    select2: 'lib/select2.full.min',
    metisMenu: 'lib/metismenu/metisMenu',
    text: 'text',
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
    },
    bootstrap: {
      deps: ['jquery'],
      // exports: '$.fn.popover'
    },
    bootstrapTagsinput: {
      deps: ['bootstrap']
    },
    bootstrapDatepicker: {
      deps: ['bootstrap'],
    },
    jqueryValidate: {
      deps: ['jquery']
    },
    // jqueryAdditional: {
    //   deps: ['jquery', 'jqueryValidate']
    // },
    jqueryCookie: {
      deps: ['jquery'],
    },
    jquerySerializeObject: {
      deps: ['jquery'],
    },
    core:{
      deps: ['jquery', 'bootstrap']
    }

  },

});

require([
  // Load our app module and pass it to our definition function
  'app',

], function(App) {
  // The "app" dependency is passed in as "App"
  // Again, the other dependencies passed in are not "AMD" therefore don't pass a parameter to this function
  App.initialize();
});