// Filename: testApp.js
define([
    'jquery',
    'underscore',
    'backbone',
    'backbone_sub',
    '../exampleModule/testSubRouter'    
], function ($, _, Backbone, Backbone_sub, testRouter) {
    var initialize = function () {
        // Pass in our Router module and call it's initialize function
        testRouter.initialize();
    };

    return {
        initialize: initialize
    };
});
