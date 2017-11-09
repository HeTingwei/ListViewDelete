package com.example.worklistview;

public class Fruit {
    private String name;
    private int imageId;
    private boolean selected;

    public Fruit(String name, int imageId) {
        this.name = name;
        this.imageId = imageId;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getName() {
        return name;
    }

    public int getImageId() {
        return imageId;
    }
}