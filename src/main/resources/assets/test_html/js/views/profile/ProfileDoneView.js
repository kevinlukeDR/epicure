directory.ProfileDoneView = Backbone.View.extend({
    initialize: function() {
        this.listenTo(this.model, "change", this.render, this);
    },
    render: function() {
        this.$el.html(this.template(this.model.attributes));
        $('#upload-avatar', this.el).slim({
            ratio: '1:1',
            size: '100,100',
            maxFileSize: 5,
            label: 'Drop or Click here!'
        });
        $('.date-input', this.el).datepicker();
        var url = 'https://s3.us-east-2.amazonaws.com/esl-public/' + this.model.attributes.picture.path;
        console.log(url);
        // $('#upload-avatar', this.el).slim('load',url,function(error,data){
        //     console.log(data)
        // });
        //education 
        this.renderEdu();
        // Certification
        this.renderCt();

        //Experience
        this.renderExp();
        this.renderStrength();
    },

    renderStrength: function(){
        var strength = new directory.Strength();
        var self = this;
        strength.fetch({
            success: function(model, response){
                if (response.metadata.status == "ok") {
                    
                    $('#profile-strength', self.el).html(new directory.ProfileStrView({model: model}).render().el)
                }
            }
        })
        
    },
    renderEdu: function() {
        directory.educations = new directory.EducationCollection({
            id: this.model.attributes.educationExperiences.id
        });
        directory.educations.set(this.model.attributes.educationExperiences);

        var view = new directory.EducationContentView({
            collection: directory.educations
        });
        view.delegateEvents();
        view.render();
        $('#education-content', this.el).empty();
        $('#education-content', this.el).append(view.el);
    },
    renderCt: function() {
        directory.certifications = new directory.CertificationCollection({
            id: this.model.attributes.certifications.id
        });
        directory.certifications.set(this.model.attributes.certifications);

        _.each(directory.certifications.models, function(certificate) {
            $('#certification-content', this.el).append(new directory.CertificationView({
                model: certificate
            }).render().el);
        }, this);
    },
    renderExp: function() {
        directory.experienceCollection = new directory.ExperienceCollection({
            id: this.model.attributes.relaventExperiences.id
        });
        directory.experienceCollection.set(this.model.attributes.relaventExperiences);
        _.each(directory.experienceCollection.models, function(exp) {
            $('#experience-content', this.el).append(new directory.ExperienceView({
                model: exp
            }).render().el);
        }, this);
    },


    events: {
        'click #replaceAvatar': function(e) {
            e.preventDefault();
            this.replaceAvatar();
        },
        'click #uploadAvatar': 'uploadAvatar',
        "click .open-take-picture": function(e) {
            e.preventDefault();
            this.takePhoto();
        },

        // "click .open-recording": function(e) {
        //     e.preventDefault();
        //     this.takeVideo();
        // },
        'click #edit-basic': function(e) {
            e.preventDefault();
            this.editBasic();
        },
        'click #edit-contact': function(e) {
            e.preventDefault();
            this.editContact();
        },
        'click #add-edu': function(e) {
            e.preventDefault();
            this.addEdu();
        },
        'click #add-exp': function(e) {
            e.preventDefault();
            this.addExp();
        },

        'click #add-Ct': function(e) {
            e.preventDefault();
            this.addCerti();
        }
    },
    replaceAvatar: function() {
        $('.avatar-img').remove();
        $('#avatar-form').removeClass('hidden');

    },

    takePhoto: function() {
        var modal = new directory.TakePictureModal();
        $('.take-picture-modal').html(modal.render().el);
        $('#takePictureModal').modal('show');
        $('#takePictureModal').on('hidden.bs.modal', function() {
            modal.childView.cancelClicked();
            Backbone.history.loadUrl();
        })
    },

    uploadAvatar: function(e) {
        e.preventDefault();
        var $btn = $(e.currentTarget);
        $btn.text('Loading...').prop("disabled", true);

        var form = $("#avatar-form");
        var data = new FormData(form[0]);
        var self = this;

        $.ajax({
            url: '/api/profile/profilepic',
            method: "POST",
            contentType: false,
            processData: false,
            data: data,

            success: function(data) {
                console.log(data);
                $btn.text('Submit').prop("disabled", false);
                if (data.metadata.error) {
                    alert(data.metadata.error.error_message)
                } else {
                    $('.alert-pic').show("slow").delay(5000).fadeOut();
                    form.addClass('hidden');
                    window.location.reload();
                    // $('.avatar-holder').html('<img src="https://s3.us-east-2.amazonaws.com/esl-public/' + data.data.path +
                    //     '" style="height:106px;width:106px;border-radius: 50%" alt="Avatar" class="avatar-img center-block mb8">');
                }

            }
        });
        // $('.alert-pic').html('Success').removeClass('hidden');
    },
    editBasic: function() {
        var modalview = new directory.EditBasicModal({
            model: this.model
        });
        $('.edit-basic').html(modalview.render().el);
    },

    editContact: function() {
        var modalview = new directory.EditContactModal({
            model: this.model
        });
        $('.edit-contact').html(modalview.render().el);
    },
    addEdu: function() {
        var modalview = new directory.AddEduModal();
        $('.add-edu').html(modalview.render().el);
    },
    addExp: function() {
        var modalview = new directory.AddExpModal();
        $('.add-exp').html(modalview.render().el);
    },
    addCerti: function() {
        var modalview = new directory.AddCtModal();
        $('.add-Ct').html(modalview.render().el);
    },
    destroy: function() {
        this.undelegateEvents();
        this.$el.removeData().unbind();
        this.remove();
    }

});



directory.AddCtModal = Backbone.Modal.extend({
    template: "#add-certification-modal",
    cancelEl: '.cancel',
    submitEl: '.save',
    onRender: function() {
        $('.date-input', this.el).datepicker();
    },
    beforeCancel: function() {
        var r = confirm("You have unsaved changes. Do you want to discard them?");
        return r;
    },

    beforeSubmit: function() {
        var form = $('#add-certification-form');

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
                issueDate: {
                    required: true,
                }
            }
        });
        return (form.valid());
    },


    submit: function() {
        var issueDate = new Date($('input[name="issueDate"]').val());
        var type = $('input[name="type"]').val();
        var issueDate = issueDate.getTime();
        var issuingBody = $('input[name="issuingBody"]').val();
        var certification = new directory.Certification();
        var formValues = {
            type: type,
            issuingBody: issuingBody,
            issueDate: issueDate
        };
        certification.set(formValues);
        certification.save({
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

        // console.log(certification);
        // $('#certification-content').append(new directory.CertificationView({
        //     model: certification
        // }).render().el);

    }
});

directory.AddExpModal = Backbone.Modal.extend({
    template: "#add-experience-modal",
    cancelEl: '.cancel',
    submitEl: '.save',

    events: {
        'keyup textarea': 'limitLength'
    },
    onRender: function() {
        // $('.date-input', this.el).datepicker();
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
    beforeCancel: function() {
        var r = confirm("You have unsaved changes. Do you want to discard them?");
        return r;
    },

    beforeSubmit: function() {
        var form = $('#add-experience-form');

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
                role: {
                    required: true,
                    maxlength: 45,
                },
                company: {
                    required: true,
                    maxlength: 45,
                },
                responsibility: {
                    required: true,
                    maxlength: 255,
                }
            }
        });
        return (form.valid());
    },


    submit: function() {
        var fromDate = new Date($('input[name="fromDate"]', this.el).val());
        fromDate = fromDate.getTime();

        var toDate = new Date($('input[name="toDate"]', this.el).val());
        toDate = toDate.getTime();

        var role = $('input[name="role"]', this.el).val();
        var company = $('input[name="company"]', this.el).val();
        var responsibility = $('textarea',this.el).val();

        var experience = new directory.Experience();
        experience.save({
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

                // $('#experience-content').append(new directory.ExperienceView({
                //     model: model
                // }).render().el);
            }
        })
    },

    limitLength: function() {
        var length = $('textarea').val().length;
        var maxLength = 255;
        var length = maxLength - length;

        if (length < 0) {
            $('#chars').addClass('red-text');
            $('#chars').text(length)
        } else {
            $('#chars').removeClass('red-text').text(length);
        }
    },
});

directory.AddEduModal = Backbone.Modal.extend({
    template: "#add-education-modal",
    cancelEl: '.cancel',
    submitEl: '.save',
    onRender: function() {
         var countries = ["Afghanistan", "Albania", "Algeria", "Andorra", "Angola", "Anguilla", "Antigua and Barbuda", "Argentina", "Armenia", "Aruba", "Australia", "Austria", "Azerbaijan", "Bahamas", "Bahrain", "Bangladesh", "Barbados", "Belarus", "Belgium", "Belize", "Benin", "Bermuda", "Bhutan", "Bolivia", "Bosnia and Herzegovina", "Botswana", "Brazil", "British Virgin Islands", "Brunei", "Bulgaria", "Burkina Faso", "Burundi", "Cambodia", "Cameroon", "Canada", "Cape Verde", "Cayman Islands", "Chad", "Chile", "China", "Colombia", "Congo", "Cook Islands", "Costa Rica", "Cote D Ivoire", "Croatia", "Cruise Ship", "Cuba", "Cyprus", "Czech Republic", "Denmark", "Djibouti", "Dominica", "Dominican Republic", "Ecuador", "Egypt", "El Salvador", "Equatorial Guinea", "Estonia", "Ethiopia", "Falkland Islands", "Faroe Islands", "Fiji", "Finland", "France", "French Polynesia", "French West Indies", "Gabon", "Gambia", "Georgia", "Germany", "Ghana", "Gibraltar", "Greece", "Greenland", "Grenada", "Guam", "Guatemala", "Guernsey", "Guinea", "Guinea Bissau", "Guyana", "Haiti", "Honduras", "Hong Kong", "Hungary", "Iceland", "India", "Indonesia", "Iran", "Iraq", "Ireland", "Isle of Man", "Israel", "Italy", "Jamaica", "Japan", "Jersey", "Jordan", "Kazakhstan", "Kenya", "Kuwait", "Kyrgyz Republic", "Laos", "Latvia", "Lebanon", "Lesotho", "Liberia", "Libya", "Liechtenstein", "Lithuania", "Luxembourg", "Macau", "Macedonia", "Madagascar", "Malawi", "Malaysia", "Maldives", "Mali", "Malta", "Mauritania", "Mauritius", "Mexico", "Moldova", "Monaco", "Mongolia", "Montenegro", "Montserrat", "Morocco", "Mozambique", "Namibia", "Nepal", "Netherlands", "Netherlands Antilles", "New Caledonia", "New Zealand", "Nicaragua", "Niger", "Nigeria", "Norway", "Oman", "Pakistan", "Palestine", "Panama", "Papua New Guinea", "Paraguay", "Peru", "Philippines", "Poland", "Portugal", "Puerto Rico", "Qatar", "Reunion", "Romania", "Russia", "Rwanda", "Saint Pierre and Miquelon", "Samoa", "San Marino", "Satellite", "Saudi Arabia", "Senegal", "Serbia", "Seychelles", "Sierra Leone", "Singapore", "Slovakia", "Slovenia", "South Africa", "South Korea", "Spain", "Sri Lanka", "St Kitts and Nevis", "St Lucia", "St Vincent", "St. Lucia", "Sudan", "Suriname", "Swaziland", "Sweden", "Switzerland", "Syria", "Taiwan", "Tajikistan", "Tanzania", "Thailand", "Timor L'Este", "Togo", "Tonga", "Trinidad and Tobago", "Tunisia", "Turkey", "Turkmenistan", "Turks and Caicos", "Uganda", "Ukraine", "United Arab Emirates", "United Kingdom(UK)", "United States(USA)", "Uruguay", "Uzbekistan", "Venezuela", "Vietnam", "Virgin Islands (US)", "Yemen", "Zambia", "Zimbabwe"];
        $('.select-country', this.el).select2({
            data: countries,
            allowClear: false
        });
        this.dateRender();
    },
    dateRender: function() {
        var self = this;    
        
        var fromDate = $('#from-date', this.el).datepicker().on('changeDate', function(ev) {
            if (ev.date.valueOf() > toDate.date.valueOf()) {
                var newDate = new Date(ev.date);
                newDate.setDate(newDate.getDate() + 1);
                toDate.setValue(newDate);
            }
            fromDate.hide();
            $('#to-date', self.el)[0].focus();
        }).data('datepicker');

        var toDate = $('#to-date',this.el).datepicker({
            onRender: function(date){
                return date.valueOf() <= fromDate.date.valueOf() ?'disabled' : '';
            }
        }).on('changeDate',  function(ev) {
            toDate.hide()
        }).data('datepicker');

    },
    beforeCancel: function() {
        var r = confirm("You have unsaved changes. Do you want to discard them?");
        return r;
    },

    beforeSubmit: function() {
        var form = $('#add-education-form');

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
                degree: {
                    required: true,
                    maxlength: 45
                },
                school: {
                    required: true,
                    maxlength: 45
                },
                country: {
                    required: true,
                    maxlength: 45
                },
                country: {
                    required: true,
                    maxlength: 45
                },
                field: {
                    required: true,
                    maxlength: 45
                }
            }
        });
        return (form.valid());
    },

    submit: function() {
        var fromDate_org = new Date($('input[name="fromDate"]', this.el).val());
        var fromDate = fromDate_org.getTime();

        var toDate = new Date($('input[name="toDate"]', this.el).val());
        toDate = toDate.getTime();

        var school = $('input[name="school"]', this.el).val();
        var field = $('input[name="field"]', this.el).val();
        var city = $('input[name="city"]', this.el).val();
        var country = $('.select-country', this.el).val();
        var degree = $('select[name="degree"]', this.el).val();

        var edu = new directory.Education();
        edu.save({
            school: school,
            field: field,
            city: city,
            country: country,
            degree: degree,
            fromDate: fromDate,
            toDate: toDate
        }, {
            success: function(model, response) {
                // console.log(model);
                // console.log(response);
                Backbone.history.loadUrl();

                // $('#education-content').append(new directory.EducationView({
                //     model: model
                // }).render().el);
            }
        });
        // edu.set("fromDate", fromDate_org);

        // $('#education-content', this.el).append(new directory.EducationView({
        //     model: edu
        // }).render().el);

    },

});