package com.example.beekill.matdienapp.activities.admin;

import com.example.beekill.matdienapp.protocol.Response;

/**
 * Created by beekill on 9/5/16.
 */
interface AdminFragmentCommonInterface {
    void handleResult(Response response, AdminData adminData, AdminAction action);
    void displayData(AdminData data);
}
