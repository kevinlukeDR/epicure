directory.CertificationView = Backbone.View.extend({

    initialize: function() {
        // console.log(this.model);
        this.listenTo(this.model, "change", this.render, this);
        this.listenTo(this.model, "destroy", this.remove, this);
    },
    render: function() {
        this.$el.html(this.template(this.model.attributes));
        // // this.$el.on('click', 'span[id^="edit-Ct"]', function(event) {
        //     alert('firing')
        // });
        return this;
    },

    events: {
        'click span[id^="edit-Ct"]': 'editCerti',
        'click span[id^="remove-Ct"]': 'deleteCerti'
    },

    editCerti: function() {

        var modalview = new directory.CertificationModal({
            model: this.model
        });
        $('.app').html(modalview.render().el);

    },

    deleteCerti: function(e) {
        var self = this;
        var url = "/api/profile/certification/" + this.model.id;
        var r = confirm("Are you sure you want delete this certification?");
        if (r == true) {
            self.model.destroy({
                url: url
                    // success: function(response) {
                    //     console.log(response);
                    //     alert("Success!");
                    //     self.destroy();
                    // }
            })
        }
    }
});

directory.CertificationModal = Backbone.Modal.extend({
    // template: _.template('templates/CertificationModal.html'),
                    
    cancelEl: '.cancel',
    submitEl: '.save',
    onRender: function(){
        $('.date-input', this.el).datepicker();
    },


    beforeCancel: function(){
        var r = confirm("You have unsaved changes. Do you want to discard them?");
    },

    
    beforeSubmit: function() {
        var form = $('#edit-certification-form',this.el);

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
                type: {
                    required: true,
                    maxlength: 100
                },
                issuingBody: {
                    required: true,
                    maxlength: 45
                },
                issueDate:{
                    required: true,
                }
            }
        });
        return (form.valid());
    },
    submit: function() {
        var issueDate = new Date($('#issueDate',this.el).val());
        var type = $('#type',this.el).val();
        var issueDate = issueDate.getTime();
        var issuingBody = $('#issuingBody',this.el).val();
        console.log(JSON.stringify(this.model));
        this.model.save({
            type: type,
            issuingBody: issuingBody,
            issueDate: issueDate
        }, {
            success: function(model, response) {
                Backbone.history.loadUrl();
                // console.log(model);
                // console.log(response);
            }
        });
        this.destroy();
        $('.app').empty();
    },
    cancel: function() {
        this.destroy();
        $('.app').empty();
    }


});