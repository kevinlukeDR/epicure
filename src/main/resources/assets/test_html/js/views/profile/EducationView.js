directory.EducationContentView = Backbone.View.extend({
    initialize: function() {
        _.bindAll(this, 'renderItem');
    },
    renderItem: function(model) {
        var eduItem = new directory.EducationView({
            model: model
        });
        eduItem.delegateEvents();
        eduItem.render();
        $(this.el).append(eduItem.el);
    },
    render: function() {
        this.$el.empty();
        this.collection.each(this.renderItem);
    },


})

directory.EducationView = Backbone.View.extend({

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
        'click span[id="edit-edu"]': 'editEdu',
        'click span[id="remove-edu"]': 'deleteEdu'
    },

    editEdu: function() {

        var modalview = new directory.EducationModal({
            model: this.model
        });
        $('.app').html(modalview.render().el);

    },

    deleteEdu: function() {
        var self = this;
        var url = "/api/profile/education/" + this.model.id;
        var r = confirm("Are you sure you want delete this education experience?")
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
    },

    destroy: function() {
        this.undelegateEvents();
        this.$el.removeData().unbind();
        this.remove();
    }
});

directory.EducationModal = Backbone.Modal.extend({
    cancelEl: '.cancel',
    submitEl: '.save',
    initialize: function() {
        $('input[name="degree"]', this.el).val(this.model.attributes.degree);

    },
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
        var form = $('#edit-education-form', this.el);

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

                field: {
                    required: true,
                    maxlength: 45
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

        var school = $('input[name="school"]', this.el).val();
        var field = $('input[name="field"]', this.el).val();
        var city = $('input[name="city"]', this.el).val();
        var country = $('.select-country', this.el).val();
        var degree = $('select[name="degree"]', this.el).val();
        this.model.save({
            school: school,
            field: field,
            city: city,
            country: country,
            degree: degree,
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
    }


})