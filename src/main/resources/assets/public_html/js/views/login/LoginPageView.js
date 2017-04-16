define([
    'jquery',
    'underscore',
    'backbone',
    'babyUtil'     
], function ($, _, Backbone, BabyUtil) {//, welcomeTemplate){

    var loginView = Backbone.View.extend({
        el: $("#page"),
        initialize: function () {
            var templateStr = window.BabyUtil.loadTemplateSync("templates/login/" + window.userInfo.lang + "_loginTemplate.html");
            this.template = _.template(templateStr);
        },
        render: function () {
            this.$el.html(this.template( ));
            //var sidebarView = new SidebarView();
            //sidebarView.render();
        }
    });
    return loginView;
});
