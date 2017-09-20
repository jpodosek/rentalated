package com.libertymutual.goforcode.spark.app.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.libertymutual.goforcode.spark.app.models.Apartment;
import com.libertymutual.goforcode.spark.app.models.User;
import com.libertymutual.goforcode.spark.app.utilities.AutoCloseableDb;
import com.libertymutual.goforcode.spark.app.utilities.MustacheRenderer;

import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.template.velocity.VelocityTemplateEngine;

public class HomeController {
	
	//since this is a variable assignment (route = lambda function), ends w/ ;
	public static final Route index = (Request req, Response res) -> {	 
		try (AutoCloseableDb db = new AutoCloseableDb()) {
		List<Apartment> apartments = Apartment.findAll();	
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("apartments", apartments);
		
		//recently added so users display on home page
		List<User> users = User.findAll();	
		model.put("users", users);
		
		
		model.put("currentUser", req.session().attribute("currentUser"));
		model.put("noUser", req.session().attribute("currentUser") == null);		
		//replace with something else - about 7 lines of code
		return MustacheRenderer.getInstance().render("home/index.html", model);
		
		//return new VelocityTemplateEngine().render(new ModelAndView(model, "templates/home/index2.html"));
		}
	        };

		

	
	

}
