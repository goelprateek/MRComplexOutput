package com.spnotes.eshadoop;

public class Address {

	private String addressLine1;
	private String city;
	private String country;
	
	
	public Address(String addressLine1, String city, String country) {
		super();
		this.addressLine1 = addressLine1;
		this.city = city;
		this.country = country;
	}
	public String getAddressLine1() {
		return addressLine1;
	}
	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}
	
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	@Override
	public String toString() {
		return "Address [addressLine1=" + addressLine1 + ", city=" + city
				+ ", country=" + country + "]";
	}
	
	
}
