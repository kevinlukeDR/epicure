directory.UserGuideView = Backbone.View.extend({
	render: function() {
		this.$el.html(this.template(this.model.attributes));
		$('#upload-avatar', this.el).slim({
            ratio: '1:1',
            size: '160,160',
            maxFileSize: 5,
            label: 'Drop here!'
        });
        this.loadQ();
		return this;
	},
	 events: {
        "click #uploadAvatar": function(e) {
            e.preventDefault();
            this.uploadAvatar();
        },
        'click #uploadResume': 'getData',
         "click .open-recording": function(e) {
            e.preventDefault();
            this.takeVideo();
        },
        'drop #drop-zone': 'drop',
        'dragover #drop-zone': 'drag',
        'dragleave #drop-zone': 'dragl',
    },
    loadQ: function(){
        $('#interview-questions',this.el).load('docu/InterviewQ.html');
    },
    takeVideo: function() {
        var modal = new directory.RecordingModal();
        $('.recording-modal').html(modal.render().el);
        $('#recordingModal').modal('show');
        $('#recordingModal').on('hidden.bs.modal', function() {
            modal.childView.cancelClicked();
        })
    },
    uploadAvatar: function() {
        var form = $("#avatar-form");
        var data = new FormData(form[0]);

        // for (var pair of data.entries()) {
        //     console.log(pair[0] + ', ' + pair[1]);
        // };

         $.ajax({
            url: '/api/profile/profilepic',
            method: "POST",
            contentType: false,
            processData: false,
            data: data,
            
            success: function(data) {
                console.log(data);
                $('.alert-pic').removeClass('hidden');
            }
        });
         // $('.alert-pic').html('Success').removeClass('hidden');
    },
    drop: function(e) {
        e.preventDefault();
        $(e.currentTarget).addClass('upload-drop-zone');
        console.log(e.originalEvent.dataTransfer.files)
        var data = new FormData();
        data.append('file', e.originalEvent.dataTransfer.files[0]);
        this.uploadResume(data);
    },

    drag: function(e) {
        e.preventDefault();
        $(e.currentTarget).addClass('drop');
        return false;
    },
    dragl: function(e) {
        e.preventDefault();
        $(e.currentTarget).removeClass('drop');
        return false;
    },
    getData: function(e) {
        e.preventDefault();
        var $btn = $(e.currentTarget);
        $btn.text('Loading...').prop("disabled", true);
        var form = $('#resume-form');
        form.validate({
            errorElement: 'span',
            errorClass: 'help-block',
            highlight: function(element, errorClass, validClass) {
                $(element).closest('.form-group').addClass("has-error");
            },
            unhighlight: function(element, errorClass, validClass) {
                $(element).closest('.form-group').removeClass("has-error");
            },
            rules: {
                file: {
                    required: true,
                    extension: "pdf|doc|docx"
                }
            },
            messages: {
                file:{
                    required: 'Please attach a file',
                    extension: 'Please attach only pdf, doc or docx files'
                }
            }
        });

        var data = new FormData(form[0]);
        if (form.valid() === true) {
            this.uploadResume(data);
        }else{
            $btn.text('Submit').prop("disabled",false);
        }
        $btn.text('Submit').prop("disabled",false);
    },
    uploadResume: function(file) {
        $('.alert-profile').addClass('hidden'); //hidden all status message
        var url = "/api/resume/update/";
        var self = this;
        $.ajax({
            url: url,
            method: "PUT",
            contentType: false,
            processData: false,
            data: file,
            dataType: "json",
            success: function(data) {
                console.log(data);
                $('.alert-profile').removeClass('hidden');
                // console.log(data.)
            }
        });
    },
    // uploadResume: function() {
    //     $('.alert-profile').addClass('hidden'); //hidden all status message

    //     var form = $('#resume-form');

    //     var data = new FormData(form[0]);

    //     // for (var pair of data.entries()) {
    //     //     console.log(pair[0] + ', ' + pair[1]);
    //     // }
    //     var url = "/api/resume/update/";

    //     $.ajax({
    //         url: url,
    //         method: "PUT",
    //         contentType: false,
    //         processData: false,
    //         data: data,
    //         dataType: "json",
    //         success: function(data) {
    //             console.log(data);
    //             $('.alert-profile').removeClass('hidden');
    //         }
    //     });
    // }

});