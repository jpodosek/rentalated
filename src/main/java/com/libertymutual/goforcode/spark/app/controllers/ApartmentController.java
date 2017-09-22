package com.libertymutual.goforcode.spark.app.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.libertymutual.goforcode.spark.app.models.Apartment;
import com.libertymutual.goforcode.spark.app.models.User;
import com.libertymutual.goforcode.spark.app.utilities.AutoCloseableDb;
import com.libertymutual.goforcode.spark.app.utilities.MustacheRenderer;

import spark.Request;
import spark.Response;
import spark.Route;

public class ApartmentController {

	public static final Route index = (Request req, Response res) -> {

		try (AutoCloseableDb db = new AutoCloseableDb()) {
			User currentUser = req.session().attribute("currentUser");
			List<Apartment> activeApartments = currentUser.get(Apartment.class, "is_active = ?", true);
			List<Apartment> inactiveApartments = currentUser.get(Apartment.class, "is_active = ?", false);

			Map<String, Object> model = new HashMap<String, Object>();
			model.put("currentUser", currentUser);
			model.put("noUser", req.session().attribute("currentUser") == null);
			model.put("activeApartments", activeApartments);
			model.put("inactiveApartments", inactiveApartments);

			return MustacheRenderer.getInstance().render("apartment/index.html", model);
		}
	};

	public static final Route details = (Request req, Response res) -> {
		boolean displayIsActive = false;
		boolean displayisNotActive = false;
		boolean isLikeable = false;
		boolean hasListed = false;
		int numLikes;
		// boolean activeStatus;
		try (AutoCloseableDb db = new AutoCloseableDb()) {
			int apartmentId = Integer.parseInt(req.params("id"));
			Apartment apartment = Apartment.findById(apartmentId);
			User currentUser = req.session().attribute("currentUser"); // is logged in
			List<User> apartmentLikes = apartment.getAll(User.class); // list of all users associated with this
																		// particular apartment
			numLikes = apartmentLikes.size();
			// is apartment active logic
			if (apartment.getIsActive() == true) {
				displayIsActive = true;
			} else {
				displayisNotActive = true;
			}

			// determine if user has already liked apartment
			if (currentUser != null && !apartment.get("user_id").equals(currentUser.getId())) {
				isLikeable = true;
				for (User user : apartmentLikes) {
					if (user.getId() == currentUser.getId())
						isLikeable = false;
				}
			}

			// has user listed logic
			// check if current user id is in the apartments table
			if (currentUser != null && apartment.get("user_id").equals(currentUser.getId())) {

				hasListed = true;
			}
			// Apartment apartmentToCompare = Apartment.where("user_id = ?",

			Map<String, Object> model = new HashMap<String, Object>();
			model.put("currentUser", currentUser);
			model.put("noUser", req.session().attribute("currentUser") == null);
			model.put("id", apartmentId);
			model.put("apartmentLikes", apartmentLikes);
			System.out.println("apartmentLikes: " + apartmentLikes.toString());

			model.put("numLikes", numLikes);
			model.put("isActive", displayIsActive);
			model.put("isNotActive", displayisNotActive);
			model.put("apartment", apartment);
			model.put("isLikeable", isLikeable);
			model.put("hasListed", hasListed);
			model.put("hasNotListed", !hasListed); // hasListed == false
			return MustacheRenderer.getInstance().render("apartment/details.html", model);
		}
	};

	public static final Route newForm = (Request req, Response res) -> {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("currentUser", req.session().attribute("currentUser"));
		model.put("noUser", req.session().attribute("currentUser") == null);
		return MustacheRenderer.getInstance().render("apartment/newForm.html", model); // null when no model needed
	};

	// handle the post on create new apartment form
	public static final Route create = (Request req, Response res) -> {

		try (AutoCloseableDb db = new AutoCloseableDb()) {

			int rent = Integer.parseInt(req.queryParams("rent"));
			int bedrooms = Integer.parseInt(req.queryParams("number_of_bedrooms"));
			double bathrooms = Double.parseDouble(req.queryParams("number_of_bathrooms"));
			int squareFootage = Integer.parseInt(req.queryParams("square_footage"));
			String address = req.queryParams("address");
			String city = req.queryParams("city");
			String state = req.queryParams("state");
			String zipCode = req.queryParams("zip_code");

			Apartment apartment = new Apartment(rent, bedrooms, bathrooms, squareFootage, address, city, state,
					zipCode);
			apartment.setBoolean("is_active", true);
			User currentUser = req.session().attribute("currentUser");
			currentUser.add(apartment); // associate apartment with the logged in user as its lister.
			apartment.saveIt();

			// req.session().attribute("apartment", apartment); // DONT PUT APARTMENT IN
			// SESSION
			req.session().attribute("isLister", "isLister");
			res.redirect("");
			return "";
		}
	};

	// activate listing
	public static final Route activate = (Request req, Response res) -> {
		try (AutoCloseableDb db = new AutoCloseableDb()) {
			int id = Integer.parseInt(req.params("id"));
			Apartment apartment = Apartment.findById(id);
			apartment.setBoolean("is_active", true);
			apartment.saveIt();
			System.out.println("apartment is :" + apartment);
			res.redirect("/apartments/" + id);
			return "";
		}
	};

	// activate listing
	public static final Route deactivate = (Request req, Response res) -> {
		try (AutoCloseableDb db = new AutoCloseableDb()) {

			int id = Integer.parseInt(req.params("id"));
			Apartment apartment = Apartment.findById(id);
			System.out.println("apartment is :" + apartment);
			// apartment.setActive(false);
			apartment.set("is_active", false);
			apartment.saveIt();
			res.redirect("/apartments/" + id);
			return "";
		}
	};

	// activate listing
	public static final Route likes = (Request req, Response res) -> {
		try (AutoCloseableDb db = new AutoCloseableDb()) {

			int id = Integer.parseInt(req.params("id"));
			Apartment apartment = Apartment.findById(id);
			User currentUser = req.session().attribute("currentUser");
			apartment.add(currentUser);
			apartment.saveIt();
			res.redirect("/apartments/" + id);
			return "";
		}
	};

}
