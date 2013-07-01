package jf.xmleditor;

import java.io.InputStream;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import jf.xmleditor.controllers.ExitController;
import jf.xmleditor.controllers.IndexController;

/**
 *
 * @author Hoàng
 */
public class XMLEditorApp extends Application {
    private ResourceBundle rs;
    private Stage stage;
    private StackPane layerPane = new StackPane();

    @Override
    public void start(final Stage stage) throws Exception {
        this.stage = stage;
        this.stage.setScene(new Scene(layerPane));
        
        // Initialize resources
        rs = ResourceBundle.getBundle("LabelsBundle", Locale.getDefault());
        ResourceBundle.clearCache();

        this.stage.setTitle(rs.getString("Title"));
        this.stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                t.consume();
                try {
                    gotoExit();
                } catch(Exception exc) {}
            }
        });
        
        this.gotoIndex();
        this.stage.show();
    }
    
    private void gotoIndex() throws Exception {
        IndexController c = (IndexController) replaceSceneContent("controllers/Index.fxml");
        c.setOnExitAction(new EventHandler<Event>() {
            @Override
            public void handle(Event t) {
                try {
                    gotoExit();
                } catch(Exception exc) {
                    
                }
            }
        });
    }
    
    private void gotoExit() throws Exception {
        final Parent previousPane = this.stage.getScene().getRoot();
        ExitController c = (ExitController) replaceSceneContent("controllers/Exit.fxml");
        c.setOnCancelAction(new EventHandler<Event>() {
            @Override
            public void handle(Event t) {
                stage.getScene().setRoot(previousPane);
            }
        });
        c.setOnOKAction(new EventHandler<Event>() {
            @Override
            public void handle(Event t) {
                stage.close();
            }
        });
    }

    private Initializable replaceSceneContent(String fxml) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setResources(this.rs);
        InputStream in = XMLEditorApp.class.getResourceAsStream(fxml);
        loader.setBuilderFactory(new JavaFXBuilderFactory());
        loader.setLocation(XMLEditorApp.class.getResource(fxml));
        Parent page;
        try {
            page = (Parent) loader.load(in);
        } finally {
            in.close();
        }
        stage.getScene().setRoot(page);
        return (Initializable) loader.getController();
    }
    
    public static void main(String[] args) throws Exception {
        Application.launch(XMLEditorApp.class, (String[])null);
    }
    
}
