package com.example.a012autospa;

public class BookingClass {
    public  String carModel;
    public String carReg;
    public String contactNum;
    public String washType;
    public String date;
    public String time;
    public double price;
    private String extras;

    public BookingClass() {
    }

    public BookingClass(String carModel, String carReg, String contactNum, String washType, String date, String time, double price, String extras) {
        this.carModel = carModel;
        this.carReg = carReg;
        this.contactNum = contactNum;
        this.washType = washType;
        this.date = date;
        this.time = time;
        this.price = price;
        this.extras = extras;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public String getCarReg() {
        return carReg;
    }

    public void setCarReg(String carReg) {
        this.carReg = carReg;
    }

    public String getContactNum() {
        return contactNum;
    }

    public void setContactNum(String contactNum) {
        this.contactNum = contactNum;
    }

    public String getWashType() {
        return washType;
    }

    public void setWashType(String washType) {
        this.washType = washType;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getExtras() {
        return extras;
    }

    public void setExtras(String extras) {
        this.extras = extras;
    }
}
