package com.tuim.components.pojo;

/**
 * Created by Filip on 2017-06-27.
 */

public class ResponsePOJO {
    private String result;
    private String description;

    public ResponsePOJO(String result, String description){
        this.result = result;
        this.description = description;
    }

    public ResponsePOJO(){}

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
