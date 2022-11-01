package com.example.track.entity;


import java.util.UUID;

//java 类
public class Safety {
   private String temperature;//
   private String insert_time;//
   private int warning_flag;//
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getInsert_time() {
        return insert_time;
    }

    public void setInsert_time(String insert_time) {
        this.insert_time = insert_time;
    }

    public int getWarning_flag() {
        return warning_flag;
    }

    public void setWarning_flag(int warning_flag) {
        this.warning_flag = warning_flag;
    }

}
