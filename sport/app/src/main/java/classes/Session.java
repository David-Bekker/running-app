package classes;

import java.util.Date;

public class Session extends Point {
    private double time;
    private double length;
    private String date;

    public Session(double latitude, double longitude, double time, double length, String date) {
        super(latitude, longitude);
        this.time = time;
        this.length = length;
        this.date = date;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Session{" +
                "time=" + time +
                ", length=" + length +
                ", date=" + date +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
