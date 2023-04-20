package code.model;

import java.io.Serializable;

public class DataFeedItem implements Serializable {
    private String dateTime;
    private String value;
    private String feedId;

    public DataFeedItem(String dateTime, String value, String feedId) {
        this.dateTime = dateTime;
        this.value = value;
        this.feedId = feedId;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getValue() {
        return value;
    }

    public String getFeedId() {
        return feedId;
    }
}
