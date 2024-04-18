package DAO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MessageEntity {
    private int message_id;

   

    @JsonProperty("postedBy")
    private int posted_by;

    @JsonProperty("timePostedEpoch")
    private long time_posted_epoch;

    @JsonProperty("messageText")
    private String message_text;
    public int getPostedBy() {
        return posted_by;
    }

    public void setPostedBy(int postedBy) {
        this.posted_by = postedBy;
    }

    public long getTimePostedEpoch() {
        return time_posted_epoch;
    }

    public void setTimePostedEpoch(long timePostedEpoch) {
        this.time_posted_epoch = timePostedEpoch;
    }

    public String getMessageText() {
        return message_text;
    }

    private int messageId;
    
    public int getMessageId() {
        return message_id;
    }
    
    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public void setMessageText(String messageText) {
        this.message_text = messageText;
    }

}
