package Controllers;

import Helpers.User;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import Helpers.DatabaseConnector;
import io.datafx.controller.ViewController;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.flow.FlowHandler;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import io.datafx.controller.util.VetoException;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.sqlite.SQLiteException;

import javax.annotation.PostConstruct;


@ViewController(value = "/fxml/LoginPage.fxml", title = "Login Page")
public class LoginController {

    FlowHandler contentFlowHandler;

    @FXMLViewFlowContext
    ViewFlowContext context = new ViewFlowContext();

    @FXML
    private Label errorLabel;
    @FXML
    private JFXTextField validatedText;
    @FXML
    private JFXPasswordField validatedPassowrd;
    @FXML
    private JFXButton loginBtn;
    @FXML
    private JFXButton registerBtn;

    /**
     * init fxml when loaded.
     */
    @PostConstruct
    public void init() {
        contentFlowHandler = (FlowHandler) context.getRegisteredObject("ContentFlowHandler");
        DatabaseConnector db = (DatabaseConnector) context.getRegisteredObject("db");

        validatedText.focusedProperty().addListener((o, oldVal, newVal) -> {
            errorLabel.setVisible(false);
            if (!newVal) {
                validatedText.validate();
            }
        });
        validatedPassowrd.focusedProperty().addListener((o, oldVal, newVal) -> {
            errorLabel.setVisible(false);
            if (!newVal) {
                validatedPassowrd.validate();
            }
        });
        loginBtn.setOnAction((event) -> {
            if (validatedText.validate() && validatedPassowrd.validate()) {
                String username = validatedText.getText().trim();
                String password = validatedPassowrd.getText();

                try {
                    User user = db.login(username, password);

                    if (user != null) {
                        context.register("user", user);
                        contentFlowHandler.navigateTo(LobbyController.class);
                    } else {
                        errorLabel.setText("User not Found!");
                        errorLabel.setVisible(true);
                    }
                } catch (SQLiteException e) {
                    errorLabel.setText(e.getMessage());
                    validatedText.clear();
                    validatedPassowrd.clear();
                } catch (VetoException | FlowException e) {
                    System.out.println("Error when trying to change content view!");
                }
            }
        });
        registerBtn.setOnAction((event) -> {
            if (validatedText.validate() && validatedPassowrd.validate()) {
                String username = validatedText.getText().trim();
                String password = validatedPassowrd.getText();

                try {
                    int userId = db.register(username, password);
                    context.register("user", new User(userId, username));
                    contentFlowHandler.navigateTo(LobbyController.class);
                } catch (SQLiteException e) {
                    errorLabel.setText(e.getMessage());
                    errorLabel.setVisible(true);
                    validatedText.clear();
                    validatedPassowrd.clear();
                } catch (VetoException | FlowException e) {
                    System.out.println("Error when trying to change content view!");
                }
            }
        });
    }

}
