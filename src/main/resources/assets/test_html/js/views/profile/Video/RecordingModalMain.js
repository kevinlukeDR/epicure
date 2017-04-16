directory.RecordingModalMain = Backbone.View.extend({

    initialize: function () {
        this.startReadCameraStream();
        this.time = new directory.Time({seconds: 0});
        this.time.on('timeUpdated', function (timeString) {

            // update time on view
            // $('.time').html(`${timeString} / ${this.time.getMaxMinutes()}:00`);
            $('.time').html(timeString + " / " + this.time.getMaxMinutes() + ":00");

            // update progress bar on view
            this.$(".progress-bar").css("width", this.time.getPercentage());
            // this.$(".progress-bar-text").text(`${this.time.getPercentage()}`);
            this.$(".progress-bar-text").text(this.time.getPercentage());

            // change color base on progress
            // 20% red
            // 40% yellow
            // 60% blue
            // 80% green
            this.updateProgressBar();

            // test if reached time limit
            if (this.time.get('seconds') >= this.time.get('maxSeconds')) {
                this.doneClicked();

            }

        }, this);

    },

    updateProgressBar: function () {
        if (this.time.getPercentageFloat() < 0.2) {
            this.$('.progress-bar').toggleClass('progress-bar-danger', true);
        } else {
            this.$('.progress-bar').toggleClass('progress-bar-danger', false);
        }

        if (this.time.getPercentageFloat() < 0.4) {
            this.$('.progress-bar').toggleClass('progress-bar-warning', true);
        } else {
            this.$('.progress-bar').toggleClass('progress-bar-warning', false);
        }

        if (this.time.getPercentageFloat() < 0.6) {
            this.$('.progress-bar').toggleClass('progress-bar-info', true);
        } else {
            this.$('.progress-bar').toggleClass('progress-bar-info', false);
        }

        if (this.time.getPercentageFloat() < 0.8) {
            this.$('.progress-bar').toggleClass('progress-bar-success', true);
        } else {
            this.$('.progress-bar').toggleClass('progress-bar-success', false);
        }


    },

    // template: function () {
    //   var source = $("#recording-modal-container-template").html();
    //   return _.template(source);
    // },

    render: function () {
        this.$el.html(this.template());
        return this;
    },

    events: {
        'click .start-recording': function (e) {
            e.preventDefault();
            this.startRecordingClicked();
        },
        'click .reset': function (e) {
            e.preventDefault();
            this.resetClicked();
        },
        'click .done': function (e) {
            e.preventDefault();
            this.doneClicked();
        },
        'click .save': function (e) {
            e.preventDefault();
            this.saveClicked();
        },
        'click .cancel': function (e) {
            e.preventDefault();
            this.cancelClicked();
        },
        'click .upload-video-button': function (e) {
            e.preventDefault();
            this.uploadClicked();
        }
    },

    uploadClicked: function () {
        $('.upload-video-button').remove();
        $('.spinner-container').show();
        var blob = recordRTC.getBlob();
        var formData = new FormData();
        formData.append('video', blob);
        $.ajax({
            url: '/api/video',
            method: "POST",
            contentType: false,
            processData: false,
            data: formData,

            success: (data) => {
                console.log('good');
                this.showSuccessText();
            }
        });
    },

    cancelClicked: function () {
        // this.resetVars();
        this.stopReadingCameraStream();
        this.time.stopTimer();
        this.time.resetTimer();
        this.stopDetectNude();

    },

    // destroy: function() {
    //     this.undelegateEvents();
    //     this.$el.removeData().unbind();
    //     this.remove();
    // },

    startRecording: function () {

        // RecordRTC usage goes here
        var options = {
            mimeType: 'video/mp4',
            audioBitsPerSecond: 64000,
            videoBitsPerSecond: 64000
        };

        recordRTC = RecordRTC(localStream);
        recordRTC.startRecording();
        // start detect nude
        this.startDetectNude();
    },

    startReadCameraStream: function () {
        // debugger;
        var mediaConstraints = {video: {width: 370, height: 280}, audio: true};
        navigator.mediaDevices.getUserMedia(mediaConstraints).then(function (stream) {

            showStreamAtVideoElement(stream, "current-recording-video", function (stream) {

                $('#current-recording-video').prop('muted', true);
                localStream = stream;

            })

        }).catch(function (error) {
            console.log('error', error);
        });
    },

    stopReadingCameraStream: function () {
        localStream.stop();

    },

    startRecordingClicked: function () {
        $('.start-recording').click(false);

        var modalMain = this;
        // start count down


        var countdown = 3;
        $('.start-recording-text').addClass('countdown');
        $('.start-recording-text').text(countdown);

        var countdownInterval = setInterval(function () {
            countdown = countdown - 1;
            $('.start-recording-text').addClass('countdown');
            $('.start-recording-text').text(countdown);
            if (countdown <= -1) {
                clearInterval(countdownInterval);
                startRecordingCallback();
            }
        }, 1000);


        var startRecordingCallback = function () {
            // remove start recording
            $(".start-recording").remove();

            // add step 2 navigation
            var template = _.template($("#recording-nav-template-step2").html());
            $(".recording-container").append(template());
            modalMain.time.startTimer();
            // start recording stream with RecordRTC
            modalMain.startRecording();
        };

        // startRecordingCallback(); //testing


        var bgWidth = 195;
        var recordingButtonWidth = 230;
        var countDownBackgroundAnimationInterval = setInterval(function () {

            // make it smaller and smaller
            bgWidth = bgWidth - 1;
            $('.recording-button-fancy-inside').css('width', bgWidth + 'px');
            $('.recording-button-fancy-inside').css('height', bgWidth + 'px');

            if (bgWidth <= 0) {
                clearInterval(countDownBackgroundAnimationInterval);
            }

        }, 20);

    },

    stopRecording: function (callback) {

        var modalMain = this;
        recordRTC.stopRecording(function (audioVideoWebMURL) {
            callback(audioVideoWebMURL);
            modalMain.stopReadingCameraStream();
            modalMain.stopDetectNude();
        });

    },

    resetClicked: function () {
        this.render();
        this.resetVars();
        this.startReadCameraStream();
    },

    resetVars: function () {
        this.stopReadingCameraStream();
        this.time.stopTimer();
        this.time.resetTimer();
        this.stopDetectNude();
    },
    showSuccessText: function () {
        // show success
        $('.spinner-container').hide();
        $('.upload-success-text').show();

    },

    saveClicked: function () {
        this.saveRecording();
        // this.saveRecording();

    },

    saveRecording: function () {
        recordRTC.save();
        this.nudeHasDetected = false; // reset the value
    },

    startDetectNude: function () {
        this.nudeHasDetected = false; // init as false

        // start detecting nude
        this.nudeInterval = setInterval(function () {

            loadImageByElement(document.getElementById('current-recording-video'));
            var result = scanImage();

            if (result) {
                this.nudeHasDetected = true;
                console.log("Nudity detected.");
            } else {
                console.log("Nudity not detected.");
            }

        }, 1000)
    },

    stopDetectNude: function () {
        clearInterval(this.nudeInterval);
        console.log('if nude has been detected in this video: ' + this.nudeHasDetected);
    },

    doneClicked: function () {
        $(".recording-nav").remove(); // remove existing nav

        // add step 3 navigation
        var template = _.template($("#recording-nav-template-step3").html());
        $(".recording-container").append(template());
        this.time.stopTimer();

        // let RecordRTC stop recording
        this.stopRecording(function (url) {

            // show preview in the video area
            $("#current-recording-video").prop('src', url);
            $("#current-recording-video").prop('muted', false); // unmute

            // show control
            $("#current-recording-video").prop('controls', true);

        });
    }
});

