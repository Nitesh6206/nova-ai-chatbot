package com.nitesh.novaai.chatbot.backend.Service;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Service
public class GoogleCalendarService {

    public String createEvent(String userEmail, String title, LocalDateTime startTime, String description) {
        try {
            // Note: Production mein proper OAuth2 token flow use karna padega
            // Abhi ke liye simplified version (user ke primary calendar mein add hoga)

            Calendar service = new Calendar.Builder(
                    new NetHttpTransport(),
                    GsonFactory.getDefaultInstance(),
                    null)
                    .setApplicationName("Nova AI Chatbot")
                    .build();

            Event event = new Event()
                    .setSummary(title)
                    .setDescription(description + "\n\nCreated by Nova AI");

            // Start Time
            EventDateTime start = new EventDateTime()
                    .setDateTime(new com.google.api.client.util.DateTime(
                            startTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()))
                    .setTimeZone(ZoneId.systemDefault().toString());

            // End Time (1 hour later)
            EventDateTime end = new EventDateTime()
                    .setDateTime(new com.google.api.client.util.DateTime(
                            startTime.plusHours(1).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()))
                    .setTimeZone(ZoneId.systemDefault().toString());

            event.setStart(start);
            event.setEnd(end);

            String calendarId = "primary";
            Event createdEvent = service.events().insert(calendarId, event).execute();

            return "✅ Event successfully created in your Google Calendar!\n" +
                    "Title: " + title + "\n" +
                    "Time: " + startTime + "\n" +
                    "Link: " + createdEvent.getHtmlLink();

        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Failed to create calendar event: " + e.getMessage();
        }
    }
}