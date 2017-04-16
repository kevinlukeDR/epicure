directory.ActSuccessView = Backbone.View.extend({
    initialize: function(access_token, where) {

        var url = 'api/candidate/verification?uuid=' + access_token + "&where=" + where;
        var self = this;
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
                    $.cookie('access_token', response.data.accesstoken);
                    $.cookie('user_type', 'user');
                } else {
                    window.location.hash = "";
                }
            }
        }).done(function() {
            if (where == 'redirect')
                self.redirect();
            else {
               self.existUser();
            }
        });
        // console.log(access_token);
        // console.log($.cookie());

    },

    render: function() {
        this.$el.html(this.template());
        // this.redirect();
    },

    existUser: function() {
        $('#forNewUser').addClass('hidden');
        window.setTimeout(function() {
            window.location.hash = '#user/profile';
            window.location.reload();
        }, 3000);
    },

    redirect: function() {
        window.setTimeout(function() {
            window.location.hash = '#activate/profile'
        }, 3000);
    }
});