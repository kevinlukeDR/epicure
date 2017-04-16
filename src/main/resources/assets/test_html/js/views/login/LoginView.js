directory.LoginView = Backbone.View.extend({
  // initialize: function(attrs) {
  //   console.log(attrs);
  // },
  className: 'container mt20',

  events: {
    'click #loginButton': 'login',
    "click#resend": function(e) {
      e.preventDefault();
      this.resend();
    }
  },

  render: function() {
    this.$el.html(this.template());
    this.$content = $('#content');
    return this;
  },

  login: function(e) {
    e.preventDefault();
    //disable button 
    var $btn = $(e.currentTarget);
    $btn.text('Loading...').prop("disabled", true);


    var self = this;
    $('.alert-error').addClass('hidden'); // Hide any errors on a new submit

    var url = '/api/candidateLogin';
    var formValues = {
      email: $('#inputEmail').val().toLowerCase(),
      password: $('#inputPassword').val()
    };


    $.ajax({
      url: url,
      type: 'POST',
      dataType: "json",
      contentType: "application/json",
      data: JSON.stringify(formValues),
      processData: false,
      statusCode: {
        403: function() {
          window.location.hash = 'denied';
        }
      },
      success: function(data) {
        // console.log(data);
        if (data.metadata.error) {
          // console.log(data.metadata.error);
          //enable button 
          $btn.text('Login').prop("disabled", false);
          if (data.metadata.error == 'please active your account first') {
            $('#feedback').html('Your accound has not been activated yet.<button class="btn btn-default" id="resend">RESEND</button> verification Email').removeClass('hidden');
            $('#resend').click(function(event) {
              event.preventDefault();
              var email = $('#inputEmail').val().toLowerCase();
              console.log(email);
              $.ajax({
                url: '/api/candidate/resend/?email=' + email,
                type: 'GET'
              });
              $('#feedback').append('<div class="alert alert-success" >Success!</div>')
            });

          } else {
            $('#feedback').text(data.metadata.error).removeClass('hidden');
          }
          // alert(data.metadata.error);
        } else {
          //Login successful 

          $btn.text('Login').prop("disabled",false);
          // console.log(data);
          if ($('#keepSignIn').is(':checked')) {
            $.cookie('access_token', data.metadata.session_token, {
              expires: 30
            });
            $.cookie('user_type', 'user', {
              expires: 30
            });

            // $.cookie('account_id', data.account.id,{expires:30});
          } else {
            $.cookie('access_token', data.metadata.session_token);
            $.cookie('user_type', 'user');
          }

          directory.user = new directory.User(data.data);
          window.location.hash = '';
          window.location.reload();
        }
      }
    });
  },
  resend: function() {
    var email = $('#inputEmail').val().toLowerCase();
    console.log(email);
    $.ajax({
      url: '/api/candidate/resend/?email=' + email,
      type: 'GET'
    }).done(function() {
      $('#feedback').append('<div class="alert alert-success hidden" >Success!</div>')
    });
  }
});