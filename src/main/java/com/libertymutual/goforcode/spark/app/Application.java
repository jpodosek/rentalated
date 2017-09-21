package com.libertymutual.goforcode.spark.app;

import static spark.Spark.*;

import java.util.HashMap;
import java.util.List;
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
import com.libertymutual.goforcode.spark.app.models.ApartmentsUsers;
import com.libertymutual.goforcode.spark.app.models.User;
import com.libertymutual.goforcode.spark.app.utilities.AutoCloseableDb;
import com.libertymutual.goforcode.spark.app.utilities.MustacheRenderer;

import spark.Request;
import spark.Response;

public class Application {

    public static void main(String[] args)  {    	
    	String encryptedPassword =  BCrypt.hashpw("password", BCrypt.gensalt());

    	try (AutoCloseableDb db = new AutoCloseableDb()) {
    		//ApartmentsUsers like = ApartmentsUsers.createIt("like", 1);
    		//user 1
	    	User.deleteAll();
	    	User user = new User("jon@gmail.com", encryptedPassword, "Jon", "Podosek"); 
	    	user.saveIt();    	
	    	Apartment.deleteAll();
	    	//apartment 1 -> user 1
	    	Apartment apartment = new Apartment(2000, 1, 0, 700, "123 Main St", "San Fransisco", "CA", "95215");
	    	apartment.setBoolean("is_active", true); 	
	    	user.add(apartment); //user creates a new apartment;
	    	apartment.saveIt();
	    	apartment.add(user); //create like - count of # users associated with a particular apartment
	    	//---------Find like --------------
	    	Apartment apartmentLiked = Apartment.findById(apartment.getId());
	    	List<User> users = apartmentLiked.getAll(User.class);
	    	int numLikes = users.size();
	    	System.out.println("number of likes: " + numLikes);
	    	
	    	//------------------------
	    	//apartment 2 -> user 1
	    	Apartment apartment2 = new Apartment(1500,2, 3.5, 1800, "Hollywood Blvd", "Hollywood", "CA", "95205");
	    	apartment2.setBoolean("is_active", false);
	    	user.add(apartment2);
	    	apartment2.saveIt();
	    	apartment.add(user); //create like
	    	
	    	//-------User2 & apartment 3)
	    	user = new User("test@gmail.com", encryptedPassword, "Mark", "Wahlberg"); 
	    	user.saveIt();    	
	    	apartment2 = new Apartment(4000, 4, 3, 2300, "23322 Pike Pl", "Seattle", "WA", "98036"); //not active (false by default)
	    	apartment2.setBoolean("is_active", false);
	    	user.add(apartment2);
	    	apartment2.saveIt();
    	}
    	
    	//Home
    	get("/", HomeController.index); //
    	
    	//Apartments
    	path("/apartments",  () -> {
	    	before("/new", SecurityFilters.isAuthenticated);
	    	get("/new", ApartmentController.newForm); //serve up form to create apartment
	    	
	    	before("/mine", SecurityFilters.isAuthenticated);	
	    	get("/mine", ApartmentController.index);
	    	
	    	get("/:id", ApartmentController.details);
	    	
	    	before("", SecurityFilters.isAuthenticated);
	    	post("", ApartmentController.create); //handle apartment create form post
    	});
    	
    	//Session
    	get("/login", SessionController.newForm);
    	post("/login", SessionController.create);
    	get("/logout", SessionController.logout);
    	
    	//user
    	get("/signup", UserController.newForm);
    	get("/users/:id", UserController.details);
    	post("/signup", UserController.create);
    	
    	//Api Controllers ---------------------------
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

