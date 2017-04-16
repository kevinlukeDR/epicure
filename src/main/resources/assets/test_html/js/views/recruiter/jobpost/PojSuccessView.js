directory.PojSuccessView = Backbone.View.extend({
    initialize: function(attrs) {
        this.job_id = this.model.attributes.id;
        console.log(this.job_id)
    },
    render: function() {
        this.$el.html(this.template(this.model.attributes));
        return this;
    },

    events: {
        "click #publishButton": function(e) {
            e.preventDefault();
            this.publish();
        },
        'click #discardButton': function(e){
            e.preventDefault();
            this.discard();
        }
    },

    publish: function() {
 
        var self = this;
        var url = "/api/job/publish/" + this.job_id;

        $.ajax({
            url: url,
            method: "PUT",
            contentType: false,
            processData: false,
            success: function(data) {
                console.log(data);
                alert('Congratulations! Your job has been published!');
                window.location.hash = 'recruiter/postJob/tag/'+ self.job_id;
                // window.location.hash='recruiter/jobManagement';
            }
        });
    },

    discard: function(){
        var self = this;
        var url = "/api/job/close/" + this.job_id;
        var r = confirm("Are you sure you want to close this job?");
        if (r == true) {
            $.ajax({
                url: url,
                method: "PUT",
                contentType: false,
                processData: false,
                success: function(data) {
                    console.log(data);
                    alert('Your job has been closed.');
                    window.location.hash='recruiter/jobManagement';
                }
            });
        }
        
        
       
    }
});