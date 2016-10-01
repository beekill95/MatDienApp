package com.example.beekill.matdienapp.activities.admin;

/**
 * Created by beekill on 9/5/16.
 */
public interface AdminFragmentCommonInterface {
    void handleResult(boolean result, AdminData adminData, AdminAction action);
    void displayData(AdminData data);
}
