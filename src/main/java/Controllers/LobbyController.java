package Controllers;

import Helpers.Score;
import Helpers.User;
import Helpers.DatabaseConnector;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import io.datafx.controller.ViewController;
import io.datafx.controller.flow.FlowHandler;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TreeTableColumn;

import javax.annotation.PostConstruct;
import java.util.function.Function;

@ViewController(value = "/fxml/LobbyPage.fxml", title = "Lobby Page")
public class LobbyController {

    int difficulty = 2;

    FlowHandler contentFlowHandler;

    @FXMLViewFlowContext
    ViewFlowContext context = new ViewFlowContext();

    @FXML
    private Label username;

    @FXML
    private JFXTreeTableView<Score> scoreTable;
    @FXML
    private JFXTreeTableColumn<Score, String> usernameColumn;
    @FXML
    private JFXTreeTableColumn<Score, String> timeColumn;
    @FXML
    private JFXTreeTableColumn<Score, String> diffColumn;

    @FXML
    private Button playBtn;

    @FXML
    private Button logoutBtn;

    @FXML
    private Button easyBtn;
    @FXML
    private Button normalBtn;
    @FXML
    private Button hardBtn;

    /**
     * init fxml when loaded.
     */
    @PostConstruct
    public void init() {
        contentFlowHandler = (FlowHandler) context.getRegisteredObject("ContentFlowHandler");
        DatabaseConnector db = (DatabaseConnector) context.getRegisteredObject("db");
        User user = (User) context.getRegisteredObject("user");

        username.setText(user.Username().toUpperCase());

        setupScoresTable(db);

        logoutBtn.setOnAction(event -> {
            try {
                contentFlowHandler.navigateTo(LoginController.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        playBtn.setOnAction(event -> {
            try {
                context.register("difficulty", difficulty);
                contentFlowHandler.navigateTo(GameController.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        easyBtn.setOnAction(e -> {
            easyBtn.setStyle("-fx-opacity: 1");
            normalBtn.setStyle("-fx-opacity: 0.5");
            hardBtn.setStyle("-fx-opacity: 0.5");
            difficulty = 1;
        });

        normalBtn.setOnAction(e -> {
            easyBtn.setStyle("-fx-opacity: 0.5");
            normalBtn.setStyle("-fx-opacity: 1");
            hardBtn.setStyle("-fx-opacity: 0.5");
            difficulty = 2;
        });

        hardBtn.setOnAction(e -> {
            easyBtn.setStyle("-fx-opacity: 0.5");
            normalBtn.setStyle("-fx-opacity: 0.5");
            hardBtn.setStyle("-fx-opacity: 1");
            difficulty = 3;
        });
    }

    private <T> void setupCellValueFactory(JFXTreeTableColumn<Score, T> column, Function<Score, ObservableValue<T>> mapper) {
        column.setCellValueFactory((TreeTableColumn.CellDataFeatures<Score, T> param) -> {
            if (column.validateValue(param)) {
                return mapper.apply(param.getValue().getValue());
            } else {
                return column.getComputedValue(param);
            }
        });
    }

    private void setupScoresTable(DatabaseConnector db) {

        setupCellValueFactory(usernameColumn, Score::getUser);
        setupCellValueFactory(timeColumn, Score::getTotalTime);
        setupCellValueFactory(diffColumn, Score::getDifficulty);

        ObservableList<Score> scores = db.getScores();

        if (scores == null) {
            scoreTable.setPlaceholder(new Label("There is no Scores yet!"));
            scoreTable.getPlaceholder().setStyle("-fx-font-size: 36px; -fx-text-fill: #5264AE;");
            return;
        }

        scoreTable.setRoot(new RecursiveTreeItem<>(scores, RecursiveTreeObject::getChildren));

        scoreTable.setShowRoot(false);
    }

}
