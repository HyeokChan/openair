package com.example.myapplication;

public class ReviewInfo {
    public String nicname;
    public String rating;
    public String sentence;

    public Float ratingFloat = 0f;

    public ReviewInfo(String nicname, String sentence, String rating){
        this.nicname = nicname;
        this.sentence = sentence;
        this.rating = rating;
        this.ratingFloat = Float.parseFloat(this.rating);
    }
    public String getNicname() {
        return nicname;
    }

    public String getSentence() {
        return sentence;
    }

    public String getRating() {
        return rating;
    }
    public  float getRatingFloat(){
        return ratingFloat;
    }

}