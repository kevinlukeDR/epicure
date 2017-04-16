define([
  'jquery',
  'underscore',
  'backbone',
  'views/sidebar/SidebarView'
  //'text!templates/home/homeTemplate.html'
], function($, _, Backbone, SidebarView){//, homeTemplate){

  var HomeView = Backbone.View.extend({
    el: $("#page"),
    
    initialize: function() {
        
        var templateStr = window.BabyUtil.loadTemplateSync("templates/home/" + window.userInfo.lang + "_homeTemplate.html");        
        this.template = _.template(templateStr);
    }, 

    render: function(){ 
      this.$el.html(this.template( ));
    }

  });

  return HomeView;
  
});
