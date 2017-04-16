directory.TakePictureModalMain = Backbone.View.extend({
    initialize: function () {
      this.startReadCameraStream();
    },

    render: function () {
      this.$el.html(this.template());
      return this;
    },

    events: {
      'click .cancel': function(e) {
          e.preventDefault();
          this.cancelClicked();
      },
      'click .take-picture-button': function(e) {
          e.preventDefault();
          this.takePictureClicked();
      },
      'click .confirm-picture-button': function(e) {
          e.preventDefault();
          this.confirmPictureClicked();
      },
      'click .reset': function(e) {
          e.preventDefault();
          this.resetClicked();
      },
      'click .download': "downloadClicked"
    },

    downloadClicked: function () {
      $(".download").attr('href', document.getElementById('hidden-canvas').toDataURL());
      $('.download').attr('download', 'big-profile-photo.png');
    },

    resetClicked: function () {
        this.stopReadingCameraStream();
        this.render();
        this.startReadCameraStream();

    },

    takePictureClicked: function () {

      // hide video
      $('.video-container').hide();

      // show image
      $('.image-container').show();

      // convert current frame to png and show it
      var canvas = document.getElementById('hidden-canvas');
      var context = canvas.getContext('2d');
      context.drawImage(document.getElementById('current-picture-video'), 0, 0, 370, 280);

      var data = canvas.toDataURL('image/png');
      document.getElementById('current-image').setAttribute('src', data);

      // show reset and download button
        var navSource = this.$el.find("#taking-picture-nav-template-step2").html();
      // var navSource = $('#taking-picture-nav-template-step2').html();
      $(".recording-container").append(navSource)
    },

    confirmPictureClicked: function () {
      console.log('confirmed');
      var pic = document.getElementById('hidden-canvas').toDataURL().toString();
        $('.confirm-picture-button').remove();
        $('.spinner-container').show();
        $.ajax({
            url: '/api/profile/photo',
            method: "POST",
            contentType: 'application/json',
            processData: false,
            data: JSON.stringify({
                'photo': pic
            }),

            success: (data) => {
                console.log('good');
                this.showSuccessText();
            }
        });
    },

    showSuccessText: function () {
        // show success
        $('.spinner-container').hide();
        $('.upload-success-text').show();
    },

    cancelClicked: function () {
      this.stopReadingCameraStream();
    },

    startReadCameraStream: function () {

      // debugger;
      var mediaConstraints = {video: {width: 370, height: 280}, audio: false};
      navigator.mediaDevices.getUserMedia(mediaConstraints).then(function (stream){

        video = document.getElementById('current-picture-video');
        if ('srcObject' in video) {
          video.srcObject = stream;
          video.play();
          $('video').prop('muted', true); // mute to avoid the echo
        } else {
          video.src = window.URL.createObjectURL(stream);
        }

        localStream = stream;
      }).catch(function (error){
        console.log('error', error);
      });
    },

    stopReadingCameraStream: function () {
      localStream.stop();
      localStream.active = false;
    }


  });

