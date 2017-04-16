define([
  'jquery',
  'underscore',
  'backbone',
  'models/myBaby/MyBabyModel'
], function($, _, Backbone, MyBabyModel){
  var MyBabyCollection = Backbone.Collection.extend({
    model: MyBabyModel,
    
    initialize: function(){

      //this.add([project0, project1, project2, project3, project4]);

    }

  });
 
  return MyBabyCollection;
});
