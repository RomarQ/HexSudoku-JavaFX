package Helpers;

public class Time {
    public static String getTimeLapsed(int timeInSecs) {
        int secs = timeInSecs % 60;
        int mins = (timeInSecs / 60) % 60;
        int hours = ((timeInSecs / 60) / 60) % 24;
        int days = ((timeInSecs / 60) / 60) / 24;

        return String.format("%02d:%02d:%02d:%02d",days, hours, mins, secs);
    }

    public static String getPrettyTime(int timeInSecs) {
        int secs = timeInSecs % 60;
        int mins = (timeInSecs / 60) % 60;
        int hours = ((timeInSecs / 60) / 60) % 24;
        int days = ((timeInSecs / 60) / 60) / 24;

        if (mins == 0 && hours == 0 && days == 0) {
            return String.format("%02d Seconds", secs);
        }
        else if (hours == 0 && days == 0) {
            return String.format("%02d:%02d", mins, secs);
        }
        else if (days == 0) {
            return String.format("%02d:%02d:%02d", hours, mins, secs);
        }

        return String.format("%02d:%02d:%02d:%02d",days, hours, mins, secs);
    }
}
