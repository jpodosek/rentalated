package com.libertymutual.goforcode.spark.app.controllers;

import java.util.HashMap;
import java.util.Map;
import org.mindrot.jbcrypt.BCrypt;

import com.libertymutual.goforcode.spark.app.models.User;
import com.libertymutual.goforcode.spark.app.utilities.AutoCloseableDb;
import com.libertymutual.goforcode.spark.app.utilities.MustacheRenderer;

import spark.Request;
import spark.Response;
import spark.Route;

public class SessionController {

	// show form via get
	public static final Route newForm = (Request req, Response res) -> {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("currentUser", req.session().attribute("currentUser"));
		model.put("noUser", req.session().attribute("currentUser") == null);	
		model.put("returnPath", req.queryParams("returnPath"));
		model.put("csrf_token", req.session().attribute("csfr_token"));
		return MustacheRenderer.getInstance().render("session/login.html", model);
	};

	// post - user logs in and creates a session
	public static final Route create = (Request req, Response res) -> {
	
		try (AutoCloseableDb db = new AutoCloseableDb()) {
		String email = req.queryParams("email");
		String password = req.queryParams("password");
	
			// see if user entered in form matches user w/ same email in database
			User user = User.findFirst("email = ?", email);
			if (user != null && BCrypt.checkpw(password, user.getPassword())) { // if user exists and password entered
																				// matches hashed password in database
				req.session().attribute("currentUser", user); // sets attritute to object
			}
		}
		res.redirect(req.queryParamOrDefault("returnPath", "/")); //if path doesnt exist, go to slash		
		return ""; // redirects should return empty body;
	};

	// creating a session for login process
	public static final Route logout = (Request req, Response res) -> {
		//User user = User.findFirst("email = ?", email);
		req.session().removeAttribute("currentUser");
		res.redirect("/");
		return "";
	};

}
