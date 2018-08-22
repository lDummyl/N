package Main;

import java.util.Objects;

public class Session {

    private String timestamp;

    private String seconds;

    private String userId;

    private String url;


    public Session(String timestamp, String seconds, String userId, String url) {
        this.timestamp = timestamp;
        this.seconds = seconds;
        this.userId = userId;
        this.url = url;
    }

    public String getTimestamp ()
    {
        return timestamp;
    }

    public void setTimestamp (String timestamp)
    {
        this.timestamp = timestamp;
    }

    public String getSeconds ()
    {
        return seconds;
    }

    public void setSeconds (String seconds)
    {
        this.seconds = seconds;
    }

    public String getUserId ()
    {
        return userId;
    }

    public void setUserId (String userId)
    {
        this.userId = userId;
    }

    public String getUrl ()
    {
        return url;
    }

    public void setUrl (String url)
    {
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Session s = (Session) o;
        return Objects.equals(timestamp, s.timestamp) &&
                Objects.equals(seconds, s.seconds) &&
                Objects.equals(userId, s.userId) &&
                Objects.equals(url, s.url);
    }

    @Override
    public int hashCode() {

        return Objects.hash(timestamp, seconds, userId, url);
    }

    @Override
    public String toString()
    {
        return "ClassPojo [timestamp = "+timestamp+", seconds = "+seconds+", userId = "+userId+", url = "+url+"]";
    }
}

