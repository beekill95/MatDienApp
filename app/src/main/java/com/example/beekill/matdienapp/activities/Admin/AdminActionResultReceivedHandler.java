package com.example.beekill.matdienapp.activities.Admin;

/**
 * Created by beekill on 9/5/16.
 */
public interface AdminActionResultReceivedHandler {
    void handleResult(boolean result, AdminData adminData, AdminAction action);
}
