directory.RegisterView = Backbone.View.extend({
    events: {
        "blur #inputEmail": "checkEmail",
        // "focus #inputPassword": "passwordLimit",
        // "blur #inputPassword": "passwordLimit",
        // "blur #confirmPassword": "checkPassword",
        'click #registerButton': 'register',
        'click #importButton': 'import'
    },
    //use stickit to perform binding

    bindings: {
        '[name=fname]': {
            observe: 'fname',
            setOptions: {
                validate: true
            }
        },
        '[name=lname]': {
            observe: 'lname',
            setOptions: {
                validate: true
            }
        },
        '[name=email]': {
            observe: 'email',
            setOptions: {
                validate: true
            }
        },
        '[name=password]': {
            observe: 'password',
            setOptions: {
                validate: true
            }
        }
    },
    initialize: function() {
        // console.log('Initializing Register View');
        Backbone.Validation.bind(this);
    },

    render: function() {
        this.$el.html(this.template(this.model.attributes));
        this.$content = $('#content');
        this.stickit();
        $(".modal-body", this.el).load("docu/teachoverseaPrivacy.html")
        return this;
    },

    checkEmail: function() {
        $('.email .glyphicon').remove();
        $('#email-error').remove();
        $('.help-block').remove()
        $('div.email').removeClass("has-success");
        $('div.email').removeClass("has-error");
        $("#importButton").attr('id', 'registerButton');
        var self = this;
        self.uuid = "";

        var email = $('#inputEmail').val();
        if (email) {
        $.ajax({
                url: "/api/candidateLogin/test/" + email
            })
            .done(function(data) {
                console.log(data);
                if (data.metadata.status == "ok") {
                    $('div.email').addClass("has-success");
                    $('div.email').append("<span class='glyphicon glyphicon-ok form-control-feedback' aria-hidden='true'></span>");
                    $("#registerButton").prop("disabled", false);

                }else if (data.metadata.status == "import") {
                    $('div.email').addClass("has-success");
                     $('div.email').append("<span class='glyphicon glyphicon-ok form-control-feedback' aria-hidden='true'></span>");
                    $('div.email').append('<span class="help-block">You have filled the application form on TeachOversea before. We will import your profile information.</span>');
                    $("#registerButton").prop("disabled", false).attr('id', 'importButton');
                    self.uuid = data.data.uuid;
                }else {
                    $('div.email').addClass("has-error");
                    $('div.email').append("<span class='glyphicon glyphicon-remove form-control-feedback' aria-hidden='true'></span>");
                    $('div.email').append('<span class="help-block" id="email-error">Sorry this email has been registered.</span>')
                    $("#registerButton").prop("disabled", true);
                }
            });
            }
    },


    register: function(e) {

        e.preventDefault();

        var url = '/api/candidate/insert/';
        var self = this;
        var form = $('#register-form');
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
                fname: {
                    required: true,
                    maxlength: 30
                },
                lname: {
                    required: true,
                    maxlength: 30
                },
                email: {
                    required: true,
                    email: true,
                    maxlength: 45
                },
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
                fname: {
                    required: "First Name is required."
                },
                lname: {
                    required: "Last Name is required."
                },
                email: {
                    required: "Email Address is requied."
                },
                password: {
                    rangelength: "Password must be between 6-30 characters and cannot include any spaces."
                },
                confirmPassword: {
                    equalTo: "Password does not match."
                }
            }
        });

        //disable button 
        var $btn = $(e.currentTarget);

        if (form.valid() === true) {

            $btn.text('Loading...').prop("disabled", true);

            var data = $('#register-form').serializeObject();
            delete data.confirmPassword;
            // console.log(data);
            self.model.set(data);
            // var data = $('#register-form').serializeObject();
            if (self.model.isValid(true)) {
                self.model.save({
                    error: function() {
                        alert("error");
                        $btn.text('Login').prop("disabled", false);
                    }
                });
                $btn.text('Login').prop("disabled", false);
                // console.log(this.model.get("email"));
                directory.regsuccessView = new directory.RegSuccessView({
                    model: this.model
                });
                directory.regsuccessView.render();
                self.$content.html(directory.regsuccessView.el);
            } else {
                alert("All the fields are required!");
            }
        }
    },
    import: function(e) {
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
            console.log(data);

            var reg = new directory.StepFive();
            reg.save(data, {
                success: function(model, resp) {
                    console.log(model);
                    if (resp.metadata.status == "ok") {
                          $btn.text('Login').prop("disabled", false);
                        // console.log(model);
                        directory.regsuccessView = new directory.RegSuccessView({model: model});
                        directory.regsuccessView.render();
                        self.$content.html(directory.regsuccessView.el);
                        // $('#register-content').html('<div class="alert alert-success">Your account has\
                        //  been activated! You will receive an email confirmation. <a href="#login">Log \
                        //  in</a> now and manage your account. </div>')
                    } else {
                        alert(resp.metadata.error.error_message);
                        $btn.text('Register').prop("disabled", false);
                    }
                }
            })
        } else {
            $btn.text('Register').prop("disabled", false);
        }
    },

    remove: function() {
        Backbone.Validation.unbind(this);
        return Backbone.View.prototype.remove.apply(this, arguments);
    },
    destroy: function() {
        this.undelegateEvents();
        this.$el.removeData().unbind();
        this.remove();
    }


});