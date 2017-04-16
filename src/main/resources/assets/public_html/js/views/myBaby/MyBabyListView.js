// Filename: views/projects/list
define([
  'jquery',
  'underscore',
  'backbone',
  // Pull in the Collection module from above,
  'models/myBaby/MyBabyModel',
  'collections/myBaby/MyBabyCollection',
  'text!templates/myBaby/myBabyListTemplate.html'

], function($, _, Backbone, MyBabyModel, MyBabyCollection, myBabyListTemplate){
  var myBabyListView = Backbone.View.extend({
    el: $("#activity-list"),

    render: function(){
      
      var data = {
        activities: this.collection.models,
        _: _ 
      };

      var compiledTemplate = _.template( myBabyListTemplate, data );
      $("#activity-list").html( compiledTemplate ); 
    }
  });
  return myBabyListView;
});
