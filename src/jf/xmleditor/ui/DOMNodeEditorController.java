package jf.xmleditor.ui;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.web.HTMLEditor;

/**
 * FXML Controller class
 *
 * @author Hoàng
 */
public class DOMNodeEditorController implements Initializable {

    @FXML
    private HTMLEditor htmlEditor;
    @FXML
    private TextArea textEditor;
    @FXML
    private Button lbStatus;
    private DOMTreeViewController.DOMNode currentNode;
    private boolean textChanged = false;
    private boolean htmlChanged = false;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        htmlEditor.addEventHandler(EventType.ROOT, new EventHandler<Event>() {
            @Override
            public void handle(Event t) {
                if (!textChanged) {
                    htmlChanged = true;
                    int ccp = textEditor.getCaretPosition();

                    textEditor.setText(htmlEditor.getHtmlText().replaceAll("<[/]?html>|<[/]?head>|<[/]?body[^>]*>", ""));
                    textEditor.selectRange(ccp, ccp);

                    htmlChanged = false;
                }
            }
        });

        textEditor.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> ov, String t, final String t1) {
                if (!htmlChanged) {
                    textChanged = true;

                    // Update modification status
                    lbStatus.setText("changed");

                    htmlEditor.setHtmlText("<html><body contentEditable='true'>" + t1 + "</body></html>");

                    textChanged = false;
                }

                // Update node value
                if(currentNode != null) currentNode.getNode().setNodeValue(t1);
            }
        });
    }

    public void setNode(DOMTreeViewController.DOMNode n) {
        currentNode = n;

        if (currentNode.isAttributeNode()) {
            htmlEditor.setDisable(true);
            textEditor.setDisable(false);
        } else if (!currentNode.isSetable()) {
            htmlEditor.setDisable(true);
            textEditor.setDisable(true);
        } else {
            htmlEditor.setDisable(false);
            textEditor.setDisable(false);
        }

        textEditor.setText(currentNode.getNode().getNodeValue());
    }

    public DOMTreeViewController.DOMNode getNode() {
        return currentNode;
    }
}
