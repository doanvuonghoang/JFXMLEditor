package jf.xmleditor.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;

/**
 *
 * @author Hoàng
 */
public class DOMNodeEditor extends BorderPane {
    private final FXMLLoader loader = new FXMLLoader(getClass().getResource("DOMNodeEditor.fxml"));
    
    public DOMNodeEditor() {
        loader.setRoot(this);
        try {
            loader.load();
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }
    
    public void setNode(DOMTreeViewController.DOMNode n) {
        ((DOMNodeEditorController) loader.getController()).setNode(n);
    }
    
    public DOMTreeViewController.DOMNode getNode() {
        return ((DOMNodeEditorController) loader.getController()).getNode();
    }
}
