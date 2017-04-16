directory.ExperienceView = Backbone.View.extend({

    initialize: function() {
        // console.log(this.model);
        this.listenTo(this.model, "change", this.render, this);
        this.listenTo(this.model, "destroy", this.remove, this);
    },
    render: function() {
        this.$el.html(this.template(this.model.attributes));
        return this;
    },

    events: {
        'click span[id="edit-exp"]': 'editExp',
        'click span[id="remove-exp"]': 'deleteExp'
    },

    editExp: function() {

        var modalview = new directory.ExperienceModal({
            model: this.model
        });
        $('.app').html(modalview.render().el);

    },

    deleteExp: function(e) {
        var self = this;
        var url = "/api/profile/relevant/" + this.model.id;
        var r = confirm("Are you sure you want delete this experience?")
        if (r == true) {
            self.model.destroy({
                url: url
            })
        }
    }
});

directory.ExperienceModal = Backbone.Modal.extend({
    cancelEl: '.cancel',
    submitEl: '.save',

    events: {
        'keyup textarea': 'limitLength'
    },
    onRender: function(){
        this.dateRender();
    },

    dateRender: function() {
        var self = this;    
        
        var fromDate = $('#fromDate', this.el).datepicker().on('changeDate', function(ev) {
            if (ev.date.valueOf() > toDate.date.valueOf()) {
                var newDate = new Date(ev.date);
                newDate.setDate(newDate.getDate() + 1);
                toDate.setValue(newDate);
            }
            fromDate.hide();
            $('#toDate', self.el)[0].focus();
        }).data('datepicker');

        var toDate = $('#toDate',this.el).datepicker({
            onRender: function(date){
                return date.valueOf() <= fromDate.date.valueOf() ?'disabled' : '';
            }
        }).on('changeDate',  function(ev) {
            toDate.hide()
        }).data('datepicker');

    },
    beforeCancel: function(){
        var r = confirm("You have unsaved changes. Do you want to discard them?");
        return r;
    },
    beforeSubmit: function() {
        var form = $('#edit-experience-form',this.el);

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
                role:{
                    required: true,
                    maxlength: 45,
                },
                company:{
                    required: true,
                    maxlength: 45,
                },
                responsibility:{
                    required: true,
                    maxlength: 255,
                }
            }
        });
        return (form.valid());
    },

    submit: function() {
        var fromDate = new Date($('input[name="fromDate"]',this.el).val());
        fromDate = fromDate.getTime();

        var toDate = new Date($('input[name="toDate"]',this.el).val());
        toDate = toDate.getTime();

        var role = $('input[name="role"]',this.el).val();
        var company = $('input[name="company"]',this.el).val();
        var responsibility = $('textarea').val();


        console.log(JSON.stringify(this.model));
        this.model.save({
            role: role,
            company: company,
            responsibility: responsibility,
            fromDate: fromDate,
            toDate: toDate
        }, {
            success: function(model, response) {
                Backbone.history.loadUrl();
                // console.log(model);
                // console.log(response);
            }
        })
        this.destroy();
        $('.app').empty();
    },
    cancel: function() {
        this.destroy();
        $('.app').empty();
    },

    limitLength: function(){
        var length = $('textarea',this.el).val().length;
        var maxLength = 255;
        var length = maxLength - length;
        
        if (length < 0) {
            $('#chars', this.el).addClass('red-text');
            $('#chars', this.el).text(length)
        }else{
            $('#chars', this.el).removeClass('red-text').text(length);
        }
    }


})