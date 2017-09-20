package com.libertymutual.goforcode.spark.app.controllers;

import java.util.HashMap;
import java.util.Map;

import com.libertymutual.goforcode.spark.app.models.Apartment;
import com.libertymutual.goforcode.spark.app.models.User;
import com.libertymutual.goforcode.spark.app.utilities.AutoCloseableDb;
import com.libertymutual.goforcode.spark.app.utilities.MustacheRenderer;

import spark.Request;
import spark.Response;
import spark.Route;

public class ApartmentController {

	public static final Route details = (Request req, Response res) -> {
		int id = Integer.parseInt(req.params("id"));
		
		try (AutoCloseableDb db = new AutoCloseableDb()) {
		Apartment apartment = Apartment.findById(id);
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("apartment", apartment);
		return MustacheRenderer.getInstance().render("apartment/details.html", model);
		}
	};
	
	public static final Route newForm = (Request req, Response res) -> {
	//	Map<String, Object> model = new HashMap<String, Object>();
		//String returnPath = req.queryParams(req.pathInfo());
	//	model.put("returnPath", req.queryParams("returnPath"));
		return MustacheRenderer.getInstance().render("apartment/newForm.html", null); //null when no model needed
	};

	public static final Route create = (Request req, Response res) -> {
	
		try(AutoCloseableDb db = new AutoCloseableDb()){
			
			int rent = Integer.parseInt(req.queryParams("rent"));
			int bedrooms = Integer.parseInt(req.queryParams("numberOfBedrooms"));
			double bathrooms = Double.parseDouble(req.queryParams("numberOfBathrooms"));
			int squareFootage = Integer.parseInt(req.queryParams("squareFootage"));
			String address = req.queryParams("address");
			String city = req.queryParams("city");
			String state = req.queryParams("state");
			String zipCode = req.queryParams("zipCode");
			
			Apartment apartment = new Apartment(rent, bedrooms, bathrooms, squareFootage, address, city, state, zipCode);
			apartment.saveIt();
			req.session().attribute("apartment", apartment);
			res.redirect("");
			return "";			
		} catch (Exception e)		{
			res.redirect("/");
			req.session().attribute("error", "there is already an apartment with that name.");
			System.err.println(e.getClass().getName());
			return "";		
		}	
	};
}
