directory.RecordingModal = Backbone.View.extend({


    render: function () {
    this.$el.html(this.template({modalID: 'recordingModal'}));
    this.childView = new directory.RecordingModalMain();
    this.childView.$el = this.$(".modal-child-view");
    this.childView.render();
    this.childView.delegateEvents();

    return this;
      // $('#recordingModal').on('hide.bs.modal', function () {
      //   console.log('here');
      // })
    }

  });

