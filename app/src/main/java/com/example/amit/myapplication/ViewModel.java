package com.example.amit.myapplication;

import android.os.Bundle;

public class ViewModel  {

    private String name;
    private float score;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }
}
