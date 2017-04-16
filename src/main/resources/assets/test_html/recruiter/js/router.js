// Filename: router.js
define([
  'jquery',
  'jqueryCookie',
  'underscore',
  'backbone',
  'views/login/LoginView',
  'views/register/RegisterView',
  'views/shell/RecruiterShellView',
  'views/dashboard/DashBoardView',
  'views/jobs/PostJobView',
  'views/jobs/DraftsView',
  'views/jobs/DraftDetailView',
  'views/jobs/PublishedJobsView',
  'views/jobs/ArchivedJobsView',
  'views/pool/PoolView',
  'models/login/UserModel',
  'models/jobs/Draft',

], function($, cookie, _, Backbone, LoginView, RegisterView, ShellView, DashBoardView, PostJobView, DraftsView,
  DraftDetailView, PublishedJobsView, ArchivedJobsView,PoolView, UserModel,Draft) {
  var self = this;
  var AppRouter = Backbone.Router.extend({
    routes: {
      // Default
      'login': 'login',
      'register': 'register',
      'dashboard': 'showDashboard',
      'postJob': 'postJob',
      'drafts': 'showDrafts',
      'drafts/:id': 'editDraft',
      'published': 'showPublished',
      'published/:id': 'editPublished',
      'archived': 'showArchived',
      'pool': 'showPool',
      'logout': 'logout',

    },
    initialize: function() {
      var auth = $.cookie('access_token');
      var user_type = $.cookie('user_type');
      $login = $('#login');
      if (user_type == 'Recruiter') {
        $login.empty();
        self.user = new UserModel();
        self.user.fetch({
          success: function(model) {
            self.shellView = new ShellView({
              model: model
            });
            shellView.render();
          }
        })
      } else {
        window.location.hash = 'login';
      }

    },

  });

  var initialize = function() {

    var app_router = new AppRouter;
    app_router.on("route", function() {
      $("html,body").scrollTop(0);
    });

    app_router.on('route:showDashboard', function() {
      console.log(self.user);
      // console.log(this.$('#recruiter-content'));
      var dashboardView = new DashBoardView();
      dashboardView.render();

    });

    app_router.on('route:postJob', function() {
      var postjobView = new PostJobView();
      postjobView.render();

    });

    app_router.on('route:showDrafts', function() {
      var draftsView = new DraftsView();
      draftsView.render();
    });
    app_router.on('route:editDraft', function(id) {
      var draft = new Draft({id: id});
      draft.fetch().done(function(){
        var draftDetailView = new DraftDetailView({model: draft});
      draftDetailView.render();
      })
      
    });
    app_router.on('route:showPublished', function() {
      var publishedJobsView = new PublishedJobsView();
      publishedJobsView.render();
    });

    app_router.on('route:showArchived', function() {
      var archivedView = new ArchivedJobsView();
      archivedView.render();
    });


    app_router.on('route:showPool', function() {

      // Call render on the module we loaded in via the dependency array
      var poolView = new PoolView();
      poolView.render();

    });


    app_router.on('route:login', function(actions) {
      var loginView = new LoginView();
      loginView.render();
    });

    app_router.on('route:register', function(actions) {
      var registerView = new RegisterView();
      registerView.render();
    });
    app_router.on('route:logout', function(actions) {
      $.cookie('access_token', "");
      $.cookie('user_type', "");

      Backbone.history.navigate('login', {
        trigger: true
      });
      self.shellView.destroy();

      window.location.reload();
      // var loginView = new LoginView();
      // loginView.render();
    });

    // var footerView = new FooterView();

    Backbone.history.start();

  };
  return {
    initialize: initialize
  };
});