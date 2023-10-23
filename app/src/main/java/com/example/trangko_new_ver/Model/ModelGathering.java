package com.example.trangko_new_ver.Model;

public class ModelGathering {
    String createdBy, evenTitle, eventData, eventDescription, eventLocation, eventTime, gatheringId, participants;

    public ModelGathering() {
    }

    public ModelGathering(String createdBy, String evenTitle, String eventData, String eventDescription, String eventLocation, String eventTime, String gatheringId, String participants) {
        this.createdBy = createdBy;
        this.evenTitle = evenTitle;
        this.eventData = eventData;
        this.eventDescription = eventDescription;
        this.eventLocation = eventLocation;
        this.eventTime = eventTime;
        this.gatheringId = gatheringId;
        this.participants = participants;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getEvenTitle() {
        return evenTitle;
    }

    public void setEvenTitle(String evenTitle) {
        this.evenTitle = evenTitle;
    }

    public String getEventData() {
        return eventData;
    }

    public void setEventData(String eventData) {
        this.eventData = eventData;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public String getGatheringId() {
        return gatheringId;
    }

    public void setGatheringId(String gatheringId) {
        this.gatheringId = gatheringId;
    }

    public String getParticipants() {
        return participants;
    }

    public void setParticipants(String participants) {
        this.participants = participants;
    }
}
