package com.example.fyplatest1;

public class Workout {

    String category, title, description, act1, act2, act3, gif, step;

    public Workout() {}

    public Workout(String category, String title, String description, String act1, String act2, String act3, String gif, String step) {
        this.category = category;
        this.title = title;
        this.description = description;
        this.act1 = act1;
        this.act2 = act2;
        this.act3 = act3;
        this.gif = gif;
        this.step = step;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

    public String getGif() {
        return gif;
    }

    public void setGif(String gif) {
        this.gif = gif;
    }

    public String getAct1() {
        return act1;
    }

    public void setAct1(String act1) {
        this.act1 = act1;
    }

    public String getAct2() {
        return act2;
    }

    public void setAct2(String act2) {
        this.act2 = act2;
    }

    public String getAct3() {
        return act3;
    }

    public void setAct3(String act3) {
        this.act3 = act3;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
