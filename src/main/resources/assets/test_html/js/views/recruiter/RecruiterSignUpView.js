directory.RecruiterSignUpView = Backbone.View.extend({
    events: {
        "blur #inputEmail": "checkEmail",
        // "focus #inputPassword": "passwordLimit",
        // "blur #inputPassword": "passwordLimit",
        // "blur #confirmPassword": "checkPassword",
        "click #notSubmit": "contactEmail",
        "change #countryCode": "getCountryCode",
        'keyup textarea': 'limitLength',
        'click #registerButton': 'register'
    },
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
        },
        '[name=phone]': {
            observe: 'phone',
            setOptions: {
                validate: true
            }
        },
        '[name=contactEmail]': {
            observe: 'contactEmail',
            setOptions: {
                validate: true
            }
        },
        '[name=title]': {
            observe: 'title',
            setOptions: {
                validate: true
            }
        },
        '[name=companyAddress]': {
            observe: 'companyAddress',
            setOptions: {
                validate: true
            }
        },
        '[name=companyName]': {
            observe: 'companyName',
            setOptions: {
                validate: true
            }
        },
        '[name=foundTime]': {
            observe: 'foundTime',
            setOptions: {
                validate: true
            }
        },
        '[name=companySize]': {
            observe: 'companySize',
            setOptions: {
                validate: true
            }
        },
        '[name=companyName]': {
            observe: 'companyName',
            setOptions: {
                validate: true
            }
        },
        '[name=companyWebsite]': {
            observe: 'companyWebsite',
            setOptions: {
                validate: true
            }
        },
        '[name=description]': {
            observe: 'description',
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
    },

    // check if the email have been registered
    checkEmail: function() {
        $('.email .glyphicon').remove();
        $('#email-error').remove();
        $('div.email').removeClass("has-success");
        $('div.email').removeClass("has-error");

        var email = $('#inputEmail').val();
        if (email) {
        $.ajax({
                url: "/api/recruiter/test/" + email
            })
            .done(function(data) {
                console.log(data);
                if (data === "Null") {
                    $('div.email').addClass("has-success");
                    $('div.email').append("<span class='glyphicon glyphicon-ok form-control-feedback' aria-hidden='true'></span>");
                    $("#registerButton").prop("disabled", false);
                } else {
                    $('div.email').addClass("has-error");
                    $('div.email').append("<span class='glyphicon glyphicon-remove form-control-feedback' aria-hidden='true'></span>");
                    $('div.email').append('<span class="help-block" id="email-error">Sorry this email has been registered.</span>')
                    $("#registerButton").prop("disabled", true);
                }
            });
            }
    },


    // passwordLimit: function() {
    //     var password = $("#inputPassword").val();
    //     if (password.length < 8) {
    //         $("div#feedback2").text("").hide();
    //         $("p.warning").remove();
    //         $("div#feedback2").addClass("alert-warning");
    //         $("div#feedback2").append("<p class='warning'><span class='glyphicon glyphicon-warning-sign' aria-hidden='true'></span> Your password must be between 6-30 characters and cannot include any spaces.</p>").show();
    //         $("#registerButton").prop("disabled", true);
    //         $("#confirmPassword").prop("disabled", true);
    //     } else {
    //         $("#feedback2").text("").hide();
    //         $("#registerButton").prop("disabled", false);
    //         $("#confirmPassword").prop("disabled", false);
    //     }
    // },

    // checkPassword: function() {
    //     var password = $("#inputPassword").val();
    //     var confirm = $("#confirmPassword").val();
    //     if (confirm === password) {
    //         $("#feedback2").text("").hide();
    //         $("#registerButton").prop("disabled", false);
    //     } else {
    //         $("div#feedback2").text("").hide();
    //         $("p.warning").remove();
    //         $("div#feedback2").addClass("alert-warning");
    //         $("div#feedback2").append("<p class='warning'><span class='glyphicon glyphicon-warning-sign' aria-hidden='true'></span> Password is not matched</p>").show();
    //         $("#registerButton").prop("disabled", true);
    //     }
    // },

    contactEmail: function(e) {
        var email = $('#inputEmail').val();
        $('#contactEmail').val(email);
    },

    getCountryCode: function() {
        var countryCode = $('#countryCode').val();
        $('#phoneNumber').val(countryCode);
        $('#phoneNumber').focus();
    },


    register: function(e) {
        e.preventDefault();
        var $btn = $(e.currentTarget);

        var form = $('#recruiter-register-form');

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
                    maxlength: 45
                },
                lname: {
                    required: true,
                    maxlength: 45
                },
                email: {
                    required: true,
                    email: true,
                    maxlength: 45
                        // remote: '/api/recruiter/test/'
                },
                password: {
                    required: true,
                    rangelength: [6, 30]
                },
                confirmPassword: {
                    required: true,
                    equalTo: "#inputPassword"
                },
                phone: {
                    required: true,
                    maxlength: 45
                },
                contactEmail: {
                    required: true,
                    email: true
                },
                title: {
                    required: true,
                },
                companyName: {
                    required: true,
                    minlength: 3,
                    maxlength: 255
                },
                companyAddress: {
                    required: true,
                    maxlength: 255
                },
                companySize: {
                    required: true,
                },
                foundTime: {
                    required: true,
                    digits: true,
                    rangelength: [4, 4]
                },
                companyWebsite: {
                    url: true
                },
                companyLogo: {
                    accept: "image/*"
                },
                companyPic: {
                    accept: "image/*"
                },
                description: {
                    required: true,
                    maxlength: 5000
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
                },
                companyName: {
                    minlength: "Please enter the full name of your company."
                },
                foundTime: {
                    rangelength: "Please enter 4 digits year."
                },
                companyLogo: {
                    accept: "Please attach only image files."
                },
                companyPic: {
                    accept: "Please attach only image files."
                }
            }
        });

        var data = new FormData(form[0]);
        data.delete('confirmPassword');
        var self = this;

        var url = "/api/recruiter/insert/";
        if (form.valid() === true) {
            $btn.text('Loading...').prop("disabled", true);
            $.ajax({
                url: url,
                method: "POST",
                contentType: false,
                processData: false,
                data: data,
                success: function(data) {
                    console.log(data);
                    // if (data.metadata.error) { 
                    //     $btn.text('Loading...').prop("disabled", true);
                    //     $('#feedback').text(data.metadata.error.error_message).removeClass('hidden');
                    // } else {
                        $btn.text('Loading...').prop("disabled", true);
                        alert("Congratulations! You have successfully sign up! Please go to your mailbox and activate your account!");
                        directory.regsuccessView = new directory.RegSuccessView({
                            model: this.model
                        });
                        directory.regsuccessView.render();
                        self.$content.html(directory.regsuccessView.el);
                    // }



                }
            });
        }
    },
    limitLength: function() {
        var length = $('textarea', this.el).val().length;
        var maxLength = 5000;
        var length = maxLength - length;

        if (length < 0) {
            $('#chars', this.el).addClass('red-text');
            $('#chars', this.el).text(length)
        } else {
            $('#chars', this.el).removeClass('red-text').text(length);
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