directory.Certification = Backbone.Model.extend({
	// urlRoot: '/api/profile/certification/',
	url:'/api/profile/certification/',

	parse: function(resp,xhr){
		return{
			id:resp.data.certification.id,
			issueDate:resp.data.certification.issueDate,
			type: resp.data.certification.type,
			issuingBody: resp.data.certification.issuingBody
		}
		
	}
});

directory.CertificationCollection = Backbone.Collection.extend({
	model: directory.Certification,

	// url: '/api/profile/certification',
	parse: function(response){
    	return response.data.certifications;
    }
})