directory.FoundJobView = Backbone.View.extend({
	initialize: function(attr){
		this.uuid = attr;
		var url = 'api/email/foundjob/'+this.uuid;
		$.ajax({
			url: url,
			type: 'POST',
			statusCode: {
                404: function() {
                    window.location.hash = "#404";
                }
            },
		})
		.done(function() {
			console.log("success");
		})
	},
	render: function() {
        this.$el.html(this.template());
        
        return this;
    },
});