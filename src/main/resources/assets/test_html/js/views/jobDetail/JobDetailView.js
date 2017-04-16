directory.JobDetailView0407 = Backbone.View.extend({
  initialize: function() {
    //  _.bindAll(this, 'stay');
    // $(window).scroll(this.stay);
  },
  render: function() {

    this.$el.html(this.template(this.model.attributes));
    if ($.cookie('access_token')) {
      this.renderApplyBtn();
    } else {
      this.renderLoginBtn();
    };
    this.model.similar.fetch({
      success: function(data) {
        if (data.length == 0) {
          $('.no-similar', this.el).removeClass('hidden');
        } else {
          _.each(data.models, function(job) {
            $('#similar-job', this.el).append(new directory.SimilarJobListView({
              model: job
            }).render().el);
          }, this);
        }
      }
    });
    return this;
  },

  events: {
    'click #showMore': 'showMore',
    'click #showLess': 'showLess',
    'click #tosave': "favIt",
    'click #saved': 'unfavIt',
    "click #applyButton": "renderApply",

  },
  renderShare: function(d,s,id){
      var js, fjs = d.getElementsByTagName(s)[0];
      if (d.getElementById(id)) return;
      js = d.createElement(s); js.id = id;
      js.src = "//connect.facebook.net/en_US/sdk.js#xfbml=1&version=v2.8";
      fjs.parentNode.insertBefore(js, fjs);
  },
  showMore: function(e) {
    e.preventDefault();
    $(e.currentTarget).html('<i class="fa fa-angle-double-up" aria-hidden="true"></i>Show Less').attr('id', 'showLess');;
    $(e.currentTarget).parent().find('.show-more').removeClass('hidden');
  },
  showLess: function(e) {
    e.preventDefault();
    $(e.currentTarget).html('<i class="fa fa-angle-double-down" aria-hidden="true"></i>Show More').attr('id', 'showMore');;
    $(e.currentTarget).parent().find('.show-more').addClass('hidden');
  },

  stay: function() {
    var $stickyEl = $('#stay-on-page');
    var $window_top = $(window).scrollTop();
    var elTop = $stickyEl.offset().top;
    if ($window_top > elTop) {
      $stickyEl.addClass('affix');
    } else {
      $stickyEl.removeClass('affix');
    }
    // $stickyEl.toggleClass('sticky', $window.scrollTop() > elTop);
  },

  renderApplyBtn: function() {
    $('#button-wrapper', this.el).html('<button type="button" class="btn btn-lg btn-pink" id="applyButton" style="width: 100%">Apply Now</button>');
  },

  renderLoginBtn: function() {
    $('#button-wrapper', this.el).html('');
    if (!directory.qloginView) {
      directory.qloginView = new directory.QuickLoginView();
      directory.qloginView.render();
    } else {
      directory.qloginView.delegateEvents();
    };
    $('#apply-content', this.el).html(directory.qloginView.el);
  },

  //save job
  favIt: function() {

    var job_id = $("input[name='job_id']").val();
    this.fav = new directory.SavedJobs();
    this.fav.url = '/api/job/favorite/' + job_id;
    this.fav.save(null, {
      success: function(model, response) {
        if (response.metadata.error) {
          alert(response.metadata.error.error_message);
        } else {
          $('.fa-heart').addClass('fav').attr('id', 'saved');
          setTimeout(function () {
            $('.popover').fadeOut('slow');
            }, 3000);
        }
      }
    });
  },
  unfavIt: function(e) {
    var job_id = $("input[name='job_id']").val();

    this.fav.destroy();
    $('.fa-heart').removeClass('fav').attr('id', 'tosave');;
  },
  renderApply: function() {
    if (!directory.applyView) {
      directory.applyView = new directory.ApplyView();
      directory.applyView.render();
    } else {
      directory.applyView.delegateEvents();
    }

    $('#apply-content', this.el).html(directory.applyView.el);
  },

  apply: function(event) {
    event.preventDefault();

    var url = '/api/applystatus/apply';
    console.log("apply now");

    var job_id = $("input[name='job_id']").val();
    console.log(job_id);
    var applyInf = {
      job_id: job_id
        // account_id: $.cookie('account_id')
    };
    console.log(applyInf);


    $.ajax({
      url: url,
      type: "PUT",
      dataType: "json",
      contentType: "application/json",
      data: JSON.stringify(applyInf),
      processData: false,
      statusCode: {
        401: function() {
          alert('Please Login First!')
        }
      },
      success: function(data) {
        console.log(data);
        if (data) {
          alert("Success!");
        } else {
          alert("Sorry you have already applied!")
        }
      }
    });
  }

});