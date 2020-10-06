package com.idream.task.dto;

import com.google.gson.annotations.Expose;

public class User {
    @Expose
    private String first_name;
    @Expose
    private String last_name;
    @Expose
    private String company_name;
    @Expose
    private String address;
    @Expose
    private String city;
    @Expose
    private String county;
    @Expose
    private String state;
    @Expose
    private String zip;
    @Expose
    private String phone1;
    @Expose
    private String phone;
    @Expose
    private String email;

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public void setPhone1(String phone1) {
        this.phone1 = phone1;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }


}
