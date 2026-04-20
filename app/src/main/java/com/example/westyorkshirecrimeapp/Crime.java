package com.example.westyorkshirecrimeapp;

import java.io.Serializable;
public class Crime implements java.io.Serializable {
    public String crimeID, month, reportedBy, fallsWithin, location,
            lsoaCode, lsoaName, crimeType, lastOutcome, context;
    public double longitude, latitude;

    // Required empty constructor for Firebase
    public Crime() {}

    public Crime(String id, String month, String rep, String falls, double lon, double lat,
                 String loc, String code, String name, String type, String outcome, String ctx) {
        this.crimeID = id;
        this.month = month;
        this.reportedBy = rep;
        this.fallsWithin = falls;
        this.longitude = lon;
        this.latitude = lat;
        this.location = loc;
        this.lsoaCode = code;
        this.lsoaName = name;
        this.crimeType = type;
        this.lastOutcome = outcome;
        this.context = ctx;
    }
}