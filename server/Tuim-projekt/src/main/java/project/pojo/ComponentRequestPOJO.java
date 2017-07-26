package project.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.ArrayList;
import java.util.Date;

public class ComponentRequestPOJO {
    private ArrayList<ComponentPOJO> applications;
    private String author;

    public ComponentRequestPOJO(){}

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
