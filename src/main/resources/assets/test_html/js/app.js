var $doc = $(document);
$(document).on("ready", function() {


	directory.loadTemplates(["HomeView", "FeaturedJobsView", "FeaturedJobsListView", "ShellView", "UserShellView",
			"ApplicationFormView", "FormSubmitView", "JobBoardView", "ApplyView", "ApplySuccessView",
			"JobListView", "LoginView", "QuickLoginView", "ForgetPwView", "RegisterView", "RegSuccessView", "ActSuccessView", "FirstProfileView",
			"UserGuideView", "ResetPwView", "ProfileView", "ProfileStrView", "PreferenceView", "ProfileDoneView", "VideoView",
			"EditBasicModal", "CertificationView", "CertificationModal", "ExperienceView", "ExperienceModal",
			"EducationView", "EducationModal", "ResumeView", "ResumeListView", "ResetPwUserView", "ChangeEmailView", "ChangeEmailSuccessView",
			"JobDetailView0407", "AppStatusView", "SavedJobsView", "SavedJobListView",
			"AppStatusListView", "RecruiterLoginView", "RecruiterSignUpView",
			"InConstructionView", "RecruiterShellView", "RecruiterSidebarView", "RecruiterDBView", "PricingView",
			"ManageJobView", "ManageJobList", "PostJobView", "JobTagView", "PojSuccessView", "EditJobView", "CandidateView",
			"CandidateListView", "CandidateDetailView", "AboutUsView", "TEFLView", "LifeView", "MeetTeacherView", "ContactView",
			"UnSubscribeView", "TakePictureModal", "TakePictureModalMain","Notfound404","FoundJobView",
			"RecordingModal", "RecordingModalMain"
		],

		function() {
			$.validator.addMethod("greaterThan", function(value, element, param) {
				var $element = $(element),
					$min;

				if (typeof(param) === "string") {
					$min = $(param);
				} else {
					$min = $("#" + $element.data("min"));
				}

				if (this.settings.onfocusout) {
					$min.off(".validate-greaterThan").on("blur.validate-greaterThan", function() {
						$element.valid();
					});
				}
				return parseInt(value) > parseInt($min.val());
			}, "Max must be greater than min");

			$.validator.addClassRules({
				greaterThan: {
					greaterThan: true
				}
			});7
			$doc.ajaxSend(function(event, xhr) {
				var authToken = $.cookie('access_token');
				// var authToken = 'fake12345611';
				if (authToken) {
					xhr.setRequestHeader("Authorization", "Bearer " + authToken);
				}
			});
			$doc.ajaxError(function(event, xhr) {
				/* Stuff to do when an AJAX call returns an error */
				if (xhr.status == 401) {
					$.cookie('access_token', "");
					$.cookie('user_type', "");

					directory.redirectToLogin();
				}else if (xhr.status == 404) {
					directory.tonotfound();
				}
			});
			directory.router = new directory.Router();
			directory.router.on("route", function() {
				$("html,body").scrollTop(0);
			});
			var auth = $.cookie('access_token');
			var userType = $.cookie('user_type');
			Backbone.history.start();
		});

});