directory.EditBasicModal = Backbone.Modal.extend({
    template: "#edit-basic-modal",
    cancelEl: '.cancel',
    submitEl: '.save',
    onRender: function() {
        if (this.model.attributes.gender) {
           $('.gender', this.el).val(this.model.attributes.gender); 
       }else{
            $('.gender', this.el).val('unknown');
       };
        
        var countries = ["Afghanistan", "Albania", "Algeria", "Andorra", "Angola", "Anguilla", "Antigua and Barbuda", "Argentina", "Armenia", "Aruba", "Australia", "Austria", "Azerbaijan", "Bahamas", "Bahrain", "Bangladesh", "Barbados", "Belarus", "Belgium", "Belize", "Benin", "Bermuda", "Bhutan", "Bolivia", "Bosnia and Herzegovina", "Botswana", "Brazil", "British Virgin Islands", "Brunei", "Bulgaria", "Burkina Faso", "Burundi", "Cambodia", "Cameroon", "Canada", "Cape Verde", "Cayman Islands", "Chad", "Chile", "China", "Colombia", "Congo", "Cook Islands", "Costa Rica", "Cote D Ivoire", "Croatia", "Cruise Ship", "Cuba", "Cyprus", "Czech Republic", "Denmark", "Djibouti", "Dominica", "Dominican Republic", "Ecuador", "Egypt", "El Salvador", "Equatorial Guinea", "Estonia", "Ethiopia", "Falkland Islands", "Faroe Islands", "Fiji", "Finland", "France", "French Polynesia", "French West Indies", "Gabon", "Gambia", "Georgia", "Germany", "Ghana", "Gibraltar", "Greece", "Greenland", "Grenada", "Guam", "Guatemala", "Guernsey", "Guinea", "Guinea Bissau", "Guyana", "Haiti", "Honduras", "Hong Kong", "Hungary", "Iceland", "India", "Indonesia", "Iran", "Iraq", "Ireland", "Isle of Man", "Israel", "Italy", "Jamaica", "Japan", "Jersey", "Jordan", "Kazakhstan", "Kenya", "Kuwait", "Kyrgyz Republic", "Laos", "Latvia", "Lebanon", "Lesotho", "Liberia", "Libya", "Liechtenstein", "Lithuania", "Luxembourg", "Macau", "Macedonia", "Madagascar", "Malawi", "Malaysia", "Maldives", "Mali", "Malta", "Mauritania", "Mauritius", "Mexico", "Moldova", "Monaco", "Mongolia", "Montenegro", "Montserrat", "Morocco", "Mozambique", "Namibia", "Nepal", "Netherlands", "Netherlands Antilles", "New Caledonia", "New Zealand", "Nicaragua", "Niger", "Nigeria", "Norway", "Oman", "Pakistan", "Palestine", "Panama", "Papua New Guinea", "Paraguay", "Peru", "Philippines", "Poland", "Portugal", "Puerto Rico", "Qatar", "Reunion", "Romania", "Russia", "Rwanda", "Saint Pierre and Miquelon", "Samoa", "San Marino", "Satellite", "Saudi Arabia", "Senegal", "Serbia", "Seychelles", "Sierra Leone", "Singapore", "Slovakia", "Slovenia", "South Africa", "South Korea", "Spain", "Sri Lanka", "St Kitts and Nevis", "St Lucia", "St Vincent", "St. Lucia", "Sudan", "Suriname", "Swaziland", "Sweden", "Switzerland", "Syria", "Taiwan", "Tajikistan", "Tanzania", "Thailand", "Timor L'Este", "Togo", "Tonga", "Trinidad and Tobago", "Tunisia", "Turkey", "Turkmenistan", "Turks and Caicos", "Uganda", "Ukraine", "United Arab Emirates", "United Kingdom(UK)", "United States(USA)", "Uruguay", "Uzbekistan", "Venezuela", "Vietnam", "Virgin Islands (US)", "Yemen", "Zambia", "Zimbabwe"];
        $('.select-nationality', this.el).select2({
            data: countries,
            allowClear: false
        });
        this.dateRender();
        // $('input[name="availableDate"]', this.el).datepicker({
        //     onRender: function(date) {
        //         return date.valueOf() < now.valueOf() ? 'disabled' : '';
        //     }
        // });
    },
    dateRender: function() {
        var nowTemp = new Date();
        var now = new Date(nowTemp.getFullYear(), nowTemp.getMonth(), nowTemp.getDate(), 0, 0, 0, 0);
        $('input[name="availableDate"]', this.el).datepicker({
            onRender: function(date) {
                return date.valueOf() < now.valueOf() ? 'disabled' : '';
            }
        });
    },
    beforeCancel: function() {
        var r = confirm("You have unsaved changes. Do you want to discard them?");
        return r;
    },
    beforeSubmit: function() {
        var form = $('#edit-basic-form');

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
                    maxlength: 30,
                },
                lname: {
                    required: true,
                    maxlength: 30,
                },
                birth: {
                    digits: true,
                    rangelength: [4, 4]
                }
            },
            message: {
                birth: {
                    rangelength: "Please enter 4 digits year."
                },
            }
        });
        return (form.valid());
    },


    submit: function() {
        var self = this;
        var basic = new directory.BasicProfile({
            id: this.model.attributes.id
        });
        var formValues = {
            fname: $('input[name="fname"]').val(),
            lname: $('input[name="lname"]').val(),
            availableDate: $('input[name="availableDate"]').val(),
            birth: $('input[name="birth"]').val(),
            gender: $('select[name="gender"]').val(),
            nationality: $('select[name="nationality"]').val()
        };
        basic.save({
            fname: $('input[name="fname"]').val(),
            lname: $('input[name="lname"]').val(),
            availableDate: $('input[name="availableDate"]').val(),
            birth: $('input[name="birth"]').val(),
            gender: $('select[name="gender"]').val(),
            nationality: $('select[name="nationality"]').val()
        }, {
            success: function(model, response) {
                // console.log(model);
                self.model.set(formValues);
                // console.log(self.model);
            }
        })


    }
});