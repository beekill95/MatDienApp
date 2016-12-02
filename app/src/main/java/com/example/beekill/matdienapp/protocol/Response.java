package com.example.beekill.matdienapp.protocol;

import java.util.List;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCurrentAccessPoint() {
        return currentAccessPoint;
    }

    public void setCurrentAccessPoint(String currentAccessPoint) {
        this.currentAccessPoint = currentAccessPoint;
    }

    public List<String> getAvailableAccessPoints() {
        return availableAccessPoints;
    }

    public void setAvailableAccessPoints(List<String> availableAccessPoints) {
        this.availableAccessPoints = availableAccessPoints;
    }

    private boolean result;
    private String description;
    private String[] list;

    // for wifi inquiry (not use description)
    private String status;
    private String currentAccessPoint;
    private List<String> availableAccessPoints;
}
