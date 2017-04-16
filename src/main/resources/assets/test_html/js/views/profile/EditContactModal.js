directory.EditContactModal = Backbone.Modal.extend({
    template: "#edit-contact-modal",
    cancelEl: '.cancel',
    submitEl: '.save',
    events: {
        'click .btn-icon': 'social'
    },

    social: function(e) {
        e.preventDefault();
        var c = '.' + e.target.id;
        $(c).removeClass('hidden');
    },

    beforeCancel: function() {
        var r = confirm("You have unsaved changes. Do you want to discard them?");
        return r;
    },

    submit: function() {
        var self = this;
        var formValues = {
            personalWebsite: $('.personalWebsite').val(),
            linkedin: $('.linkedin').val(),
            twitter: $('.twitter').val(),
            facebook: $('.facebook').val(),
            wechat: $('.wechat').val(),
            instagram: $('.instagram').val()
        };
        console.log(formValues);
        var basic = new directory.Contact({
            id: this.model.attributes.id
        });
        basic.save({
            personalWebsite: $('.personalWebsite').val(),
            linkedin: $('.linkedin').val(),
            twitter: $('.twitter').val(),
            facebook: $('.facebook').val(),
            wechat: $('.wechat').val(),
            instagram: $('.instagram').val()
        }, {
            success: function(model, response) {
                self.model.set(formValues);
                // console.log(model);
                // console.log(response);
            }
        })

    }
})