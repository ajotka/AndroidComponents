package com.tuim.components.pojo;

import java.util.ArrayList;

/**
 * Created by Filip on 2017-06-26.
 */

public class ComponentRequestPOJO {
    private ArrayList<ComponentPOJO> applications;
    private String author;

    public ArrayList<ComponentPOJO> getApplications() {
        return applications;
    }

    public void setApplications(ArrayList<ComponentPOJO> applications) {
        this.applications = applications;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

}
