directory.ApplicationFormView = Backbone.View.extend({
    initialize: function(attr) {
        this.uuid = attr;
        var self = this;
        var url = 'api/email/emailcampaign?uuid='+this.uuid+'&version=&date=';
        // this.email = decodeURIComponent(attr);
         this.checkEmail();
        $.ajax({
            url: url,
            type: 'GET',
            processData: false,
            statusCode: {
                404: function() {
                    window.location.hash = "";
                }
            },
            success: function(response) {
                if (response.metadata.status == "ok") {
                    console.log("success");
                    self.email = decodeURIComponent(response.data.email);
                    $('#inputEmail', self.el).val(self.email);
                }
                else {
                    window.location.hash = "";
                }
            }
        })

    },
    render: function() {
        var countries = ["Afghanistan", "Albania", "Algeria", "Andorra", "Angola", "Anguilla", "Antigua and Barbuda", "Argentina", "Armenia", "Aruba", "Australia", "Austria", "Azerbaijan", "Bahamas", "Bahrain", "Bangladesh", "Barbados", "Belarus", "Belgium", "Belize", "Benin", "Bermuda", "Bhutan", "Bolivia", "Bosnia and Herzegovina", "Botswana", "Brazil", "British Virgin Islands", "Brunei", "Bulgaria", "Burkina Faso", "Burundi", "Cambodia", "Cameroon", "Canada", "Cape Verde", "Cayman Islands", "Chad", "Chile", "China", "Colombia", "Congo", "Cook Islands", "Costa Rica", "Cote D Ivoire", "Croatia", "Cruise Ship", "Cuba", "Cyprus", "Czech Republic", "Denmark", "Djibouti", "Dominica", "Dominican Republic", "Ecuador", "Egypt", "El Salvador", "Equatorial Guinea", "Estonia", "Ethiopia", "Falkland Islands", "Faroe Islands", "Fiji", "Finland", "France", "French Polynesia", "French West Indies", "Gabon", "Gambia", "Georgia", "Germany", "Ghana", "Gibraltar", "Greece", "Greenland", "Grenada", "Guam", "Guatemala", "Guernsey", "Guinea", "Guinea Bissau", "Guyana", "Haiti", "Honduras", "Hong Kong", "Hungary", "Iceland", "India", "Indonesia", "Iran", "Iraq", "Ireland", "Isle of Man", "Israel", "Italy", "Jamaica", "Japan", "Jersey", "Jordan", "Kazakhstan", "Kenya", "Kuwait", "Kyrgyz Republic", "Laos", "Latvia", "Lebanon", "Lesotho", "Liberia", "Libya", "Liechtenstein", "Lithuania", "Luxembourg", "Macau", "Macedonia", "Madagascar", "Malawi", "Malaysia", "Maldives", "Mali", "Malta", "Mauritania", "Mauritius", "Mexico", "Moldova", "Monaco", "Mongolia", "Montenegro", "Montserrat", "Morocco", "Mozambique", "Namibia", "Nepal", "Netherlands", "Netherlands Antilles", "New Caledonia", "New Zealand", "Nicaragua", "Niger", "Nigeria", "Norway", "Oman", "Pakistan", "Palestine", "Panama", "Papua New Guinea", "Paraguay", "Peru", "Philippines", "Poland", "Portugal", "Puerto Rico", "Qatar", "Reunion", "Romania", "Russia", "Rwanda", "Saint Pierre and Miquelon", "Samoa", "San Marino", "Satellite", "Saudi Arabia", "Senegal", "Serbia", "Seychelles", "Sierra Leone", "Singapore", "Slovakia", "Slovenia", "South Africa", "South Korea", "Spain", "Sri Lanka", "St Kitts and Nevis", "St Lucia", "St Vincent", "St. Lucia", "Sudan", "Suriname", "Swaziland", "Sweden", "Switzerland", "Syria", "Taiwan", "Tajikistan", "Tanzania", "Thailand", "Timor L'Este", "Togo", "Tonga", "Trinidad and Tobago", "Tunisia", "Turkey", "Turkmenistan", "Turks and Caicos", "Uganda", "Ukraine", "United Arab Emirates", "United Kingdom(UK)", "United States(USA)", "Uruguay", "Uzbekistan", "Venezuela", "Vietnam", "Virgin Islands (US)", "Yemen", "Zambia", "Zimbabwe"];
        this.$el.html(this.template());
        // console.log(this.email);
        // $('#inputEmail', this.el).val(this.email);
        this.loadSubject();
        this.loadLocation();
        // $('.date-input', this.el).datepicker();
        $('.select-nationality', this.el).select2({
            placeholder: "Nationality",
            data: countries,
        });

        this.dateRender();
        return this;
    },
    events: {
        "blur #inputEmail": "checkEmail",
        // 'click .next': 'showNext',
        'click #on-site': 'enableCheckbox',
        'click #save-basic': 'saveBasic',
        'click #save-teaching': 'saveTeaching',
        'click #save-preference': 'savePreference',
        'click #save-detailed': 'saveDetailed',
        'mouseover .checked-pill': "changeIcon",
        "mouseout .checked-pill": "changeBack",
        'click .location': "uncheckLocation",
        'click .unlocation': "checkLocation",
        'click .subjects': "uncheckSubject",
        'click .unsubjects': "checkSubject",
        'change #family-question': 'showQ'
    },
    checkEmail: function() {
        $('.email .glyphicon').remove();
        $('#email-error').remove();
        $('div.email').removeClass("has-success");
        $('div.email').removeClass("has-error");

        var email = $('#inputEmail').val();
        if (email) {
        $.ajax({
                url: "/api/candidateLogin/test/" + email
            })
            .done(function(data) {
                console.log(data);
                if (data.metadata.error) {
                    $('div.email').addClass("has-error");
                    $('div.email').append("<span class='glyphicon glyphicon-remove form-control-feedback' aria-hidden='true'></span>");
                    $('div.email').append('<span class="help-block" id="email-error">You have already signed up for our website. Please <a href="#login">Login</a> to complete your profile.</span>');
                    $("#save-basic").prop("disabled", true);
                    
                } else {
                    $('div.email').addClass("has-success");
                    $('div.email').append("<span class='glyphicon glyphicon-ok form-control-feedback' aria-hidden='true'></span>");
                    $("#save-basic").prop("disabled", false);
                }
            });
            }
    },
    formValid: function(){
        var form = $('#application-form');
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
                fname:{
                    required: true,
                    maxlength:30
                },
                lname:{
                    required: true,
                    maxlength: 30,
                },
                
                birth: {
                    required: true,
                    digits: true,
                    rangelength: [4, 4]
                }
                
            },

            messages:{
                fname:{
                    required: "First Name is required."
                },
                lname:{
                    required:"Last Name is required."
                },
                gender:{
                    required:"Please select."
                },
                birth:{
                    rangelength: "Please enter 4 digits year."
                },
            }
        });
        return form.valid();
    },
    enableCheckbox: function() {
        if ($('#on-site').is(':checked')) {
            $('#practicum').removeAttr('disabled');
        }
    },
    saveBasic: function() {
        var self = this;
        var data = $('#basic-information').serializeObject();
        data.graduateDate = new Date(data.graduateDate).getTime();
        data.availableDate = new Date(data.availableDate).getTime();

        var basic = new directory.StepOne();
        console.log(this.checkEmail());
        console.log(this.formValid());
        // if (this.formValid()) {
            basic.save(data, {
                success: function(model, resp) {
                    console.log(model);
                    console.log(resp);
                    self.uuid = resp.data.uuid;
                }
            });
        // }

    },
    showTeaching: function() {
        var next_fs = $('#teaching-background');
        var current_fs = $('#basic-information');
        var animating = true;
        var left, opacity, scale;
        $("#progressbar li").eq($("fieldset").index(next_fs)).addClass("active");
        next_fs.show();
        //hide the current fieldset with style
        current_fs.animate({
            opacity: 0
        }, {
            step: function(now, mx) {
                
                scale = 1 - (1 - now) * 0.2;
                
                left = (now * 50) + "%";
               
                opacity = 1 - now;
                current_fs.css({
                    'transform': 'scale(' + scale + ')'
                });
                next_fs.css({
                    'left': left,
                    'opacity': opacity
                });
            },
            duration: 800,
            complete: function() {
                current_fs.hide();
                animating = false;
            },
           
            easing: 'easeInOutBack'
        });
},
saveTeaching: function() {
    var data = $('#teaching-background').serializeObject();
    data.uuid = this.uuid;
    if ($('#practicum').is(':checked')) {
        data.practicum = true;
    } else {
        data.practicum = false;
    }
    console.log(data);
    var teaching = new directory.StepTwo();
    teaching.save(data, {
        success: function(model, resp) {
            console.log(model);
            console.log(resp);
        }
    })
},
savePreference: function() {
    var self = this;
    var subject = $('.checked-subject').map(function() {
            return $.trim($(this).text());
        })
        .get();

    var age_group = [];
    //get checked age group checkbox
    age_group = $('.age-group:checked').map(function() {
            return $.trim(this.id)
        })
        .get();

    var data = {
        'uuid': self.uuid,
        'ageGroup': age_group,
        'subject': subject
    };
    console.log(data);
    var preference = new directory.StepThree();
    preference.save(data, {
        success: function(model, resp) {
            console.log(model);
        }
    });

},
saveDetailed: function() {
    var self = this;
    var form = $('#application-form');
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
            salaryMin: {
                required: true,
                number: true,
            },
            SalaryMax: {
                required: true,
                number: true,
                greaterThan: salaryMin
            },

            visa: {
                required: true,
            }

        },

    });


    if (form.valid() === true) {
        var data = $('#detailed').serializeObject();
        var location = $('.checked-location').map(function() {
                return $.trim($(this).text());
            })
            .get();
        data.location = location;
        data.uuid = this.uuid;

        console.log(data);

        var detail = new directory.StepFour();
        detail.save(data, {
            success: function(model, resp) {
                console.log(model);
                console.log(resp);
            }
        });
        var url = 'formSubmit/' + this.uuid;
        Backbone.history.navigate(url, true);
    }
},


loadSubject: function() {
    //get checked array
    var subjectsArray = ['A-level Testing', 'SAT', 'Business English', 'Mathematics', 'Science', 'History', 'Literature', 'Art', 'Others'];
    //unchecked subject load
    for (var i = subjectsArray.length - 1; i >= 0; i--) {
        var text = subjectsArray[i];
        $('.unchecked-subject-list', this.el).append('<li class="preference-list-item unchecked-pill unsubjects">\
            <button class="btn-pill"><span class="glyphicon glyphicon-plus" aria-hidden="true">\
            </span><span class="pill-text">' + text + '</span></button></li>');
    }

},


loadLocation: function() {
    //main city in china for suggestion
    var cityArray = ['Beijing', 'Shanghai', 'Shenzhen', 'Tianjin', 'Chongqing', 'Guangzhou', 'Chengdu', 'Hangzhou', 'Nanjing', 'Wuhan', 'Changsha', 'Hongkong', 'Macao', 'Taiwan'];
    for (var i = cityArray.length - 1; i >= 0; i--) {
        var text = cityArray[i];
        $('.unchecked-location-list', this.el).prepend('<li class="preference-list-item unchecked-pill unlocation">\
            <button class="btn-pill"><span class="glyphicon glyphicon-plus" aria-hidden="true">\
            </span><span class="pill-text">' + text + '</span></button></li>');
    }
},
checkLocation: function(e) {
    e.preventDefault();
    var text = $(e.currentTarget).find('.pill-text').text();
    //remove current pill
    $(e.currentTarget).remove();
    //move current pill to unchecked list
    $('.checked-location-list').prepend('<li class="preference-list-item checked-pill location">\
                            <button class="btn-pill"><span class="glyphicon glyphicon-ok" aria-hidden="true">\
                            </span><span class="pill-text checked-location">' + text + '</span></button></li>');
},
uncheckLocation: function(e) {
    e.preventDefault();
    var text = $(e.currentTarget).find('.pill-text').text();
    //remove current pill
    $(e.currentTarget).remove();
    //move current pill to unchecked list
    $('.unchecked-location-list').append('<li class="preference-list-item unchecked-pill unlocation">\
            <button class="btn-pill"><span class="glyphicon glyphicon-plus" aria-hidden="true">\
            </span><span class="pill-text">' + text + '</span></button></li>');
},

checkSubject: function(e) {
    e.preventDefault();
    var text = $(e.currentTarget).find('.pill-text').text();
    //remove current pill
    $(e.currentTarget).remove();
    //move current pill to unchecked list
    $('.checked-subject-list').prepend('<li class="preference-list-item checked-pill subjects">\
                            <button class="btn-pill"><span class="glyphicon glyphicon-ok" aria-hidden="true">\
                            </span><span class="pill-text checked-subject">' + text + '</span></button></li>');
},

uncheckSubject: function(e) {
    e.preventDefault();
    var text = $(e.currentTarget).find('.pill-text').text();
    //remove current pill
    $(e.currentTarget).remove();
    //move current pill to unchecked list
    $('.unchecked-subject-list').append('<li class="preference-list-item unchecked-pill unsubjects">\
            <button class="btn-pill"><span class="glyphicon glyphicon-plus" aria-hidden="true">\
            </span><span class="pill-text">' + text + '</span></button></li>');
},
checkSubject: function(e) {
    e.preventDefault();
    var text = $(e.currentTarget).find('.pill-text').text();
    //remove current pill
    $(e.currentTarget).remove();
    //move current pill to unchecked list
    $('.checked-subject-list').prepend('<li class="preference-list-item checked-pill subjects">\
                            <button class="btn-pill"><span class="glyphicon glyphicon-ok" aria-hidden="true">\
                            </span><span class="pill-text checked-subject">' + text + '</span></button></li>');
},
changeIcon: function(e) {
    $(e.currentTarget).find('.glyphicon').addClass('glyphicon-remove').removeClass('glyphicon-ok');
},
changeBack: function(e) {
    $(e.currentTarget).find('.glyphicon').removeClass('glyphicon-remove').addClass('glyphicon-ok');
},
showQ: function() {
    if ($('#family-question').val() == 'true') {
        $('#spouse-question').removeClass('hidden');
    } else {
        $('#spouse-question').addClass('hidden');
    }
},
dateRender: function() {
    $('input[name="graduateDate"]', this.el).datepicker();
    var nowTemp = new Date();
    var now = new Date(nowTemp.getFullYear(), nowTemp.getMonth(), nowTemp.getDate(), 0, 0, 0, 0);
    $('input[name="availableDate"]', this.el).datepicker({
        onRender: function(date) {
            return date.valueOf() < now.valueOf() ? 'disabled' : '';
        }
    });

},
});
