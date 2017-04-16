define([
    "app",
], function(app){

    var UserModel = Backbone.Model.extend({
        url: '/api/recruiter/get'
    });
    
    return UserModel;
});