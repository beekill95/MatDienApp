package com.example.beekill.matdienapp.protocol;

/**
 * Created by beekill on 8/8/16.
 */
public class Response {
    public boolean getResult() {
        return result;
    }

    public String getDescription() {
        return description;
    }

    public String[] getList() {
        return list;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setList(String[] list) {
        this.list = list;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    private boolean result;
    private String description;
    private String[] list;


}
