package com.alsharqi.compliance.location;

import com.alsharqi.compliance.contact.Contact;

import java.util.HashSet;
import java.util.Set;

public class Location {

    private Long id;
    private String name;
    private String address1;
    private String address2;

    private String city;
    private String state;

    private String countryCode;
    private String zipCode;

    private boolean residential;

    private boolean loadingDock;
    private boolean appointment;
    private String notes;
    private double lat;

    private double lng;
    private String type;
    private String receivingHours;

    // Story: 96  -Ammar
    private boolean isHeadOffice;
    private String country;
    private Set<Contact> contacts = new HashSet<Contact>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public boolean isResidential() {
        return residential;
    }

    public void setResidential(boolean residential) {
        this.residential = residential;
    }

    public boolean isLoadingDock() {
        return loadingDock;
    }

    public void setLoadingDock(boolean loadingDock) {
        this.loadingDock = loadingDock;
    }

    public boolean isAppointment() {
        return appointment;
    }

    public void setAppointment(boolean appointment) {
        this.appointment = appointment;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReceivingHours() {
        return receivingHours;
    }

    public void setReceivingHours(String receivingHours) {
        this.receivingHours = receivingHours;
    }

    public boolean isHeadOffice() {
        return isHeadOffice;
    }

    public void setHeadOffice(boolean headOffice) {
        isHeadOffice = headOffice;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Set<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(Set<Contact> contacts) {
        this.contacts = contacts;
    }
}
