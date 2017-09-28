package com.libertymutual.goforcode.spark.app.controllers;

import static spark.Spark.notFound;

import java.util.HashMap;
import java.util.Map;

import org.mindrot.jbcrypt.BCrypt;

import com.libertymutual.goforcode.spark.app.models.Apartment;
import com.libertymutual.goforcode.spark.app.models.User;
import com.libertymutual.goforcode.spark.app.utilities.AutoCloseableDb;
import com.libertymutual.goforcode.spark.app.utilities.JsonHelper;

import spark.Request;
import spark.Response;
import spark.Route;

public class UserApiController {

	public static final Route details = (Request req, Response res) -> {

		try (AutoCloseableDb db = new AutoCloseableDb()) {
			int idAsString = Integer.parseInt(req.params("id"));
			User user = Apartment.findById(idAsString);
			if (user != null) {
				res.header("Content-type", "application/json");
				return user.toJson(true);
			}
			notFound("User not found"); // sets hhtp status code to 404
			return "";
		}
	};

	// respond to post
	public static final Route createUser = (Request req, Response res) -> {
		System.out.println("create user route ran:");
		User user = new User();
		String userJson = req.body();
		user.fromMap(JsonHelper.toMap(userJson));

		try (AutoCloseableDb db = new AutoCloseableDb()) {
			user.saveIt();
			System.out.println("user is: " +user); 
			req.session().attribute("currentUser", user); 
			res.status(201);
			return user.toJson(true);
		}

	};
}
