directory.FirstProfileView = Backbone.View.extend({
    render: function() {
        var countries = ["Afghanistan", "Albania", "Algeria", "Andorra", "Angola", "Anguilla", "Antigua and Barbuda", "Argentina", "Armenia", "Aruba", "Australia", "Austria", "Azerbaijan", "Bahamas", "Bahrain", "Bangladesh", "Barbados", "Belarus", "Belgium", "Belize", "Benin", "Bermuda", "Bhutan", "Bolivia", "Bosnia and Herzegovina", "Botswana", "Brazil", "British Virgin Islands", "Brunei", "Bulgaria", "Burkina Faso", "Burundi", "Cambodia", "Cameroon", "Canada", "Cape Verde", "Cayman Islands", "Chad", "Chile", "China", "Colombia", "Congo", "Cook Islands", "Costa Rica", "Cote D Ivoire", "Croatia", "Cruise Ship", "Cuba", "Cyprus", "Czech Republic", "Denmark", "Djibouti", "Dominica", "Dominican Republic", "Ecuador", "Egypt", "El Salvador", "Equatorial Guinea", "Estonia", "Ethiopia", "Falkland Islands", "Faroe Islands", "Fiji", "Finland", "France", "French Polynesia", "French West Indies", "Gabon", "Gambia", "Georgia", "Germany", "Ghana", "Gibraltar", "Greece", "Greenland", "Grenada", "Guam", "Guatemala", "Guernsey", "Guinea", "Guinea Bissau", "Guyana", "Haiti", "Honduras", "Hong Kong", "Hungary", "Iceland", "India", "Indonesia", "Iran", "Iraq", "Ireland", "Isle of Man", "Israel", "Italy", "Jamaica", "Japan", "Jersey", "Jordan", "Kazakhstan", "Kenya", "Kuwait", "Kyrgyz Republic", "Laos", "Latvia", "Lebanon", "Lesotho", "Liberia", "Libya", "Liechtenstein", "Lithuania", "Luxembourg", "Macau", "Macedonia", "Madagascar", "Malawi", "Malaysia", "Maldives", "Mali", "Malta", "Mauritania", "Mauritius", "Mexico", "Moldova", "Monaco", "Mongolia", "Montenegro", "Montserrat", "Morocco", "Mozambique", "Namibia", "Nepal", "Netherlands", "Netherlands Antilles", "New Caledonia", "New Zealand", "Nicaragua", "Niger", "Nigeria", "Norway", "Oman", "Pakistan", "Palestine", "Panama", "Papua New Guinea", "Paraguay", "Peru", "Philippines", "Poland", "Portugal", "Puerto Rico", "Qatar", "Reunion", "Romania", "Russia", "Rwanda", "Saint Pierre and Miquelon", "Samoa", "San Marino", "Satellite", "Saudi Arabia", "Senegal", "Serbia", "Seychelles", "Sierra Leone", "Singapore", "Slovakia", "Slovenia", "South Africa", "South Korea", "Spain", "Sri Lanka", "St Kitts and Nevis", "St Lucia", "St Vincent", "St. Lucia", "Sudan", "Suriname", "Swaziland", "Sweden", "Switzerland", "Syria", "Taiwan", "Tajikistan", "Tanzania", "Thailand", "Timor L'Este", "Togo", "Tonga", "Trinidad and Tobago", "Tunisia", "Turkey", "Turkmenistan", "Turks and Caicos", "Uganda", "Ukraine", "United Arab Emirates", "United Kingdom(UK)", "United States(USA)", "Uruguay", "Uzbekistan", "Venezuela", "Vietnam", "Virgin Islands (US)", "Yemen", "Zambia", "Zimbabwe"];
        this.$el.html(this.template(this.model.attributes));
        $('.select-nationality', this.el).select2({
            placeholder: "Nationality",
            data: countries,
            // allowClear:false,
        });
        $('.select-country', this.el).select2({
            placeholder: "Nationality",
            data: countries,
            allowClear: false,
        });
        // $('.date-input', this.el).datepicker();
        this.dateRender();
        return this;
    },
    events: {
        // "click #submitBtn": function(e) {
        //     e.preventDefault();
        //     this.submitProfile();
        // },
        'click #basic-button': 'saveBasic',
        'click #saveExp': 'saveExp',
        'click #saveEdu': 'saveEdu',
        'click #submitBtn': 'saveCt'
    },
    saveBasic: function(e) {
        e.preventDefault();
        var self = this;
        var basic = new directory.BasicProfile();
        basic.save({
            id: self.model.id,
            fname: $('input[name="fname"]').val(),
            lname: $('input[name="lname"]').val(),
            availableDate: $('input[name="availableDate"]').val(),
            birth: $('input[name="birth"]').val(),
            gender: $('select[name="gender"]').val(),
            nationality: $('select[name="nationality"]').val()
        }, {
            success: function(model, response) {
                console.log(model);
            }
        })
    },

    saveExp: function(e) {
        e.preventDefault();
        var fromDate = new Date($('#fromDate').val());
        fromDate = fromDate.getTime();

        var toDate = new Date($('#toDate').val());
        toDate = toDate.getTime();

        var role = $('input[name="role"]').val();
        var company = $('input[name="company"]').val();
        var responsibility = $('#responsibility').val();

        var experience = new directory.Experience();
        experience.save({
            role: role,
            company: company,
            responsibility: responsibility,
            fromDate: fromDate,
            toDate: toDate
        }, {
            success: function(model, response) {
                console.log(model)
            }
        });
    },
    saveEdu: function(e){
        var fromDate_org = new Date($('#from-date').val());
        var fromDate = fromDate_org.getTime();

        var toDate = new Date($('#to-date').val());
        toDate = toDate.getTime();
        console.log(fromDate);
        var school = $('input[name="school"]').val();
        var field = $('input[name="field"]').val();
        var city = $('input[name="city"]').val();
        var country = $('.select-country').val();
        var degree = $('select[name="degree"]').val();

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
                console.log(model);
            }
        });
    },
    saveCt: function(e){
        e.preventDefault();

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
                // Backbone.history.loadUrl();
                console.log(model);
                if (response.metadata.error) {
                    console.log(response.metadata.error);
                    $('#feedback').text(response.metadata.error.error_message).show();
                    
                }else{
                     window.location.hash = 'user/guide';
                    window.location.reload();
                }
            }
        });
    },

    dateRender: function() {
        var nowTemp = new Date();
        var now = new Date(nowTemp.getFullYear(), nowTemp.getMonth(), nowTemp.getDate(), 0, 0, 0, 0);
        $('input[name="availableDate"]', this.el).datepicker({
            onRender: function(date) {
                return date.valueOf() < now.valueOf() ? 'disabled' : '';
            }
        });
        var fromDate = $('#fromDate', this.el).datepicker().on('changeDate', function(ev) {
            if (ev.date.valueOf() > toDate.date.valueOf()) {
                var newDate = new Date(ev.date);
                newDate.setDate(newDate.getDate() + 1);
                toDate.setValue(newDate);
            }
            fromDate.hide();
            $('#toDate', self.el).focus();
        }).data('datepicker');

        var toDate = $('#toDate', this.el).datepicker({
            onRender: function(date) {
                return date.valueOf() <= fromDate.date.valueOf() ? 'disabled' : '';
            }
        }).on('changeDate', function(ev) {
            toDate.hide()
        }).data('datepicker');

        var from = $('#from-date', this.el).datepicker().on('changeDate', function(ev) {
            if (ev.date.valueOf() > to.date.valueOf()) {
                var newDate = new Date(ev.date);
                newDate.setDate(newDate.getDate() + 1);
                to.setValue(newDate);
            }
            from.hide();
            $('#to-date', self.el)[0].focus();
        }).data('datepicker');

        var to = $('#to-date', this.el).datepicker({
            onRender: function(date) {
                return date.valueOf() <= from.date.valueOf() ? 'disabled' : '';
            }
        }).on('changeDate', function(ev) {
            to.hide()
        }).data('datepicker');

        $('input[name="issueDate"]',this.el).datepicker();

    },

    submitProfile: function() {
        var url = "/api/profile/update/";
        console.log(this.model.id);
        var data = $('#update-profile-form').serializeObject();

        console.log(data);
        data.id = this.model.id;
        directory.arrangeJson(data);
        var d = JSON.stringify(data);

        console.log(d);

        $.ajax({
            url: url,
            type: 'PUT',
            contentType: "application/json",
            data: d,
            processData: false,
            statusCode: {
                401: function() {
                    alert('Please Login First!')
                }
            },
            success: function(data) {
                if (data == 'Successful') {
                    alert("Update Profile Successfully!");
                    window.location.hash = 'user/guide';
                    window.location.reload();

                } else {
                    console.log(data);
                    $('#feedback').text(data).show();
                    alert(data);
                }
            }
        });
    }

})