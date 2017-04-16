directory.TakePictureModal = Backbone.View.extend({

        render: function () {
            this.$el.html(this.template({modalID: 'takePictureModal'}));
            this.childView = new directory.TakePictureModalMain();
            this.childView.$el = this.$(".modal-child-view");
            this.childView.render();
            this.childView.delegateEvents();

            return this;
        }

    });


