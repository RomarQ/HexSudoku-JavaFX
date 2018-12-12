package Controllers;

import Helpers.Sudoku;
import Helpers.Time;
import Helpers.User;
import Helpers.DatabaseConnector;
import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import io.datafx.controller.ViewController;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.flow.FlowHandler;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import io.datafx.controller.util.VetoException;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.annotation.PostConstruct;
import java.util.concurrent.atomic.AtomicInteger;

@ViewController(value = "/fxml/GamePage.fxml", title = "Game Page")
public class GameController {

    Sudoku game;
    User user;
    AtomicInteger gameDuration;
    DatabaseConnector db;
    int difficulty;

    Timeline timer;

    FlowHandler contentFlowHandler;

    @FXMLViewFlowContext
    ViewFlowContext context = new ViewFlowContext();

    @FXML
    private GridPane board;

    @FXML
    private Label gameTimer;

    @FXML
    private Button pauseBtn;
    @FXML
    private Button quitBtn;
    @FXML
    private Button clearBtn;

    /**
     * init fxml when loaded.
     */
    @PostConstruct
    public void init() {

        contentFlowHandler = (FlowHandler) context.getRegisteredObject("ContentFlowHandler");
        db = (DatabaseConnector) context.getRegisteredObject("db");
        user = (User) context.getRegisteredObject("user");
        difficulty = (context.getRegisteredObject("difficulty") != null) ? (int) context.getRegisteredObject("difficulty") : 1;

        game = new Sudoku(difficulty-1);
        game.getBoard(board);
        board.setGridLinesVisible(true);
        startTimer(gameTimer);

        pauseBtn.setOnAction((event) -> {
            if (timer.getStatus() == Animation.Status.PAUSED) {
                timer.play();
                board.setDisable(false);
                board.setVisible(true);
                pauseBtn.setText("Pause");
            }
            else {
                timer.pause();
                board.setDisable(true);
                board.setVisible(false);
                pauseBtn.setText("Resume");
            }

        });

        quitBtn.setOnAction(e -> {
            try {
                contentFlowHandler.navigateTo(LobbyController.class);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });

        clearBtn.setOnAction(e -> {
            try {
                game.resetBoard();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });

    }

    public AtomicInteger startTimer(Label gTimer) {
        gameDuration = new AtomicInteger();

        timer = new Timeline(new KeyFrame(Duration.seconds(1), ev -> {
            gameDuration.getAndIncrement();
            gTimer.setText(Time.getTimeLapsed(gameDuration.get()));
        }));

        timer.setCycleCount(Animation.INDEFINITE);
        timer.setDelay(Duration.millis(300));
        timer.play();

        return gameDuration;
    }

    @FXML
    private void addValue(ActionEvent event) {
        Button b = (Button) event.getSource();
        if (game.changeFieldValue(b.getText())) {
            try {
                timer.stop();
                db.addScore(user.UserId(), gameDuration.get(), difficulty);

                JFXAlert alert = new JFXAlert((Stage) board.getScene().getWindow());
                alert.initModality(Modality.APPLICATION_MODAL);
                alert.setOverlayClose(false);
                JFXDialogLayout layout = new JFXDialogLayout();

                // Modal header
                Label header = new Label("Congratulations!!!");
                header.getStyleClass().add("modalHeader");
                HBox headerRow = new HBox(header);
                headerRow.setAlignment(Pos.CENTER);
                layout.setHeading(headerRow);

                // Modal body ( Will display TimeLapsed and Difficulty )
                Label diff = new Label(getDiffString());
                diff.getStyleClass().add("diffLabel");
                HBox diffRow = new HBox(diff);
                diffRow.getStyleClass().add(difficulty == 1 ? "diffBoxE": (difficulty == 2 ? "diffBoxN": "diffBoxH"));
                diffRow.setAlignment(Pos.CENTER);
                Label rowLeft = new Label("Total time: ");
                rowLeft.getStyleClass().add("timeRowLeft");
                Label rowRight = new Label(Time.getPrettyTime(gameDuration.get()));
                rowRight.getStyleClass().add("timeRowRight");
                HBox timeRow = new HBox(rowLeft, rowRight);
                timeRow.getStyleClass().add("timeBox");
                timeRow.setAlignment(Pos.CENTER);
                VBox body = new VBox(timeRow, diffRow);
                body.getStyleClass().add("bodyModal");
                layout.setBody(body);


                JFXButton tryAgainBtn = new JFXButton("New game");
                JFXButton endBtn = new JFXButton("Exit");
                tryAgainBtn.getStyleClass().add("dialog-accept");
                endBtn.getStyleClass().add("dialog-exit");
                tryAgainBtn.setOnAction(e -> { alert.hideWithAnimation(); init(); });
                endBtn.setOnAction(e -> {
                    alert.hideWithAnimation();
                    try {
                        contentFlowHandler.navigateTo(LobbyController.class);
                    } catch (VetoException e1) {
                        e1.printStackTrace();
                    } catch (FlowException e1) {
                        e1.printStackTrace();
                    }
                });
                layout.setActions(tryAgainBtn, endBtn);
                alert.setContent(layout);
                alert.show();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String getDiffString() {
        switch (difficulty) {
            case 1: return "Easy";
            case 2: return "Normal";
            case 3: return "Hard";
        }
        return "Easy";
    }

}
