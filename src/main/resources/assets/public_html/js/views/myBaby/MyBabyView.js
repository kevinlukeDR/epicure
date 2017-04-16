define([
  'jquery',
  'underscore',
  'backbone',
  'views/sidebar/SidebarView',
  'models/myBaby/MyBabyModel',
  'collections/myBaby/MyBabyCollection',
  'views/myBaby/MyBabyListView',
  'text!templates/myBaby/myBabyTemplate.html'
], function($, _, Backbone, SidebarView, MyBabyModel, MyBabyCollection, MyBabyListView, myBabyTemplate){

  var myBabyView = Backbone.View.extend({
    el: $("#page"),
    render: function(){
      $('.menu li').removeClass('active');
      $('.menu li a[href="'+window.location.hash+'"]').parent().addClass('active');
      this.$el.html(myBabyTemplate);

      var activity0 = new MyBabyModel({title: '艺术', url: 'https://github.com/thomasdavis/backbonetutorials/tree/gh-pages/examples/cross-domain'}); 
      var activity1 = new MyBabyModel({title:'体育', url: 'https://github.com/thomasdavis/backbonetutorials/tree/gh-pages/examples/infinite-scroll'}); 
      var activity2 = new MyBabyModel({title:'学术', url: 'https://github.com/thomasdavis/backbonetutorials/tree/gh-pages/examples/modular-backbone'}); 
      var activity3 = new MyBabyModel({title:'午休', url: 'https://github.com/thomasdavis/backbonetutorials/tree/gh-pages/examples/nodejs-mongodb-mongoose-restify'});
      var activity4 = new MyBabyModel({title:'午餐', url: 'https://github.com/thomasdavis/backbonetutorials/tree/gh-pages/examples/todo-app'});

      var aActivities = [activity0, 
                      activity1,
                      activity2,
                      activity3,
                      activity4];

      var myBabyCollection = new MyBabyCollection(aActivities);  
      var myBabyListView = new MyBabyListView({ collection: myBabyCollection}); 
      
      myBabyListView.render(); 

      // add the sidebar 
      var sidebarView = new SidebarView();
      sidebarView.render();

    }
  });

  return myBabyView;
});
