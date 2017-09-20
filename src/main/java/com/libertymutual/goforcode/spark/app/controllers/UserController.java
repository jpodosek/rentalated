package com.libertymutual.goforcode.spark.app.controllers;

import java.util.HashMap;
import java.util.Map;

import org.mindrot.jbcrypt.BCrypt;

import com.libertymutual.goforcode.spark.app.models.Apartment;
import com.libertymutual.goforcode.spark.app.models.User;
import com.libertymutual.goforcode.spark.app.utilities.AutoCloseableDb;
import com.libertymutual.goforcode.spark.app.utilities.JsonHelper;
import com.libertymutual.goforcode.spark.app.utilities.MustacheRenderer;

import spark.Request;
import spark.Response;
import spark.Route;

public class UserController {
	//get
	public static final Route newForm = (Request req, Response res) -> {
		return MustacheRenderer.getInstance().render("users/signup.html", null);
	};
		
	//respond to post 
	public static final Route create = (Request req, Response res) -> {	
	
		String email = req.queryParams("email");
		String password = req.queryParams("password");
		String firstName = req.queryParams("firstName");
		String lastName = req.queryParams("lastName");
		User user = new User(email, password, firstName, lastName);
		
		try(AutoCloseableDb db = new AutoCloseableDb()){
			user.saveIt();
			req.session().attribute("currentUser", user);
			res.redirect("/");
			return "";			
		} catch (Exception e)		{
			res.redirect("/");
			req.session().attribute("error", "there is already a user with that name.");
			System.err.println(e.getClass().getName());
			return "";		
		}	
	};
	
	public static final Route details = (Request req, Response res) -> {
		int id = Integer.parseInt(req.params("id"));
		
		try (AutoCloseableDb db = new AutoCloseableDb()) {
		User user = User.findById(id);
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("user", user);
		return MustacheRenderer.getInstance().render("users/details.html", model);
		}
	};
}
