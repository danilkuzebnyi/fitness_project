package org.danylo.model;

import java.util.List;

public class Trainer extends User {
    private String description;
    private Integer experience;
    private Integer price;
    private String image;
    private Rating rating;
    private List<Specialization> specializations;
    private WorkingTime workingTime;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getExperience() {
        return experience;
    }

    public void setExperience(Integer experience) {
        this.experience = experience;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<Specialization> getSpecializations() {
        return specializations;
    }

    public void setSpecializations(List<Specialization> specializations) {
        this.specializations = specializations;
    }

    public WorkingTime getWorkingTime() {
        return workingTime;
    }

    public void setWorkingTime(WorkingTime workingTime) {
        this.workingTime = workingTime;
    }

    @Override
    public String toString() {
        return "Trainer{" +
                "description='" + description + '\'' +
                ", experience=" + experience +
                ", price=" + price +
                ", image='" + image + '\'' +
                ", specializations=" + specializations +
                '}';
    }
}
