package jf.xmleditor.controllers;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.stage.FileChooser;
import javafx.stage.FileChooserBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import jf.xmleditor.ui.DOMNodeEditor;
import jf.xmleditor.ui.DOMTreeView;
import jf.xmleditor.ui.DOMTreeViewController;
import org.w3c.dom.Document;

/**
 * FXML Controller class
 *
 * @author Hoàng
 */
public class IndexController implements Initializable {

    @FXML
    private DOMTreeView dtv;
    @FXML
    private DOMNodeEditor dne;
    private File xmlfile;
    private Document xmlContent;
    private ObjectProperty<EventHandler<Event>> onExitActionProperty =
            new SimpleObjectProperty<>();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dtv.selectedItemProperty().addListener(new ChangeListener<TreeItem<DOMTreeViewController.DOMNode>>() {
            @Override
            public void changed(ObservableValue<? extends TreeItem<DOMTreeViewController.DOMNode>> ov, TreeItem<DOMTreeViewController.DOMNode> t, TreeItem<DOMTreeViewController.DOMNode> t1) {
                if(t1 != null) dne.setNode(t1.getValue());
            }
        });
    }

    public void setOnExitAction(EventHandler<Event> eh) {
        onExitActionProperty.set(eh);
    }

    @FXML
    protected void processExitAction() {
        if (onExitActionProperty.get() != null) {
            onExitActionProperty.get().handle(new ActionEvent());
        }
    }

    @FXML
    protected void processOpenAction(ActionEvent ae) {
        FileChooser fc = FileChooserBuilder.create()
                .extensionFilters(new FileChooser.ExtensionFilter("XML files", "*.xml", "*.html", "*.xhtml", "*.xslt", "*.xsl"))
                .build();
        xmlfile = fc.showOpenDialog(((Node) ae.getSource()).getScene().getWindow());
        try {
            if (xmlfile.exists()) {
                xmlContent = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlfile);
            } else {
                xmlContent = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            }

            // load DOM to tree view
            dtv.loadDOM(xmlContent);
        } catch (Exception exc) {
            exc.printStackTrace();
        }

    }

    @FXML
    protected void processSaveAction(ActionEvent ae) throws Exception {
        saveXMLContent();
    }

    @FXML
    protected void processSaveAsAction(ActionEvent ae) throws Exception {
        FileChooser fc = FileChooserBuilder.create()
                .extensionFilters(new FileChooser.ExtensionFilter("XML files", "*.xml", "*.html", "*.xhtml", "*.xslt", "*.xsl"))
                .build();
        xmlfile = fc.showSaveDialog(((Node) ae.getSource()).getScene().getWindow());
        if (xmlfile.exists()) {
            // TODO: Show confirm box to overwrite file or not
        } else {
            saveXMLContent();
        }

    }

    protected void saveXMLContent() throws Exception {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer t = tf.newTransformer();
        
        DOMSource ds = new DOMSource(xmlContent);
        StreamResult sr = new StreamResult(xmlfile);
        t.transform(ds, sr);
    }
    
    @FXML protected void processCTRL_SAction() throws Exception {
        saveXMLContent();
    }
}
