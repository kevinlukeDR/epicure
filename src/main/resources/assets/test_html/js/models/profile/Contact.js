directory.Contact = Backbone.Model.extend({
	url:'/api/profile/contact/',

	

	parse: function(resp,xhr){
		return{
			personalWebsite: resp.data.contactInfo.personalWebsite,
			linkedin:resp.data.contactInfo.linkedin,
			facebook: resp.data.contactInfo.facebook,
			instagram: resp.data.contactInfo.instagram,
			wechat: resp.data.contactInfo.wechat,
			twitter: resp.data.contactInfo.twitter
			
		}
		
	}
});