directory.Education = Backbone.Model.extend({
	url: "/api/profile/education",

	parse: function(resp,xhr){
		return{
			id:resp.data.education.id,
			city:resp.data.education.city,
			fromDate: resp.data.education.fromDate,
			toDate: resp.data.education.toDate,
			degree: resp.data.education.degree,
			country: resp.data.education.country,
			school: resp.data.education.school,
			field: resp.data.education.field
		}
		
	}
});

directory.EducationCollection = Backbone.Collection.extend({
	model: directory.Education,

	parse: function(response){
    	return response.data.educations;
    }
})