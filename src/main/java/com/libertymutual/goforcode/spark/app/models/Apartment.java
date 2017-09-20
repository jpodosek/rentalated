package com.libertymutual.goforcode.spark.app.models;


import org.javalite.activejdbc.Model;

public class Apartment extends Model {
	public User user;
	public Apartment() {}

	public Apartment(int rent, int number_of_bedrooms, double number_of_bathrooms, int square_footage, String address, String city, String state, String zip_code) {
		setRent(rent);
		setNumberOfBedrooms(number_of_bedrooms);
		setNumberOfBathrooms(number_of_bathrooms);
		setSquareFootage(square_footage);
		setAddress(address);
		setCity(city);
		setState(state);
		setZipCode(zip_code);
	}

	public int getRent() {
		return getInteger("rent");
	}

	public void setRent(int rent) {
		set("rent", rent);
	}

	public int getNumberOfBedrooms() {
		return getInteger("number_of_bedrooms");
	}

	public void setNumberOfBedrooms(int number_of_bedrooms) {
		set("number_of_bedrooms", number_of_bedrooms);
	}

	public double getNumberOfBathrooms() {
		return getDouble("number_of_bathrooms");
	}

	public void setNumberOfBathrooms(double number_of_bathrooms) {
		set("number_of_bathrooms", number_of_bathrooms);
	}

	public int getSquareFootage() {
		return getInteger("square_footage");
	}

	public void setSquareFootage(int square_footage) {
		set("square_footage", square_footage);
	}

	public String getAddress() {
		return getString("address");
	}

	public void setAddress(String address) {
		set("address", address);
	}

	public String getCity() {
		return getString("city");
	}

	public void setCity(String city) {
		set("city", city);
	}

	public String getState() {
		return getString("state");
	}

	public void setState(String state) {
		set("state", state);
	}

	public String getZipCode() {
		return getString("zip_code");
	}

	public void setZipCode(String zip_code) {
		set("zip_code", zip_code);
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}