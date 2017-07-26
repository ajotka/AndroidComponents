package project.pojo;

public class ResponsePOJO {
    private String result;
    private String description;

    public ResponsePOJO(String result, String description){
        this.result = result;
        this.description = description;
    }
    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
