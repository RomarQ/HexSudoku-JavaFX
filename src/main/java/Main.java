import Controllers.MainController;
import com.jfoenix.controls.JFXDecorator;
import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.container.DefaultFlowContainer;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Main extends Application {

    @FXMLViewFlowContext
    private ViewFlowContext flowContext;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        Flow flow = new Flow(MainController.class);
        DefaultFlowContainer container = new DefaultFlowContainer();
        flowContext = new ViewFlowContext();
        flowContext.register("Stage", stage);
        flow.createHandler(flowContext).start(container);

        JFXDecorator decorator = new JFXDecorator(stage, container.getView(), false, true, true);
        decorator.setCustomMaximize(true);
        decorator.setAlignment(Pos.CENTER);

        stage.setTitle("HexSudoku");

        double width = 900;
        double height = 900;

        try {
            Rectangle2D bounds = Screen.getScreens().get(0).getBounds();
            width = bounds.getWidth() / 2;
            height = bounds.getHeight() / 1.1;
        } catch (Exception e){ }

        Scene scene = new Scene(decorator, width, height);

        scene.getStylesheets().addAll(Main.class.getResource("/css/style.css").toExternalForm());

        stage.setScene(scene);
        stage.show();
    }
}

