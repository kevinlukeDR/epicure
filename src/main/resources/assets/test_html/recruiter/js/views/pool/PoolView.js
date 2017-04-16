define([
	'jquery',
	'underscore',
	'backbone',
	'util',
	'views/pool/PoolListView',
	'collections/pool/CandidateCollection',
	'jqueryDataTables'
], function($, _, Backbone, util, PoolListView, CandidateCollection, DataTable) {
	var PoolView = Backbone.View.extend({
		el: $("#content"),

		initialize: function() {
			var templateStr = window.util.loadTemplateSync("templates/Pool/PoolView.html");
			var self = this;
			this.template = _.template(templateStr);
			this.collection = new CandidateCollection();
			this.collection.fetch().done(function() {
				self.render();
			});

		},
		render: function() {
			console.log(this.collection);
			this.$el.html(this.template());
			_.each(this.collection.models, function(jobresult) {
				$('#candidate-list', this.el).append(new PoolListView({
					model: jobresult
				}).render().el);

			}, this);
			// this.data();
			// $('#candidate-table tfoot th', this.el).each(function() {
			// 	var title = $(this, this.el).text();
			// 	$(this).html('<input type="text" placeholder="Search ' + title + '" />');
			// });
			this.renderDataTable();

			return this;
		},
		renderDataTable: function() {
			this.table = $('#candidate-table', this.el).DataTable({
				"scrollX": true
			});
			
		},
		events: {
			'click tr': 'selectRow',
			'click #contact': 'sendInterview',
			'click #reject': 'sendReject',
		},
		sendInterview: function(e) {
			e.preventDefault();
			var $btn = $(e.currentTarget);
			$btn.text('sending').prop('disabled', true);
			var url = '/api/recruiter/qualify';
			$.ajax({
				url: url,
				type: 'POST',
				success: function(resp){
					if(resp.metadata.error){
						alert(resp.metadata.error.error_message)
					}else{
						alert("Success!")
					}
				}
			}).done(function(){
				$btn.text('done');
			})
		},
		sendReject: function(e) {
			e.preventDefault();
			var $btn = $(e.currentTarget);
			$btn.text('sending').prop('disabled', true);
			var url = '/api/recruiter/unqualify';
			$.ajax({
				url: url,
				type: 'POST',
				success: function(resp){
					if(resp.metadata.error){
						alert(resp.metadata.error.error_message)
					}else{
						alert("Success!")
					}
				}
			}).done(function(){
				$btn.text('done');
			})
		},
		selectRow: function(e) {
			$this = $(e.currentTarget);
			$this.toggleClass('selected');
		},
		// events: {
		// 	'keyup #min': 'filter',
		// 	'keyup #max': 'filter'
		// },
		data: function() {

			this.table = $('#candidate-table', this.el).DataTable({
				"scrollX": true
			});
		},
		filter: function() {
			this.table.draw();
		},
		destroy: function() {
			this.undelegateEvents();
			this.$el.removeData().unbind();
			this.remove();
		}
	});
	return PoolView;
});