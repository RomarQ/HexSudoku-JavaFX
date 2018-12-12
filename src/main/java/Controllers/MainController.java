package Controllers;

import com.jfoenix.controls.*;
import Helpers.DatabaseConnector;
import io.datafx.controller.ViewController;
import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.FlowHandler;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;

import javax.annotation.PostConstruct;


@ViewController(value = "/fxml/main.fxml", title = "HexSudoku")
public final class MainController {

    private DatabaseConnector db;

    @FXMLViewFlowContext
    private ViewFlowContext context;

    @FXML
    private JFXDrawer drawer;

    /**
     * init fxml when loaded.
     */
    @PostConstruct
    public void init() throws Exception {

        // Instantiate DB
        try {
            db = new DatabaseConnector();
            // Creates DATABASE if it doesn't exist
            db.createTables();
        } catch(Exception e) {
            System.err.println("Couldn't create Database :(");
        }

        context = new ViewFlowContext();
        // set the default controller
        Flow innerFlow = new Flow(LoginController.class);

        final FlowHandler flowHandler = innerFlow.createHandler(context);
        context.register("ContentFlowHandler", flowHandler);
        context.register("db", db);
        context.register("ContentFlow", innerFlow);
        drawer.setContent(flowHandler.start());
        context.register("ContentPane", drawer.getContent().get(0));

    }
}
