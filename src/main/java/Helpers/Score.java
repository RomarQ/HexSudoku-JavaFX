package Helpers;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Score extends RecursiveTreeObject<Score> {

    final StringProperty user;
    final StringProperty timeLapsed;
    final StringProperty difficulty;

    public Score (User u, int time, int diff) {
        user = new SimpleStringProperty(u.Username());
        timeLapsed = new SimpleStringProperty(Time.getTimeLapsed(time));

        switch (diff) {
            case 1: difficulty = new SimpleStringProperty("Easy"); break;
            case 2: difficulty = new SimpleStringProperty("Normal"); break;
            case 3: difficulty = new SimpleStringProperty("Hard"); break;
            default: difficulty = new SimpleStringProperty("Easy");
        }
    }

    public StringProperty getUser() { return user; }

    public StringProperty getTotalTime() { return timeLapsed; }

    public StringProperty getDifficulty() { return difficulty; }
}
