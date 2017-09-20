package com.libertymutual.goforcode.spark.app.controllers;

import static spark.Spark.notFound;

import java.util.HashMap;
import java.util.Map;

import org.mindrot.jbcrypt.BCrypt;

import com.libertymutual.goforcode.spark.app.models.Apartment;
import com.libertymutual.goforcode.spark.app.models.User;
import com.libertymutual.goforcode.spark.app.utilities.AutoClosableDb;
import com.libertymutual.goforcode.spark.app.utilities.JsonHelper;

import spark.Request;
import spark.Response;
import spark.Route;

public class UserApiController {

	public static final Route details = (Request req, Response res) -> {

		try (AutoClosableDb db = new AutoClosableDb()) {
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
	public static final Route create = (Request req, Response res) -> {
		User user = new User();
		String userJson = req.body();
		user.fromMap(JsonHelper.toMap(userJson));

		try (AutoClosableDb db = new AutoClosableDb()) {
			user.saveIt();
			res.status(201);
			return user.toJson(true);
		}

	};
}
