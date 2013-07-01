package jf.xmleditor.ui;

import javafx.beans.property.ObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.BorderPane;
import org.w3c.dom.Document;

/**
 *
 * @author Hoàng
 */
public class DOMTreeView extends BorderPane {
    private final FXMLLoader loader = new FXMLLoader(getClass().getResource("DOMTreeView.fxml"));
    
    public DOMTreeView() {
        loader.setRoot(this);

        try {
            loader.load();
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }
    
    public void loadDOM(Document doc) {
        ((DOMTreeViewController) loader.getController()).loadDOM(doc);
    }
    
    public ObjectProperty<TreeItem<DOMTreeViewController.DOMNode>> selectedItemProperty() {
        return ((DOMTreeViewController) loader.getController()).selectedItemProperty();
    }
}
