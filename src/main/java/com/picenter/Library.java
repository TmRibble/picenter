package com.picenter;

import java.util.Collections;
import java.util.List;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.CalendarScopes;

public class Library {

    // app options
    public static final String APP_VERSION = "1.0.0";

    public static final String APP_PRIVATE_NAME = "picenter";
    public static final String APP_PUBLIC_NAME = "Pi Center";

    // network options
    public static int NETWORK_BACKEND_PORT = 1331;
    public static int NETWORK_FRONTEND_PORT = 3113;

    public static String NETWORK_BACKEND_ADDRESS = "localhost";

    // graphics options
    public static final int WINDOW_WIDTH = 900;
    public static final int WINDOW_HEIGHT = 600;

    // calender options
    public static final String CALENDER_CREDS_PATH = "D:\\AppData\\PiSpot\\data\\credentials.json";
    public static final JsonFactory CALENDER_JSON_FACTORY = GsonFactory.getDefaultInstance();
    public static final String CALENDER_TOKENS_PATH = "tokens";
    public static final List<String> CALENDER_SCOPES = Collections.singletonList(CalendarScopes.CALENDAR_READONLY);

    //music options
    public static String MUSIC_DATA_DIRECTORY = "music";
}