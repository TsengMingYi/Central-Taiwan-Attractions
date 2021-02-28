package com.example.googlemap;


public class RegionData {

    private int resId;
    private String Name;
    private String Area;

    public RegionData(int resId, String name, String area) {
        this.resId = resId;
        Name = name;
        Area = area;
    }

    public int getResId() {
        return resId;
    }

    public String getName() {
        return Name;
    }

    public String getArea() {
        return Area;
    }
}
