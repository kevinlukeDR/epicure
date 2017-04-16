// Filename: app.js
'use strict';
define([
  'jquery',
  'underscore',
  'backbone',
  'router',
  'util'
], function($, _, Backbone, Router, util) {
  //add token in header 
  $(document).ajaxSend(function(event, xhr) {
    var authToken = $.cookie('access_token');
    // var authToken = 'fake12345611';
    if (authToken) {
      xhr.setRequestHeader("Authorization", "Bearer " + authToken);
    }
  });
  $(document).ajaxError(function(event, xhr) {
    /* Stuff to do when an AJAX call returns an error */
    if (xhr.status == 401) {
      $.cookie('access_token', "");
      $.cookie('user_type', "");

      window.util.redirectToLogin();
    } else if (xhr.status == 404) {
      window.util.tonotfound();
    }
  });
  
  var initialize = function() {
    Router.initialize();
  };

  return {
    initialize: initialize
  };
});