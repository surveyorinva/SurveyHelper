package com.survlogic.surveyhelper.activity.staffFeed.model;

import java.util.Date;

public class FeedReflections {

    private String summary;
    private String answer;
    private boolean complete;
    private long completedOn;

    public FeedReflections() {
    }

    public FeedReflections(FeedReflections reflection){
        this.summary = reflection.summary;
        this.answer = reflection.answer;
        this.complete = reflection.complete;
        this.completedOn = reflection.completedOn;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public long getCompletedOn() {
        return completedOn;
    }

    public void setCompletedOn(long completedOn) {
        this.completedOn = completedOn;
    }
}
