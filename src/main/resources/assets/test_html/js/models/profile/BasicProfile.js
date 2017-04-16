directory.BasicProfile = Backbone.Model.extend({
	// urlRoot: '/api/profile/certification/',
	url:'/api/profile/basic/',

	parse: function(resp,xhr){
		return{
			id:resp.data.personalInfo.id,
			fname: resp.data.personalInfo.fname,
			lname: resp.data.personalInfo.lname,
			birth: resp.data.personalInfo.birth,
			nationality: resp.data.personalInfo.nationality,
			gender: resp.data.personalInfo.gender
			
		}
		
	}
});