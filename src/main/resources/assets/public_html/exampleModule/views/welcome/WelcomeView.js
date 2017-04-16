define([
    'jquery',
    'underscore',
    'backbone',
    'babyUtil'
            //'text!../../../../templates/welcome/welcomeTemplate.html'
], function ($, _, Backbone, BabyUtil) {//, welcomeTemplate){

    var WelcomeView = Backbone.View.extend({
        el: $("#page"),
        initialize: function () {
            var templateStr = window.BabyUtil.loadTemplateSync("exampleModule/templates/welcome/" + window.userInfo.lang + "_welcomeTemplate.html");
            this.template = _.template(templateStr);
        },
        render: function () {
            this.$el.html(this.template( ));
            //var sidebarView = new SidebarView();
            //sidebarView.render();
        }
    });
    return WelcomeView;
});
