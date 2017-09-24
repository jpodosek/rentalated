package com.libertymutual.goforcode.spark.app.filters;

import static spark.Spark.halt;

import java.util.UUID;

import com.libertymutual.goforcode.spark.app.models.Apartment;
import com.libertymutual.goforcode.spark.app.models.User;
import com.libertymutual.goforcode.spark.app.utilities.AutoCloseableDb;

import spark.Filter;
import spark.Request;
import spark.Response;

public class SecurityFilters {

	public static Filter isAuthenticated = (Request req, Response res) -> {

		if (req.session().attribute("currentUser") == null) {
			res.redirect("/login?returnPath=" + req.pathInfo()); // if user clicks create new apartment without being
																	// logged in,
			halt(); // stops the request
		}
	};

	public static Filter isOwner = (Request req, Response res) -> {

		try (AutoCloseableDb db = new AutoCloseableDb()) {
			User currentUser = req.session().attribute("currentUser");
			Apartment apartment = Apartment.findById(Integer.parseInt(req.params("id")));
			//is not owner, redirect & halt
			if (apartment.get("user_id") != currentUser.getId()) {
				res.redirect("/login?returnPath=/apartments/" + apartment.get("id"));
				halt();
			}
		}
	};

	

	public static Filter isNewSession = (Request req, Response res) -> {
		if (req.session().isNew()) {
			UUID csfr_token = UUID.randomUUID();
			req.session().attribute("csrf", csfr_token);
		}
	};
	
	public static Filter CSRF_Check = (Request req, Response res) -> {
		if (req.requestMethod() == "POST" && !req.session().attribute("csrf").toString().equals(req.queryParams("csrf"))) {
				res.redirect("/");
				halt();
		}
	};

}

