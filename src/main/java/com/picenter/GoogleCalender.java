package com.picenter;

import java.io.File;
import java.io.FileReader;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;

public class GoogleCalender {

    public static final String SYSTEM_NAME = "picenter_calender";

    // picenter
    private static Logger logger;

    private static boolean active;

    // calender
    private static Credential credential;
    private static Calendar calendar;
    private static NetHttpTransport transport;

    public static boolean init(Logger log) {
        logger = log;

        // first load credentials
        try {

            transport = GoogleNetHttpTransport.newTrustedTransport();
            credential = new AuthorizationCodeInstalledApp(
                new GoogleAuthorizationCodeFlow.Builder(
                    transport, 
                    Library.CALENDER_JSON_FACTORY,
                    GoogleClientSecrets.load(GsonFactory.getDefaultInstance(), new FileReader(new File(Library.CALENDER_CREDS_PATH))),
                    Library.CALENDER_SCOPES
                ).setDataStoreFactory(new FileDataStoreFactory(new java.io.File(Library.CALENDER_TOKENS_PATH)))
                .setAccessType("offline").build(),
                new LocalServerReceiver.Builder().setPort(8888).build()
            ).authorize("user");

            calendar = new Calendar.Builder(transport, Library.CALENDER_JSON_FACTORY, credential)
            .setApplicationName(Library.APP_PUBLIC_NAME)
            .build();
        } catch (Exception e) {
            logger.log("Error while initializing google calender services credential", SYSTEM_NAME, Logger.ERROR);
            logger.logError(e, null, SYSTEM_NAME);
        }
        
        active = true;

        return true;
    }

    public static boolean isActive() {
        return active;
    }

}
