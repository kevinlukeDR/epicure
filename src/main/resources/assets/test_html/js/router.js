directory.Router = Backbone.Router.extend({

    routes: {
        "": "home",
        //for email campaign 
        "applicationForm/:email": "applicationForm",
        "formSubmit/:uuid": "formSubmit",
        //for candidate side 
        "search/:query": "searchResult",
        "search/user/:query": 'userSearchResult',
        "jobs/:id": "jobDetails",
        "jobs/:id/applyReceived": "applySuccess",
        // "for-test": "forTest", // This row is for test
        "login": "login",
        "forgetPw/:userType": "forgetPw",
        "resetPw/:type/:uuid": 'resetPwEmail',
        "register": "register",
        "activate/user/:access_token/:where": "activateSuccess",
        "activate/profile": "firstprofile",
        "user/guide": "userGuide",
        // "user": "user",
        "recruiter": "recruiter",
        "user/profile": "profile",
        "user/video": "video",
        "user/preference": "preference",
        // "user/profile/edit": "profileEdit",
        "user/resume": "resume",
        "user/resume/:id": "resumeDetails",
        "user/myapplication": "myapplication",
        "user/saved": "savedJobs",
        "user/:userType/resetPw": "resetPw",
        "user/changeEmail": "userChangeEmail",
        "changeEmailSuccess/:email": "changeEmailSuccess",

        //recruiter side
        "recruiterLogin": "recruiterLogin",
        "recruiterSignUp": "recruiterSignUp",
        "recruiter/dashboard": "recruiterDashboard",
        "recruiter/postJob": "postJob",
        "recruiter/Job:action/:job_id": "editJob",
        "recruiter/postJob/tag/:job_id": "addTag",
        "recruiter/postJob/:job_id": "submitJobPosting",
        "recruiter/postjob/:job_id/candidate": "candidateList",
        "recruiter/jobManagement": "jobManagement",
        "recruiter/jobs/:job_id": "reviewJobs",
        "recruiter/pricing": "pricing",
        "recruiter/notThereYet": "recruiterNo",
        "logout/:userType": "logout",
        "unsubscribe": "unsubscribe",
        "about": "about",
        "TEFL": "TEFL",
        "life": "life",
        "meet": "meet",
        "contact": "contact",
        "notThereYet": "inConstruction",
        "foundJob/:uuid": "foundJob",
        "404": "notFound"

    },

    initialize: function() {
        var auth = $.cookie('access_token');
        var userType = $.cookie('user_type');
        if (!directory.shellView) {
            directory.shellView = new directory.ShellView();
            directory.shellView.render();
        } else {
            directory.shellView.delegateEvents();
        }

        $('body').html(directory.shellView.el);
        this.$nav = $('nav');
        this.$content = $("#content");
        this.$sidebar = $('#sidebar-content');
        this.$body = $('body');
        this.$navSecond = $('#second-nav');
        this.$footer = $('footer');
        var self = this;
        if (auth) {
            directory.shellView.removeLogin();

            if (userType == 'user') { //check user type 
                directory.user = new directory.User();
                directory.user.fetch({
                        success: function(data) {
                            directory.usershellView = new directory.UserShellView({
                                model: data
                            });
                            directory.usershellView.render();
                            self.$navSecond.html(directory.usershellView.el).removeClass('hidden');
                        }
                    });
                // .done(function() {
                //         if (typeof(Storage) !== "undefined") {
                //             window.localStorage.setItem("id", directory.user.id);
                //             window.localStorage.setItem("fname", directory.user.get('fname'));
                //              window.localStorage.setItem("fname", directory.user.get('lname'));
                //         } else {
                //            $.cookie('id', directory.user.id)
                //         }
                //     })
                    // window.location.hash = 'user'
            } else {
                directory.recruiter = new directory.RecruiterProfile();
                directory.recruiter.fetch({
                    success: function(data) {
                        directory.recruiterShellView = new directory.RecruiterShellView({
                            model: data
                        });
                        directory.recruiterShellView.render();
                        self.$content.remove();
                        self.$nav.html(directory.recruiterShellView.el);
                    }
                });
                directory.recruiterSidebarView = new directory.RecruiterSidebarView();
                directory.recruiterSidebarView.render();

                self.$sidebar.html(directory.recruiterSidebarView.el).removeClass('hidden');
                self.$footer.remove();
                self.$recruiter = $('#recruiter-content');
                // window.location.hash = 'recruiter/dashboard'
            }
        }

    },

    home: function() {
        if (!directory.homelView) {
            directory.homelView = new directory.HomeView();
            directory.homelView.render();

        } else {
            console.log('reusing home view');
            directory.homelView.delegateEvents();
            directory.homelView.renderFeatured();
        }
        this.$content.html(directory.homelView.el);
        directory.shellView.selectMenuItem('home-menu');
    },

    // For email campaign
    applicationForm: function(email) {
        var self = this;
        this.$nav.addClass('hidden');
        this.$footer.addClass('hidden');
        this.$navSecond.addClass('hidden');
        // this.$navSecond.addClass('hidden');

        if (!directory.formView) {
            directory.formView = new directory.ApplicationFormView(email);
            directory.formView.render();

        } else {
            directory.formView.delegateEvents();
        }
        this.$content.html(directory.formView.el);
    },

    formSubmit: function(uuid) {
        var self = this;
        this.$nav.removeClass('hidden');
        this.$footer.removeClass('hidden');
        this.$navSecond.addClass('hidden');
        if (!directory.formsubmitView) {
            directory.formsubmitView = new directory.FormSubmitView(uuid);
            directory.formsubmitView.render();

        } else {
            directory.formsubmitView.delegateEvents();
        }
        this.$content.html(directory.formsubmitView.el);

    },


    searchResult: function(query) {
        query = decodeURIComponent(query);
        var self = this;

        // var results = new directory.JobResultCollection();
        // results.url = url;
        // results.fetch();
        // console.log(results);
        directory.jobBoardView = new directory.JobBoardView(query);
        directory.jobBoardView.render();
        self.$content.empty();
        self.$content.html(directory.jobBoardView.el);
        //             collection: resultsCollection
        //         });
        // var json = results.getFirstPage({fetch: true});
        // console.log(json);
        // $.ajax({
        //     url: url,
        //     type: 'GET',
        //     contentType: "application/json",

        //     processData: false,
        //     success: function(data) {
        //         var results = JSON.stringify(data.data);
        //         var resultsCollection = new directory.JobResultCollection(JSON.parse(results));

        //         // resultsCollection.each(function(jobresult) {
        //         //     console.log(jobresult);
        //         // });

        //         // console.log(resultsCollection);
        //         directory.jobBoardView = new directory.JobBoardView({
        //             collection: resultsCollection
        //         });
        //         directory.jobBoardView.render();
        //         self.$content.empty();
        //         self.$content.html(directory.jobBoardView.el);
        //         if (data.error) { // If there is an error, show the error messages
        //             console.log("error")
        //         }
        //     }
        // });

        directory.shellView.selectMenuItem("job-menu")
    },

    userSearchResult: function(query) {
        query = decodeURIComponent(query);

        var url = '/api/job/' + query;
        var self = this;

        $.ajax({
            url: url,
            type: 'GET',
            contentType: "application/json",
            processData: false,
            success: function(data) {
                var results = JSON.stringify(data.data);
                var resultsCollection = new directory.JobResultCollection(JSON.parse(results));

                resultsCollection.each(function(jobresult) {
                    console.log(jobresult);
                });

                console.log(resultsCollection);
                directory.jobBoardView = new directory.JobBoardView({
                    model: resultsCollection
                });
                directory.jobBoardView.render();
                self.$content.html(directory.jobBoardView.el);
                if (data.error) { // If there is an error, show the error messages
                    console.log("error")
                }
            }
        });

        directory.shellView.selectMenuItem("job-menu")
    },

    jobDetails: function(id) {
        var position = new directory.Position({
            id: id
        });
        var self = this;
        position.fetch({
            // url: '/api/job/getbyid/' + id,
            success: function(model, response) {
                // alert('success');
                console.log(response);
                self.$content.html(new directory.JobDetailView0407({
                    model: model
                }).render().el);
            }
        });
        // directory.shellView.selectMenuItem('job-menu');
    },

    applySuccess: function(id) {
        if (directory.applySuccessView) {
            directory.applySuccessView.destroy();
        }

        directory.applySuccessView = new directory.ApplySuccessView(id);
        directory.applySuccessView.render();
        this.$content.html(directory.applySuccessView.el);
        directory.shellView.noSelectMenu();
    },
    login: function() {
        if (!directory.loginView) {
            directory.loginView = new directory.LoginView();
            directory.loginView.render();
        } else {
            directory.loginView.delegateEvents();
        }
        this.$content.html(directory.loginView.el);
        directory.shellView.noSelectMenu();
    },

    forgetPw: function(userType) {
        if (directory.forgetpwView) {
            directory.forgetpwView.destroy();
        }

        directory.forgetpwView = new directory.ForgetPwView(userType);
        directory.forgetpwView.render();

        this.$content.html(directory.forgetpwView.el);
        directory.shellView.noSelectMenu();
    },

    resetPwEmail: function(type, uuid) {
        if (!directory.resetPwView) {
            directory.resetPwView = new directory.ResetPwView(type, uuid);
            directory.resetPwView.render();
        } else {
            directory.resetPwView.delegateEvents();
        }
        this.$content.html(directory.resetPwView.el);
        directory.shellView.noSelectMenu();
    },
    resetPw: function(userType) {
        var self = this;
        if (!directory.resetPwUserView) {
            directory.resetPwUserView = new directory.ResetPwUserView(userType);
            directory.resetPwUserView.render();
        } else {
            directory.resetPwUserView.delegateEvents();
        }
        if (userType == 'candidate') {
            this.$content.html(directory.resetPwUserView.el);
            directory.shellView.noSelectMenu();
        } else {
            self.$recruiter = $('#recruiter-content');
            self.$recruiter.html(directory.resetPwUserView.el);
        }
    },

    userChangeEmail: function() {
        directory.user = new directory.User();
        directory.user.fetch();

        if (!directory.changeEmailView) {
            directory.changeEmailView = new directory.ChangeEmailView();
            directory.changeEmailView.render();
        } else {
            directory.changeEmailView.delegateEvents();
        }
        this.$content.html(directory.changeEmailView.el);
        directory.shellView.noSelectMenu();
    },
    changeEmailSuccess: function(email) {
        if (!directory.changeView) {
            directory.changeView = new directory.ChangeEmailSuccessView(email);
            directory.changeView.render();
        } else {
            directory.changeView.delegateEvents();
        }
        this.$content.html(directory.changeView.el);
        directory.shellView.noSelectMenu();
    },

    register: function() {
        var signup = new directory.UserSignUp();
        if (directory.registerView) {
            directory.registerView.destroy();
        }

        directory.registerView = new directory.RegisterView({
            model: signup
        });
        directory.registerView.render();
        // this.$content.empty();
        this.$content.html(directory.registerView.el);
        this.shellView.noSelectMenu();
    },

    activateSuccess: function(access_token, where) {
        if (!directory.actSuccessView) {
            directory.actSuccessView = new directory.ActSuccessView(access_token, where);
            directory.actSuccessView.render();
        } else {
            directory.actSuccessView.delegateEvents();
        }
        this.$content.html(directory.actSuccessView.el);
    },

    firstprofile: function() {
        var self = this;
        this.$nav.addClass('hidden');
        this.$footer.addClass('hidden');
        this.$navSecond.addClass('hidden');
        directory.user = new directory.User();
        directory.user.fetch({
            success: function(data) {
                directory.firstprofileView = new directory.FirstProfileView({
                    model: data
                });
                directory.firstprofileView.render();
                self.$content.html(directory.firstprofileView.el);
            }
        });
    },

    userGuide: function() {
        var self = this;
        this.$nav.removeClass('hidden');
        this.$footer.removeClass('hidden');
        this.$navSecond.removeClass('hidden');
        directory.user.fetch({
            success: function(data) {
                directory.userguideView = new directory.UserGuideView({
                    model: data
                });
                self.$content.html(directory.userguideView.render().el);
            }
        });
    },

    profile: function() {
        var self = this;

        if (directory.profileDoneView) {
            directory.profileDoneView.destroy();
        }
        directory.user = new directory.User();
        directory.user.fetch({
            success: function(model, response) {
                // alert('success');
                console.log(model);
                directory.profileDoneView = new directory.ProfileDoneView({
                    model: model
                });

                directory.profileDoneView.render();

                // if (!directory.profileDoneView) {
                //     directory.profileDoneView = new directory.ProfileDoneView({
                //         model: model
                //     });

                //     directory.profileDoneView.render();
                // } else {
                //     directory.profileDoneView.delegateEvents();
                // }
                self.$content.empty();
                self.$content.append(directory.profileDoneView.el);
            }
        });
        directory.shellView.noSelectMenu();
    },


    // profileEdit: function() {

    //     var self = this;
    //     directory.user.fetch({
    //         success: function(model, response) {
    //             console.log(model);
    //             if (!directory.profileView) {
    //                 directory.profileView = new directory.ProfileView({
    //                     model: model
    //                 });

    //                 directory.profileView.render();
    //             } else {
    //                 directory.profileView.delegateEvents();
    //             }
    //             self.$content.html(directory.profileView.el);
    //         }
    //     });

    //     directory.shellView.noSelectMenu();
    // },

    preference: function() {
        directory.shellView.noSelectMenu();

        var self = this;
        var p = new directory.Preference();
        p.fetch({
            success: function(model, response) {
                directory.preferenceView = new directory.PreferenceView({
                    model: model
                });
                directory.preferenceView.render();
                self.$content.empty();
                self.$content.append(directory.preferenceView.el);
            }
        });
        // if (!directory.preferenceView) {
        //     directory.preferenceView = new directory.PreferenceView();
        //     directory.preferenceView.render();
        // } else {
        //     directory.preferenceView.delegateEvents();
        // }

        // self.$content.empty();
        // self.$content.append(directory.preferenceView.el);
    },

    video: function() {
        directory.shellView.noSelectMenu();
        var self = this;

        var video = new directory.Video();
        video.fetch({
            success: function(model, response) {
                console.log(model);
                directory.videoView = new directory.VideoView({
                    model: model
                });
                directory.videoView.render();
                self.$content.empty();
                self.$content.append(directory.videoView.el);
            }
        });
        // if (!directory.videoView) {
        //     directory.videoView = new directory.VideoView();
        //     directory.videoView.render();
        // }else{
        //     directory.videoView.delegateEvents();
        // }

        // self.$content.empty();
        // self.$content.append(directory.videoView.el);
    },

    resume: function() {
        directory.shellView.noSelectMenu();
        var self = this;
        // resumes = new directory.ResumeCollection();
        var resumes = new directory.ResumeLatest();
        resumes.fetch({
            success: function(data) {
                // console.log(data);
                directory.resumeView = new directory.ResumeView({
                    model: data
                });
                // directory.resumeView = new directory.ResumeView({
                //     collection: data
                // });
                directory.resumeView.render();
                self.$content.html(directory.resumeView.el);
            }
        })

    },

    resumeDetails: function(id) {
        directory.shellView.noSelectMenu();
        var self = this;
        resume = new directory.Resume({
            id: id
        });
        resume.fetch({
            success: function(data) {
                directory.resumeDetailView = new directory.ResumeDetailView({
                    model: data
                });
                directory.resumeDetailView.render();
                self.$content.html(directory.resumeDetailView.el);
            }
        })
    },

    myapplication: function() {
        var self = this;
        var appliedJobs = new directory.AppliedJobCollection();
        appliedJobs.fetch({
            success: function(model, response) {
                console.log(model);
                directory.appstatusView = new directory.AppStatusView({
                    model: model
                });
                directory.appstatusView.render();
                self.$content.html(directory.appstatusView.el);
            }
        });
        directory.shellView.noSelectMenu();
        directory.usershellView.selectMenuItem('application-menu');
    },

    savedJobs: function() {
        var self = this;
        var saved = new directory.SavedJobsCollection();
        saved.fetch({
            success: function(model, response) {

                if (response.metadata.status == "ok") {
                    savedView = new directory.SavedJobsView({
                        collection: model
                    });
                    savedView.render();
                    self.$content.html(savedView.el);
                }
            }
        });

        directory.shellView.noSelectMenu();
        // directory.usershellView.selectMenuItem('savedJobs-menu');
    },

    recruiterLogin: function() {
        if (!directory.recruiterLoginView) {
            directory.recruiterLoginView = new directory.RecruiterLoginView();
            directory.recruiterLoginView.render();
        } else {
            console.log('reusing recruiter signup view');
            directory.recruiterLoginView.delegateEvents(); // delegate events when the view is recycled
        }
        this.$content.html(directory.recruiterLoginView.el);

    },

    recruiterSignUp: function() {
        var recruitersignup = new directory.RecruiterSignUp();

        if (directory.recruiterSignUpView) {
            directory.recruiterSignUpView.destroy();
        }
        directory.recruiterSignUpView = new directory.RecruiterSignUpView({
            model: recruitersignup
        });
        directory.recruiterSignUpView.render();

        this.$content.html(directory.recruiterSignUpView.el);
    },
    recruiterDashboard: function() {
        this.$recruiter = $('#recruiter-content');
        var self = this;
        directory.recruiter = new directory.RecruiterProfile();
        directory.recruiter.fetch({
            success: function(data) {
                directory.recruiterDBView = new directory.RecruiterDBView({
                    model: data
                });
                directory.recruiterDBView.render();
                self.$recruiter.html(directory.recruiterDBView.el);
            }
        });

        directory.recruiterSidebarView.selectMenuItem('dashboard-menu');
    },

    postJob: function() {
        this.$recruiter = $('#recruiter-content');
        var self = this;
        directory.recruiter = new directory.RecruiterProfile();
        directory.recruiter.fetch({
            success: function(data) {
                directory.postJobView = new directory.PostJobView({
                    model: data
                });
                directory.postJobView.render();
                self.$recruiter.html(directory.postJobView.el);
            }
        });
        // if (directory.postJobView) {
        //     directory.postJobView.destroy();
        // }
        // directory.postJobView = new directory.PostJobView({
        //     model: directory.recruiter
        // });
        // directory.postJobView.render();

        // this.$recruiter.html(directory.postJobView.el);
        directory.recruiterSidebarView.noSelectMenu();
    },


    editJob: function(action, job_id) {
        this.$recruiter = $('#recruiter-content');
        var position = new directory.Position({
            id: job_id
        });
        var self = this;
        position.fetch({
            // url: '/api/job/getbyid/' + job_id,
            success: function(model, response) {
                // alert('success');
                console.log(model);
                self.$recruiter.html(new directory.EditJobView({
                    attributes: action,
                    model: model
                }).render().el);
            }
        });
        directory.recruiterSidebarView.noSelectMenu();
    },

    addTag: function(job_id) {
        var self = this;
        this.$recruiter = $('#recruiter-content');

        if (!directory.jobTagView) {
            directory.jobTagView = new directory.JobTagView(job_id);
            directory.jobTagView.render();
        } else {
            directory.jobTagView.delegateEvents();
        }
        this.$recruiter.html(directory.jobTagView.el);
    },

    submitJobPosting: function(job_id) {
        var self = this;
        this.$recruiter = $('#recruiter-content');
        var position = new directory.Position({
            id: job_id
        });
        var self = this;
        position.fetch({
            // url: '/api/job/getbyid/' + job_id,
            success: function(model, response) {
                // alert('success');
                console.log(model);
                self.$recruiter.html(new directory.PojSuccessView({
                    model: model
                }).render().el);
            }
        });
        directory.recruiterSidebarView.noSelectMenu();
    },
    jobManagement: function() {
        var self = this;
        // this.$recruiter = $('#recruiter-content');
        // if (!directory.recruiterShellView) {
        //     directory.recruiterShellView = new directory.RecruiterShellView({
        //         model: directory.recruiter
        //     });
        //     directory.recruiterShellView.render();
        // } else {
        //     directory.recruiterShellView.delegateEvents();
        // };
        // self.$body.html(directory.recruiterShellView.el);
        this.$recruiter = $('#recruiter-content');
        var postjobs = new directory.JobRecruiter();
        postjobs.fetch({
            url: '/api/job/getallbyid/',
            success: function(model, response) {
                // console.log(JSON.stringify(model));
                directory.manageJobView = new directory.ManageJobView({
                    model: model
                });
                self.$recruiter.html(directory.manageJobView.render().el);
            }
        });
        directory.recruiterSidebarView.selectMenuItem('managejob-menu');
    },
    reviewJobs: function(job_id) {
        this.$recruiter = $('#recruiter-content');
        var position = new directory.Position({
            id: job_id
        });
        var self = this;
        position.fetch({
            // url: '/api/job/getbyid/',
            success: function(model, response) {
                // alert('success');
                console.log(model);
                self.$recruiter.html(new directory.JobDetailView0407({
                    model: model
                }).render().el);
            }
        });
    },
    candidateList: function(job_id) {
        var self = this;
        // if (!directory.recruiterShellView) {
        //     directory.recruiterShellView = new directory.RecruiterShellView({
        //         model: directory.recruiter
        //     });
        //     directory.recruiterShellView.render();
        // } else {
        //     directory.recruiterShellView.delegateEvents();
        // };
        // this.$body.html(directory.recruiterShellView.el);
        // this.$recruiter = $('#content');
        this.$recruiter = $('#recruiter-content');
        var candidates = new directory.CandidateResultCollection();
        candidates.fetch({
            url: '/api/applystatus/getaccounts/' + job_id,
            success: function(model, response) {
                console.log(JSON.stringify(model));
                directory.candidateView = new directory.CandidateView({
                    model: model
                });
                self.$recruiter.html(directory.candidateView.render().el);
            }
        });
        directory.recruiterSidebarView.noSelectMenu();
        // directory.recruiterShellView.selectMenuItem('candidates-menu');
    },
    pricing: function() {
        this.$recruiter = $('#recruiter-content');
        if (!directory.priceView) {
            directory.priceView = new directory.PricingView();
            directory.priceView.render();
        }
        this.$recruiter.html(directory.priceView.el);
    },

    recruiterNo: function() {
        this.$recruiter = $('#recruiter-content');
        if (!directory.inConstructionView) {
            directory.inConstructionView = new directory.InConstructionView();
            directory.inConstructionView.render();
        }
        this.$recruiter.html(directory.inConstructionView.el);
    },


    logout: function(userType) {
        $.cookie('access_token', "");
        $.cookie('user_type', "");


        var self = this;
        this.$body.html(new directory.ShellView().render().el);
        if (userType == "user") {
            directory.user.destroy();
            self.$navSecond.empty();
            directory.usershellView.destroy();
            window.location.hash = 'login';
        } else {
            window.location.hash = 'recruiterLogin';
        }

        window.location.reload();

    },
    unsubscribe: function() {
        if (directory.unsubscribeView) {
            directory.unsubscribeView.destroy();
        }
        directory.unsubscribeView = new directory.UnSubscribeView();
        directory.unsubscribeView.render();
        this.$content.html(directory.unsubscribeView.el);
    },
    TEFL: function() {
        if (!directory.tefl) {
            directory.tefl = new directory.TEFLView();
            directory.tefl.render();
        }
        this.$content.html(directory.tefl.el);
        directory.shellView.selectMenuItem('tefl-menu');
    },
    life: function() {
        if (!directory.lifeView) {
            directory.lifeView = new directory.LifeView();
            directory.lifeView.render();
        }
        this.$content.html(directory.lifeView.el);
        directory.shellView.selectMenuItem('life-menu');
    },
    about: function() {
        if (!directory.aboutView) {
            directory.aboutView = new directory.AboutUsView();
            directory.aboutView.render();
        }
        this.$content.html(directory.aboutView.el);
        directory.shellView.selectMenuItem('about-menu');
    },
    meet: function() {
        if (!directory.meetView) {
            directory.meetView = new directory.MeetTeacherView();
            directory.meetView.render();
        }
        this.$content.html(directory.meetView.el);
        directory.shellView.selectMenuItem('meet-menu');
    },
    contact: function() {
        if (!directory.contactView) {
            directory.contactView = new directory.ContactView();
            directory.contactView.render();
        } else {
            directory.contactView.delegateEvents();
        }
        this.$content.html(directory.contactView.el);
        directory.shellView.noSelectMenu();
    },

    inConstruction: function() {
        if (!directory.inConstructionView) {
            directory.inConstructionView = new directory.InConstructionView();
            directory.inConstructionView.render();
        }
        this.$content.html(directory.inConstructionView.el);
    },
    foundJob: function(uuid) {
        if (!directory.foundView) {
            directory.foundView = new directory.FoundJobView(uuid);
            directory.foundView.render();
        }
        this.$content.html(directory.foundView.el);
    },
    notFound: function() {
        if (!directory.notfound) {
            directory.notfound = new directory.Notfound404();
            directory.notfound.render();
        } else {
            directory.notfound.delegateEvents();
        }

        this.$content.html(directory.notfound.el);
    }

});