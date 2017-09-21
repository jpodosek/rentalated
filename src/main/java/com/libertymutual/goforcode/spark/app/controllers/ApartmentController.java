package com.libertymutual.goforcode.spark.app.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.list.LazyList;

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
			//List<Apartment> apartments = Apartment.where("user_id = ?", currentUser.getId()); //same as below
			List<Apartment> activeApartments = currentUser.get(Apartment.class, "is_active = ?", true);
			List<Apartment> inactiveApartments = currentUser.get(Apartment.class, "is_active = ?", false);
			//List<Apartment> apartments = currentUser.getAll(Apartment.class);
			
			Map<String, Object> model = new HashMap<String, Object>();
			model.put("currentUser", currentUser);
			model.put("noUser", req.session().attribute("currentUser") == null);	
			model.put("activeApartments", activeApartments);
			model.put("inactiveApartments", inactiveApartments);

			return MustacheRenderer.getInstance().render("apartment/index.html", model);
		}
	};

	public static final Route details = (Request req, Response res) -> {
		int id = Integer.parseInt(req.params("id"));

		try (AutoCloseableDb db = new AutoCloseableDb()) {
			Apartment apartment = Apartment.findById(id);
			Map<String, Object> model = new HashMap<String, Object>();
			model.put("currentUser", req.session().attribute("currentUser"));
			model.put("noUser", req.session().attribute("currentUser") == null);	
			model.put("apartment", apartment);
			model.put("hasLiked", null);
			model.put("hasListed", null);
			return MustacheRenderer.getInstance().render("apartment/details.html", model);
		}
	};

	public static final Route newForm = (Request req, Response res) -> {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("currentUser", req.session().attribute("currentUser"));
		model.put("noUser", req.session().attribute("currentUser") == null);	
		return MustacheRenderer.getInstance().render("apartment/newForm.html", model); // null when no model needed
	};

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
			apartment.saveIt();
			
			
			req.session().attribute("apartment", apartment);
			res.redirect("");
			return "";
		} catch (Exception e) {
			res.redirect("/");
			req.session().attribute("error", "there is already an apartment with that name.");
			System.err.println(e.getClass().getName());
			return "";
		}
	};

	
}
