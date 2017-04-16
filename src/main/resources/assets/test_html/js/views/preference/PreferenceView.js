directory.PreferenceView = Backbone.View.extend({
    initialize: function() {
        // console.log(this.model);
        this.listenTo(this.model, "change", this.render, this);
    },
    render: function() {
        this.$el.html(this.template());
        this.checkboxes();
        this.loadSubject();
        this.loadLocation();
        this.renderStrength();
        return this;
    },

    events: {
        'mouseover .checked-pill': "changeIcon",
        "mouseout .checked-pill": "changeBack",
        'click #add-location': 'addLocation',
        'click .location': "uncheckLocation",
        'click .unlocation': "checkLocation",
        'click .subjects': "uncheckSubject",
        'click .unsubjects': "checkSubject",
        'click #save-button': 'save'
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
    checkboxes:function(){
        var jobtype = this.model.attributes.job_type;
        var age = this.model.attributes.age_group;
        var school = this.model.attributes.school_type;
        this.setCheckbox(jobtype);
        this.setCheckbox(age);
        this.setCheckbox(school);
        if (this.model.attributes.contract_length =="") {
            var contract = "#25"
        }else{
            var contract = '#'+ this.model.attributes.contract_length;
        }
        
        $(contract,this.el).attr('checked','checked');

    },
    setCheckbox: function(array){
        for (var i = array.length - 1; i >= 0; i--) {
            var id ='#'+array[i];
            $(id,this.el).attr('checked','checked');
        };
    },

    loadSubject: function() {
        //get checked array
        var checked = this.model.attributes.subject;
        var subjectsArray = ['English','A-level Testing', 'SAT', 'Business English', 'Mathematics', 'Science', 'History', 'Literature', 'Art', 'Others'];
        var filtered = subjectsArray.filter(function(index) {
            return this.indexOf(index) < 0;
        }, checked);
        console.log(filtered);
        //checked subject load
        $('.checked-subject-list-list', this.el).empty();
        for (var i = checked.length - 1; i >= 0; i--) {
            var text = checked[i];
            $('.checked-subject-list', this.el).append('<li class="preference-list-item checked-pill subjects">\
                            <button class="btn-pill"><span class="glyphicon glyphicon-ok" aria-hidden="true"></span>\
                            <span class="pill-text checked-subject">' + text + '</span></button></li>');
        };
        //unchecked subject load
        $('.unchecked-subject-list',this.el).empty();
        for (var i = filtered.length - 1; i >= 0; i--) {

            var text = filtered[i];
            $('.unchecked-subject-list',this.el).append('<li class="preference-list-item unchecked-pill unsubjects">\
            <button class="btn-pill"><span class="glyphicon glyphicon-plus" aria-hidden="true">\
            </span><span class="pill-text">'+text +'</span></button></li>');
        }

    },

    loadLocation:function(){
        var checked = this.model.attributes.location;
        //main city in china for suggestion
        var cityArray = ['Beijing','Shanghai','Shenzhen','Tianjin','Chongqing','Guangzhou','Chengdu','Hangzhou','Nanjing','Wuhan','Changsha','Hongkong','Macao','Taiwan'];
        var filtered = cityArray.filter(function(index) {
            return this.indexOf(index) < 0;
        }, checked);
        // console.log(filtered);
        //checked location load
         $('.checked-location-list-list', this.el).empty();
        for (var i = checked.length - 1; i >= 0; i--) {
            var text = checked[i];
            $('.checked-location-list',this.el).prepend('<li class="preference-list-item checked-pill location">\
                            <button class="btn-pill"><span class="glyphicon glyphicon-ok" aria-hidden="true">\
                            </span><span class="pill-text checked-location">' + text + '</span></button></li>');
        };
        // add location button 

        // $('.checked-location-list',this.el).append('<li class="add-textbox floatl">\
        //                     <button class="btn btn-add" id="add-location"><span class="glyphicon \
        //                     glyphicon-plus" aria-hidden="true"></span>Add Location</button></li>');


        //unchecked location load
        $('.unchecked-location-list',this.el).empty();
        for (var i = filtered.length - 1; i >= 0; i--) {
            var text = filtered[i];
            $('.unchecked-location-list',this.el).prepend('<li class="preference-list-item unchecked-pill unlocation">\
            <button class="btn-pill"><span class="glyphicon glyphicon-plus" aria-hidden="true">\
            </span><span class="pill-text">' + text + '</span></button></li>');
        }

    },

    changeIcon: function(e) {
        $(e.currentTarget).find('.glyphicon').addClass('glyphicon-remove').removeClass('glyphicon-ok');
    },
    changeBack: function(e) {
        $(e.currentTarget).find('.glyphicon').removeClass('glyphicon-remove').addClass('glyphicon-ok');
    },

    addLocation: function(e) {
        e.preventDefault();
        var addButton = e.currentTarget;
        var $li = $(addButton).parent();
        $(addButton).remove();

        $li.removeClass('floatl').html('<input type="text" id="add-location-input" placeholder="Add Location" class="form-control typeahead preference-input">');

        var substringMatcher = function(strs) {
            return function findMatches(q, cb) {
                var matches, substringRegex;

                // an array that will be populated with substring matches
                matches = [];

                // regex used to determine if a string contains the substring `q`
                substrRegex = new RegExp(q, 'i');

                // iterate through the pool of strings and for any string that
                // contains the substring `q`, add it to the `matches` array
                $.each(strs, function(i, str) {
                    if (substrRegex.test(str)) {
                        matches.push(str);
                    }
                });

                cb(matches);
            };
        };

        var cities = ['Shanghai', 'Beijing', "Shenzhen", "Chengdu", "Chongqing", "Wuhang", "Henan"];
        console.log($('#add-location-input .typeahead', this.el));
        $('#add-location-input .typeahead', this.el).typeahead({
            hint: true,
            highlight: true,
            minLength: 1
        }, {
            name: 'cities',
            source: substringMatcher(cities)
        })

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

    save: function(e) {
        e.preventDefault();
        //get checked location pills
        var location = $('.checked-location').map(function() {
                return $.trim($(this).text());
            })
            .get();
        //get checked job type checkbox
        var job_type = $('.job-type:checked').map(function() {
                return $.trim(this.id)
            })
            .get();

        //get checked subject pills
        var subject = $('.checked-subject').map(function() {
                return $.trim($(this).text());
            })
            .get();

        var age_group = [],
            school_type = [];
        //get checked age group checkbox
        age_group = $('.age-group:checked').map(function() {
                return $.trim(this.id)
            })
            .get();
        //get checked school type checkbox
        school_type = $('.school-type:checked').map(function() {
                return $.trim(this.id)
            })
            .get();
        //get contract length 
        var contract_length = "";
        contract_length = $('.contract-length:checked').val();

        // var salaryMin  = $('#salaryMin').val();
        // var salaryMax = $('#salaryMax').val();
        var formValues = {
            'location': location,
            'job_type': job_type,
            'subject': subject,
            "age_group": age_group,
            "school_type": school_type,
            "contract_length": contract_length,
            // "salaryMin": number(salaryMin),
            // "salaryMax": number(salaryMax)
        };

        // this.model.set();
        this.model.save(formValues, {
            success: function(model, response) {
                console.log(model);
                console.log(response);
                if (response.metadata.error) {
                    alert('error');
                } else {
                   $('.alert-success').show("slow").delay(4000).fadeOut();
                }
            }

        })
    }



});