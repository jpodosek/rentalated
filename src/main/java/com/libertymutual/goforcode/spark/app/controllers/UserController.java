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
	// get
	public static final Route newForm = (Request req, Response res) -> {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("currentUser", req.session().attribute("currentUser"));
		model.put("noUser", req.session().attribute("currentUser") == null);
		model.put("csrf", req.session().attribute("csrf"));
		return MustacheRenderer.getInstance().render("users/signup.html", model);
	};

	// respond to post
	public static final Route create = (Request req, Response res) -> {
		//MAKE SURE TO EXCRYPT PASSWORDS
	 	
		try (AutoCloseableDb db = new AutoCloseableDb()) {
			String email = req.queryParams("email");
			String encryptedPassword =  BCrypt.hashpw(req.queryParams("password"), BCrypt.gensalt());
			//String password = req.queryParams("password");
			String firstName = req.queryParams("first_name");
			String lastName = req.queryParams("last_name");
			User user = new User(email, encryptedPassword, firstName, lastName);
			user.saveIt();
			req.session().attribute("currentUser", user);
			res.redirect("/");
			return "";
		} catch (Exception e) {
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
