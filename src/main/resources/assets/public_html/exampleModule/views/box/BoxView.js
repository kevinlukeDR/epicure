define([
  'jquery',
  'underscore',
  'backbone',  
  'text!../../../../templates/box/boxTemplate.html'
], function($, _, Backbone, boxTemplate){

  var BoxView = Backbone.View.extend({
    el: $("#page"),

    render: function(){
      
      //$('.menu li').removeClass('active');
      //$('.menu li a[href="#"]').parent().addClass('active');
      this.$el.html(boxTemplate);

      //var sidebarView = new SidebarView();
      //sidebarView.render();
 
    }

  });

  return BoxView;
  
});
