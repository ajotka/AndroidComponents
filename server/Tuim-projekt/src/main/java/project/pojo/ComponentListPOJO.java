package project.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.ArrayList;
import java.util.Date;

public class ComponentListPOJO {
    private long id;
    private String author;
    @JsonFormat(pattern="yyyy-MM-dd@HH:mm:ss", locale = "pl_PL")
    private Date uploadDate;
    private ArrayList<ComponentPOJO> applications;

    public ComponentListPOJO(){
        uploadDate = new Date();
        uploadDate.setTime(new Date().getTime() /*+ 7200000*/);
        applications = new ArrayList<>();
        id = uploadDate.getTime();
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Date getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }

    public ArrayList<ComponentPOJO> getApplications() {
        return applications;
    }

    public void setApplications(ArrayList<ComponentPOJO> applications) {
        this.applications = applications;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
