directory.ShellView = Backbone.View.extend({

    render: function() {
        this.$el.html(this.template());
        return this;
    },
    events: {
        'click #searchnow': 'search',
        'click #submit-feedback-side':'feedback'
    },
    feedback: function(e){
        e.preventDefault();
        // var form = $('#feedback-side-form');
        var data = new FormData();
        var text = $('#feedback-select').val() + ':' + $('#textarea').val();
        data.append('reason', text);
        var url = '/api/email/feedback';

        $.ajax({
            url: url,
            method: "POST",
            contentType: false,
            processData: false,
            data: data,
            success: function(data) {
                console.log(data);
                $('#side-feedback-success').show('slow');
                window.setTimeout(function(){
                     $('#side-feedback-success').hide('slow');
                },5000);
            }
        });
    },

    // renderAvatar: function() {
    //     directory.user = new directory.User();
    //     directory.user.fetch({
    //         success: function(data) {
    //             directory.usershellView = new directory.UserShellView({
    //                 model: data
    //             });
    //             directory.usershellView.render();
    //             self.$navSecond.html(directory.usershellView.el);
    //         }
    //     });
    // },
    search: function(e) {
        e.preventDefault();
        var self = this;

        var position = $('#search').val();
        var query = "?position=" + position + '&city=&subject=&grade=';
        window.location.hash = "#search/" + encodeURIComponent(query);
    },

    removeLogin: function() {
        $('#remove-after').empty();
    },
    selectMenuItem: function(menuItem) {
        $('.navbar .navbar-nav li').removeClass('active');
        if (menuItem) {
            $('.' + menuItem).addClass('active');
        }
    },
    noSelectMenu: function() {
        $('.navbar .navbar-nav li').removeClass('active');
    },
    close: function() {
        this.undelegateEvents();
        this.$el.removeData().unbind();
        this.remove();
    }
});