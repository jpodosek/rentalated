package com.libertymutual.goforcode.spark.app;

import static spark.Spark.*;

import java.util.List;

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

import spark.Filter;


public class Application {

    public static void main(String[] args)  {    	
    	String encryptedPassword =  BCrypt.hashpw("t", BCrypt.gensalt());

    	try (AutoCloseableDb db = new AutoCloseableDb()) {
 			ApartmentsUsers.deleteAll();
	    	User.deleteAll();
	    	Apartment.deleteAll();
	    	User user = new User("jon@gmail.com", encryptedPassword, "Jon", "Podosek"); 
	    	user.saveIt();    	
	    	
	    	//apartment 1 -> user 1
	    	Apartment apartment = new Apartment(2000, 1, 0, 700, "123 Main St", "San Fransisco", "CA", "95215");
	    	apartment.setBoolean("is_active", true); 	
	    	user.add(apartment); //user creates a new apartment;
	    	apartment.saveIt();
	    	//apartment.add(user); //create like - count of # users associated with a particular apartment
	    	//---------Find like --------------
	    	Apartment apartmentLiked = Apartment.findById(apartment.getId());
	    	List<User> users = apartmentLiked.getAll(User.class);
	    	int numLikes = users.size();
	    	System.out.println("number of likes: " + numLikes);
	    	
	    	//------------------------
	    	//apartment 2 -> user 1
	    	apartment = new Apartment(1500,2, 3.5, 1800, "Hollywood Blvd", "Hollywood", "CA", "95205");
	    	apartment.setBoolean("is_active", false);
	    	user.add(apartment);
	    	apartment.saveIt();
	    	
	    	//-------User2 & apartment 3)
	    	User user2 = new User("test@gmail.com", encryptedPassword, "Mark", "Wahlberg"); 
	    	user2.saveIt();    	
	    	apartment = new Apartment(4000, 4, 3, 2300, "23322 Pike Pl", "Seattle", "WA", "98036"); //not active (false by default)
	    	apartment.setBoolean("is_active", true);
	    	user2.add(apartment);
	    	apartment.saveIt();
	    	//-------User2 & apartment 4)
	    	apartment = new Apartment(2101, 2, 3, 1500, "12345 Street S", "Poopland", "VA", "010234"); //not active (false by default)
	    	apartment.setBoolean("is_active", true);
	    	user2.add(apartment);
	    	apartment.saveIt();
    	}
    	
    	//before("/*", SecurityFilters.isNewSession);
    //before("/*", SecurityFilters.CSRF_Check);
    	
    	//Apartments
    	path("/apartments",  () -> {
    		
    		before("/new", SecurityFilters.isAuthenticated);
	    	before("/new", SecurityFilters.isAuthenticated);
	    	get("/new", ApartmentController.newForm); //serve up form to create apartment
	    	
	    	before("/mine", SecurityFilters.isAuthenticated);	
	    	get("/mine", ApartmentController.index);
	    	
	    	before("", SecurityFilters.isAuthenticated);
	    	post("", ApartmentController.create); //handle apartment create form post
	    	
	    	get("/:id", ApartmentController.details);	
	    	
	    	before("/:id/activations", SecurityFilters.isAuthenticated);
	    	before("/:id/activations", SecurityFilters.isOwner);
	    	post("/:id/activations", ApartmentController.activate);
	    	
	    	before("/:id/deactivations", SecurityFilters.isAuthenticated);
	    	before("/:id/deactivations", SecurityFilters.isOwner);
	    	post("/:id/deactivations", ApartmentController.deactivate);  
	    	
	    	before("/:id/likes", SecurityFilters.isAuthenticated);
	    	post("/:id/likes", ApartmentController.likes);
	    	
    	});
    	
    	//Home
    	get("/", HomeController.index); //
    	
    	//Session
    	get("/login", SessionController.newForm);
    	post("/login", SessionController.create);
    	get("/logout", SessionController.logout);
    	
    	//user
    	get("/users/new", UserController.newForm);
    	post("/users", UserController.create);
    	get("/users/:id", UserController.details);	
    	
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
    	
