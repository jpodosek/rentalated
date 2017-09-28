package com.libertymutual.goforcode.spark.app.controllers;

import com.libertymutual.goforcode.spark.app.models.Apartment;
import com.libertymutual.goforcode.spark.app.models.User;
import com.libertymutual.goforcode.spark.app.utilities.AutoCloseableDb;
import com.libertymutual.goforcode.spark.app.utilities.JsonHelper;
import com.libertymutual.goforcode.spark.app.utilities.MustacheRenderer;

import static spark.Spark.notFound;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.javalite.activejdbc.LazyList;

import spark.Request;
import spark.Response;
import spark.Route;

public class ApartmentApiController {

	public static final Route details = (Request req, Response res) -> {

		try (AutoCloseableDb db = new AutoCloseableDb()) {
			int idAsString = Integer.parseInt(req.params("id"));
			Apartment apartment = Apartment.findById(idAsString);
			if (apartment != null) {
				res.header("Content-type", "application/json");
				return apartment.toJson(true);
			}
			notFound("Apartment not found");
			return "";
		}
	};

	public static final Route create = (Request req, Response res) -> {
		Apartment apartment = new Apartment();
		String apartmentJson = req.body();
		apartment.fromMap(JsonHelper.toMap(apartmentJson));

		try (AutoCloseableDb db = new AutoCloseableDb()) {
			apartment.saveIt();
			res.status(201);
			return apartment.toJson(true);
		}
	};

	public static final Route index = (Request req, Response res) -> {
		try (AutoCloseableDb db = new AutoCloseableDb()) {

			LazyList<Apartment> activeApartments = Apartment.where("is_active = ?", true);
			res.header("Content-type", "application/json");
			return activeApartments.toJson(true);
		}
	};

	public static final Route myListings = (Request req, Response res) -> {

		try (AutoCloseableDb db = new AutoCloseableDb()) {
			User currentUser = req.session().attribute("currentUser");
			LazyList<Apartment> userApartments = currentUser.getAll(Apartment.class);
			res.header("Content-type", "application/json");
			return userApartments.toJson(true);

		}
	};

	// activate listing
	public static final Route activate = (Request req, Response res) -> {
		System.out.println("activate route ran.");
		try (AutoCloseableDb db = new AutoCloseableDb()) {
			int id = Integer.parseInt(req.params("id"));
			Apartment apartment = Apartment.findById(id);
			apartment.setBoolean("is_active", true);
			apartment.saveIt();
			res.redirect("/apartments/" + id);
			return "";
		}
	};

	// activate listing
	public static final Route deactivate = (Request req, Response res) -> {
		System.out.println("deactivate route ran.");
		return "";
	};

}
