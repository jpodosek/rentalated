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
			try (AutoCloseableDb db = new AutoCloseableDb()) {
				
			
			User user = User.findFirst("email = ?", email);
			if (user != null && BCrypt.checkpw(password, user.getPassword())) {
				res.header("Content-type", "application/json");
				return user.toJson(true);
			} 
				notFound("User not found"); // sets hhtp status code to 404
				return "";
			}
		};
}
