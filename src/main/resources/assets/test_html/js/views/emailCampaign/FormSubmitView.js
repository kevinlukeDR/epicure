directory.FormSubmitView = Backbone.View.extend({
    initialize: function(attr) {
        this.uuid = attr;
    },
    render: function() {
        this.$el.html(this.template());
        this.$content = $('#content');
        $(".modal-body", this.el).load("docu/teachoverseaPrivacy.html")
        return this;
    },
    events: {
        // 'change #sign-up': 'showRegister',
        'click #sign-up-button': 'register',
        'click #not-sign': 'notsign'
    },

    showRegister: function() {
        if ($('#sign-up').is(':checked')) {
            $('#register-form').removeClass('hidden');
        } else {
            $('#register-form').addClass('hidden')
        }
    },
    register: function(e) {
        e.preventDefault();
        var $btn = $(e.currentTarget);
        var form = $('#register-form');
        var self = this;
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
                password: {
                    required: true,
                    rangelength: [6, 30]
                },
                confirmPassword: {
                    required: true,
                    equalTo: "#inputPassword"
                }
            },
            messages: {
                password: {
                    rangelength: "Password must be between 6-30 characters and cannot include any spaces."
                },
                confirmPassword: {
                    equalTo: "Password does not match."
                }
            }
        });
        if (form.valid() === true) {
            $btn.text('Loading...').prop("disabled", true);
            
            var password = $('#inputPassword').val();
            var data = {
                'password': password,
                'uuid': self.uuid
            };
            var reg = new directory.StepFive();
            reg.save(data,{
                success: function(model, resp){
                    console.log(resp);
                    if (resp.metadata.status == "ok") {
                         $btn.text('Sign up and get hired').prop("disabled", false);
                        directory.regsuccessView = new directory.RegSuccessView({model: model});
                        directory.regsuccessView.render();
                        self.$content.html(directory.regsuccessView.el);

                        // $('#register-content').html('<div class="alert alert-success">Your account has\
                        //  been activated! You will receive an email confirmation. <a href="#login">Log \
                        //  in</a> now and manage your account. </div>')
                    }else{
                        alert(resp.metadata.error.error_message);
                        $btn.text('Sign up and get hired').prop("disabled", false);
                    }
                }
            })
            
            
        } else {
            $btn.text('Sign up and get hired').prop("disabled", false);
        }
    },
    notsign: function(e) {
        e.preventDefault();
        alert('Your application has received. Thank you!');
        window.location.hash = '';
    }
})