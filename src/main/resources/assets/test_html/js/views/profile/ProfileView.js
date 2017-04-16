directory.ProfileView = Backbone.View.extend({
    initialize: function(){
        this.model.on("change", this.render, this);
    },

    render: function() {
        this.$el.html(this.template(this.model.attributes));
        $('#upload-avatar', this.el).slim({
            ratio: '1:1',
            size: '240,240',
            maxFileSize: 5,
            label: 'Drop here!'
        });
        $('select[name="gender"]', this.el).val(this.model.attributes.gender);
        $('select[name="nationality"]', this.el).val(this.model.attributes.nationality);
        $('select[name="gender"]', this.el).val(this.model.attributes.gender);

        return this;
    },

    events: {
        "click .open-take-picture": function(e) {
            e.preventDefault();
            this.takePhoto();
        },
        "click .open-recording": function(e) {
            e.preventDefault();
            this.takeVideo();
        },
        "click #remove-button": "removeAvatar",
        "click #uploadAvatar": function(e) {
            e.preventDefault();
            this.uploadAvatar();
        },
        "click #uploadResume": function(e) {
            e.preventDefault();
            this.uploadResume();
        },
        "click #updateProfile": function(e) {
            e.preventDefault();
            this.updateProfile();
        }
    },
    takePhoto: function () {
        var modal = new directory.TakePictureModal();
        $('.take-picture-modal').html(modal.render().el);
        $('#takePictureModal').modal('show');
        $('#takePictureModal').on('hidden.bs.modal', function () {
            modal.childView.cancelClicked();
        })
    },

    takeVideo: function () {
        var modal = new directory.RecordingModal();
        $('.recording-modal').html(modal.render().el);
        $('#recordingModal').modal('show');
        $('#recordingModal').on('hidden.bs.modal', function () {
            modal.childView.cancelClicked();
        })
    },

    removeAvatar: function(){
        var cropper = Slim.find($('#my-cropper'));
        cropper.remove();
    },

    uploadAvatar: function() {
        var form = $("#avatar-form");
        var data = new FormData(form[0]);

         $.ajax({
            url: '/api/profile/profilepic',
            method: "POST",
            contentType: false,
            processData: false,
            data: data,
            
            success: function(data) {
                console.log(data);
                $('.alert-pic').html(data).removeClass('hidden');
            }
        });
         // $('.alert-pic').html('Success').removeClass('hidden');
    },

    uploadResume: function() {
        $('.alert-profile').addClass('hidden'); //hidden all status message

        var form = $('#resume-form');

        var data = new FormData(form[0]);

        // for (var pair of data.entries()) {
        //     console.log(pair[0] + ', ' + pair[1]);
        // }
        var url = "/api/resume/update/";

        $.ajax({
            url: url,
            method: "PUT",
            contentType: false,
            processData: false,
            data: data,
            dataType: "json",
            success: function(data) {
                console.log(data);
                $('.alert-profile').html('success').removeClass('hidden');
            }
        });
        // $('.alert-profile').html('Success').removeClass('hidden');
    },

    updateProfile: function() {
        var url = "/api/profile/update/";
        console.log(this.model.id);
        var data = $('#update-profile-form-old').serializeObject();

        console.log(data);
        data.id = this.model.id;
        directory.arrangeJson(data);
        var d = JSON.stringify(data);

        console.log(d);
        
        $.ajax({
            url: url,
            type: 'PUT',
            
            contentType: "application/json",
            data: d,
            processData: false,
            statusCode: {
                401: function() {
                    alert('Please Login First!')
                }
            },
            success: function(data) {
                if (data == 'Successful') {
                    alert("Update Profile Successfully!");
                    
                } else {
                    console.log(data);
                    $('#feedback').text(data).show();
                    alert(data);
                }
            }
        });
    }


});