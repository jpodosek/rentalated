package com.libertymutual.goforcode.spark.app;

import static spark.Spark.*;

import java.util.HashMap;
import java.util.Map;

import org.javalite.activejdbc.Base;
import org.mindrot.jbcrypt.BCrypt;

import com.libertymutual.goforcode.spark.app.controllers.ApartmentApiController;
import com.libertymutual.goforcode.spark.app.controllers.ApartmentController;
import com.libertymutual.goforcode.spark.app.controllers.HomeController;
import com.libertymutual.goforcode.spark.app.controllers.SessionController;
import com.libertymutual.goforcode.spark.app.controllers.UserApiController;
import com.libertymutual.goforcode.spark.app.controllers.UserController;
import com.libertymutual.goforcode.spark.app.filters.SecurityFilters;
import com.libertymutual.goforcode.spark.app.models.Apartment;
import com.libertymutual.goforcode.spark.app.models.User;
import com.libertymutual.goforcode.spark.app.utilities.AutoCloseableDb;
import com.libertymutual.goforcode.spark.app.utilities.MustacheRenderer;

import spark.Request;
import spark.Response;

public class Application {

    public static void main(String[] args)  {    	
    	String encryptedPassword =  BCrypt.hashpw("password", BCrypt.gensalt());

    	try (AutoCloseableDb db = new AutoCloseableDb()) {
	    	User.deleteAll();
	    	new User("jon@gmail.com", encryptedPassword, "Jon", "Podosek").saveIt();
	    	
	    	Apartment.deleteAll();
	    	new Apartment(2000, 1, 0, 700, "123 Main St", "San Fransisco", "CA", "95215").saveIt();
	    	new Apartment(3000, 3, 2, 1500, "456 Fake St", "Seattle", "WA", "98036").saveIt();
	    	
    	}
    	
    	get("/", HomeController.index); //
    	
    	path("/apartments",  () -> {
	    	before("/new", SecurityFilters.isAuthenticated);
	    	get("/new", ApartmentController.newForm); //serve up form to create apartment
	    	
	    	get("/:id", ApartmentController.details);
	    	
	    	before("", SecurityFilters.isAuthenticated);
	    	post("", ApartmentController.create); //handle apartment create form post
    	});
    	
    	
    	get("/login", SessionController.newForm);
    	post("/login", SessionController.create);
    	get("/logout", SessionController.logout);
    	
    	get("/signup", UserController.newForm);
    	get("/users/:id", UserController.details);
    	post("/signup", UserController.create);
    	
    	path("/api",  () -> {
        	get("/users/:id", UserApiController.details);
        	post("/users", UserApiController.create);
    	});
    	
    	path("/api",  () -> {
	    	get("/apartments/:id", ApartmentApiController.details);
	    	post("/apartments",ApartmentApiController.create);
    	});
    	
    	
    	
    }
}
    	
    	//MustacheRenderer renderer = new MustacheRenderer("templates");
//    	
//    	
//        get("/hello", (req, res) -> "Hello World");
//        get("/:id", (req, res) -> "Your chosen id is " + req.params("id")); //placeholders begin with : similar to {id} 
//        get("/", (req, res) -> {
//        	
//        	Map<String, Object> model = new HashMap<String, Object>();
//        	model.put("name", "Sir");
//        	model.put("bodyPart", "back");
//        	model.put("queryLastName", req.queryParamsValues("lastName")); //URLpath ?[lastName] value
//        	return renderer.render("home/default.html", model);
//        });
//        
//        post("/handlePost", (req, res) -> {
//        	
//        	Map<String, Object> model = new HashMap<String, Object>();
//        	int numberOfOunces = Integer.parseInt(req.queryParams("ounces"));
//        	if (numberOfOunces > 16) {
//        		model.put("message", "Since you want to buy over 1 lb, your information has been sent to the DEA ");
//        	} else {
//        		model.put("message", "Niiice. Enjoy your " + req.queryParams("weed"));
//        	}
//        	return renderer.render("home/ounces.html", model);
        	
//        });

