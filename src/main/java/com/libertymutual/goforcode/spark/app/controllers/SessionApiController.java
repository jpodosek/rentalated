package com.libertymutual.goforcode.spark.app.controllers;

import static spark.Spark.notFound;

import org.mindrot.jbcrypt.BCrypt;

import com.libertymutual.goforcode.spark.app.models.User;
import com.libertymutual.goforcode.spark.app.utilities.AutoCloseableDb;
import com.libertymutual.goforcode.spark.app.utilities.JsonHelper;

import spark.Request;
import spark.Response;
import spark.Route;

public class SessionApiController {
	// respond to post
	public static final Route login = (Request req, Response res) -> {
		User jsonUser = new User();
		jsonUser.fromMap(JsonHelper.toMap(req.body()));

		String email = jsonUser.getEmail();
		String password = jsonUser.getPassword();
		res.header("Content-type", "application/json");
		try (AutoCloseableDb db = new AutoCloseableDb()) {
			User user = User.findFirst("email = ?", email);
			if (user != null && BCrypt.checkpw(password, user.getPassword())) {
				req.session().attribute("currentUser", user);
				res.status(201); //created a new session
				return user.toJson(true);
			}
			res.status(200); //serve could process request, server could not handle it
			return "{}";
		}
	};
	
	public static final Route destroy = (Request req, Response res)	-> {
		req.session().removeAttribute("currentUser");
		res.header("Content-Type", "application/json");
		res.status(200);
		return "{}";
	};
	
	
	
}
	
	
