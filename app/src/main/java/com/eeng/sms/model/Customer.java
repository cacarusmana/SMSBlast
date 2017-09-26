package com.eeng.sms.model;

import java.io.Serializable;

/**
 * Created by dsi01 on 04/03/2017.
 */

public class Customer implements Serializable {
    private static final long serialVersionUID = -7680866694662312444L;


    private long id;
    private String customerName;
    private String phoneNumber;
    private String gender = "L";
    private String title;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
