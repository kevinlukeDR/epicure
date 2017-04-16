define([
  'jquery',
  'underscore',
  'backbone',
  'text!templates/sidebar/sidebarTemplate.html'
], function($, _, Backbone, sidebarTemplate){

  var SidebarView = Backbone.View.extend({
    el: $(".sidebar"),

    render: function(){

      var that = this;

      var first_btn = { site_url : "http://yiyujia.blogspot.com" ,
                          image_url : "./imgs/Button-Blank-Blue-icon.png",
                          title : "Function_1",
                          description: "This side bar will be toolbox buttons" };

      var second_btn = {  site_url : "http://www.idatamining.org" ,
                          image_url : "./imgs/Button-Blank-Red-icon.png",
                          title : "Function_2",
                          description: "to hold toolbox buttons in the future" };

      var data = {
        ads: [first_btn, second_btn]
      };

      var compiledTemplate = _.template( sidebarTemplate, data );
    
      $(".sidebar").append(compiledTemplate);
    }

  });

  return SidebarView;
  
});
