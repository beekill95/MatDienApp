package com.example.beekill.matdienapp.handlers;
import android.content.Context;

import com.example.beekill.matdienapp.protocol.Response;

/**
 * Created by beekill on 8/13/16.
 */
public interface NotificationHandler {
    void handleNotification(Context context, Response response);
}
