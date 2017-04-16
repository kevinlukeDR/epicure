directory.ProfileStrView = Backbone.View.extend({
    initialize: function() {
    	console.log(this.model);
        this.listenTo(this.model, "change", this.render, this);
    },
    render: function() {
        this.$el.html(this.template(this.model.attributes));
        this.details();
        return this;
    },

    details: function(){
    	var details = this.model.attributes.details;
    	var self = this;
    	if (details == 0) {
    		$('#marketing', self.el).text('Congratulations! Your profile is outstanding!')
    	}else{
    		$('#marketing', self.el).text('Add more information to stand out!');

    	 	if(self.checkArray(details, 'resume')){
    	 		$('#add-button', self.el).html('<a href="#user/resume" class="video-play"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span>Add Resume</a>')
    	 	}else if(self.checkArray(details, 'video')){
    	 		$('#add-button', self.el).html('<a href="#user/video" class="video-play"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span>Add Self Introduction Video</a>')
    	 	}else if (self.checkArray(details, 'preference')) {
    	 		$('#add-button', self.el).html('<a href="#user/preference" class="video-play"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span>Add Teaching Preference</a>')
    	 	}else {
    	 		// $('#add-button', self.el).html('<button class="btn btn-default">Edit My Profile</button>')
    	 		var t = details[0];
    	 		$('#add-button', self.el).append('<p><a href="#user/profile" class="video-play"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span>Add ' +t+'</a></p>');
    	 	}

    	}
    },

    checkArray: function(arr,obj){
    	return (arr.indexOf(obj) != -1);
    }
})