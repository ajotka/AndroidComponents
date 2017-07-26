package project.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.List;

public class ComponentPOJO {
    private String name;
    private String packageName;
    private String versionNumber;
    private String versionName;
    private String activity;
    @JsonFormat(pattern="yyyy-MM-dd@HH:mm:ss", locale = "pl_PL")
    private Date installedData;
    @JsonFormat(pattern="yyyy-MM-dd@HH:mm:ss", locale = "pl_PL")
    private Date modifiedData;
    private List<String> contentProviders;
    private List<String> permissions;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(String versionNumber) {
        this.versionNumber = versionNumber;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public Date getInstalledData() {
        return installedData;
    }

    public void setInstalledData(Date installedData) {
        this.installedData = installedData;
    }

    public Date getModifiedData() {
        return modifiedData;
    }

    public void setModifiedData(Date modifiedData) {
        this.modifiedData = modifiedData;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    public List<String> getContentProviders() {
        return contentProviders;
    }

    public void setContentProviders(List<String> contentProviders) {
        this.contentProviders = contentProviders;
    }
}
