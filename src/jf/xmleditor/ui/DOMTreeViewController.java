package jf.xmleditor.ui;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * FXML Controller class
 *
 * @author Hoàng
 */
public class DOMTreeViewController extends BorderPane implements Initializable, ChangeListener<TreeItem<DOMTreeViewController.DOMNode>> {
    private Document doc;
    @FXML
    private TreeView<DOMNode> tv;
    @FXML
    private TextField textSearch;
    @FXML
    private Label lbSearchStatus;
    private ObjectProperty<TreeItem<DOMNode>> selectedItemProperty = new SimpleObjectProperty<>();
    private ObservableList<TreeItem<DOMNode>> searchResults;
    private int csri = -1;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tv.getSelectionModel().selectedItemProperty().addListener(this);
        tv.setEditable(true);
        tv.setCellFactory(new Callback<TreeView<DOMNode>, TreeCell<DOMNode>>() {
            @Override
            public TreeCell<DOMNode> call(TreeView<DOMNode> p) {
                return new TextFieldTreeCellImpl();
            }
        });
        textSearch.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> ov, String t, String t1) {
                csri = -1;
            }
        });
        textSearch.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent t) {
                if (t.getCode() == KeyCode.ENTER) {
                    processSearchClick(new ActionEvent());
                }
            }
        });
    }

    public void loadDOM(Document doc) {
        this.doc = doc;
        TreeItem<DOMNode> rootNode = new TreeItem<>(new DOMNode(this.doc.getDocumentElement()));
        createTree(rootNode, rootNode.getValue());
        tv.setRoot(rootNode);
    }

    @FXML
    private void processSearchClick(ActionEvent t) {
        if (!textSearch.getText().isEmpty()) {
            if (csri == -1) {
                try {
                    searchResults = searchNodes(textSearch.getText());
                } catch (Exception exc) {
                    lbSearchStatus.setText(exc.getLocalizedMessage());
                }
            }

            if (searchResults.size() != 0) {
                csri++;
                selectItem(searchResults.get(csri));
                lbSearchStatus.setText("Result: #" + (csri + 1) + "/" + searchResults.size());
                csri = csri == (searchResults.size() - 1) ? -1 : csri;
            } else {
                lbSearchStatus.setText("Result: not found!");
            }
        }
    }

    private void createTree(TreeItem<DOMNode> i, DOMNode n) {
        i.setExpanded(true);

        if (n.getNode().getNodeType() == Node.ELEMENT_NODE) {
            // build attributes
            for (int x = 0; x < n.getNode().getAttributes().getLength(); x++) {
                createNode(i, n.getNode().getAttributes().item(x));
            }

            // build sub-elements
            for (int x = 0; x < n.getNode().getChildNodes().getLength(); x++) {
                Node tn = n.getNode().getChildNodes().item(x);
                if (tn.getNodeType() == Node.ELEMENT_NODE || tn.getNodeType() == Node.CDATA_SECTION_NODE) {
                    createNode(i, tn);
                }
            }
        }
    }

    private void createNode(TreeItem<DOMNode> i, Node tn) {
        DOMNode dn = new DOMNode(tn);
        TreeItem<DOMNode> ti = new TreeItem<>(dn);
        i.getChildren().add(ti);
        createTree(ti, dn);
    }

    @Override
    public void changed(ObservableValue<? extends TreeItem<DOMNode>> ov, TreeItem<DOMNode> t, TreeItem<DOMNode> t1) {
        this.selectedItemProperty.set(t1);
    }

    public ObjectProperty<TreeItem<DOMNode>> selectedItemProperty() {
        return this.selectedItemProperty;
    }

    public void selectItem(final TreeItem<DOMNode> i) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                tv.getSelectionModel().select(i);
                tv.scrollTo(tv.getSelectionModel().getSelectedIndex());
            }
        });
    }

    public ObservableList<TreeItem<DOMNode>> searchNodes(String expr) throws Exception {
        Node n = tv.getRoot().getValue().getNode();
        XPath xpath = XPathFactory.newInstance().newXPath();
        NodeList ns = (NodeList) xpath.evaluate(expr, n.getOwnerDocument(), XPathConstants.NODESET);

        ObservableList<TreeItem<DOMNode>> list = FXCollections.observableArrayList();
        for (int x = 0; x < ns.getLength(); x++) {
            searchNode(ns.item(x), tv.getRoot(), list);
        }

        return list;
    }

    private void searchNode(Node n, TreeItem<DOMNode> parent, ObservableList<TreeItem<DOMNode>> result) {
        if (parent.getValue().getNode() == n) {
            result.add(parent);
        }

        for (int x = 0; x < parent.getChildren().size(); x++) {
            searchNode(n, parent.getChildren().get(x), result);
        }
    }

    public static DOMNode createDOMNode(Node n) {
        return new DOMNode(n);
    }

    public static class DOMNode {

        private ObjectProperty<Node> node = new SimpleObjectProperty<>();

        public DOMNode(Node n) {
            this.node.set(n);
        }

        public Node getNode() {
            return node.get();
        }

        public boolean isAttributeNode() {
            return getNode().getNodeType() == Node.ATTRIBUTE_NODE;
        }

        public boolean isSetable() {
            return !getNode().hasChildNodes();
        }

        @Override
        public String toString() {
            return (isAttributeNode() ? "@" + this.getNode().getNodeName() + " = " + this.getNode().getNodeValue() : "" + this.getNode().getNodeName());
        }
    }

    private final class TextFieldTreeCellImpl extends TreeCell<DOMNode> {

        private TextField textField;
        private ContextMenu addMenu = new ContextMenu();

        public TextFieldTreeCellImpl() {
            MenuItem miAddElement = new MenuItem("Add element");
            miAddElement.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent t) {
                    if(getItem().getNode().getNodeType() != Node.ELEMENT_NODE) return;
                    
                    Document doc = getItem().getNode().getOwnerDocument();
                    Node n = doc.createElement("NewElement");
                    getItem().getNode().appendChild(n);

                    TreeItem<DOMNode> ti = new TreeItem<>(new DOMNode(n));
                    getTreeItem().getChildren().add(ti);
                }
            });
            addMenu.getItems().add(miAddElement);

            MenuItem miAddAttribute = new MenuItem("Add attribute");
            miAddAttribute.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent t) {
                    if(getItem().getNode().getNodeType() != Node.ELEMENT_NODE) return;
                    
                    Document doc = getItem().getNode().getOwnerDocument();
                    Node n = doc.createAttribute("NewAttribute");
                    ((Element) getItem().getNode()).getAttributes().setNamedItem(n);

                    TreeItem<DOMNode> ti = new TreeItem<>(new DOMNode(n));
                    getTreeItem().getChildren().add(ti);
                }
            });
            addMenu.getItems().add(miAddAttribute);

            MenuItem miAddCData = new MenuItem("Add CDATA section");
            miAddCData.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent t) {
                    if(getItem().getNode().getNodeType() != Node.ELEMENT_NODE || !getItem().isSetable()) return;
                    
                    Document doc = getItem().getNode().getOwnerDocument();
                    Node n = doc.createCDATASection("");
                    getItem().getNode().appendChild(n);

                    TreeItem<DOMNode> ti = new TreeItem<>(new DOMNode(n));
                    getTreeItem().getChildren().add(ti);
                }
            });
            addMenu.getItems().add(miAddCData);

            MenuItem miDelete = new MenuItem("Delete node");
            miDelete.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent t) {
                    if(getItem().getNode().getNodeType() == Node.ATTRIBUTE_NODE) {
                        Attr attr = (Attr) getItem().getNode();
                        attr.getOwnerElement().removeAttributeNode(attr);
                    } else {
                        Node parent = getItem().getNode().getParentNode();
                        parent.removeChild(getItem().getNode());
                    }
                    
                    getTreeItem().getParent().getChildren().removeAll(getTreeItem());
                }
            });
            addMenu.getItems().add(miDelete);
        }

        @Override
        public void startEdit() {
            super.startEdit();

            if (textField == null) {
                createTextField();
            }
            textField.setText(getItem().getNode().getNodeName());
            setText(null);
            setGraphic(textField);
            textField.selectAll();
        }

        @Override
        public void cancelEdit() {
            super.cancelEdit();

            setText(getItem().toString());
            setGraphic(getTreeItem().getGraphic());
        }

        @Override
        public void commitEdit(DOMNode n) {
            super.commitEdit(n);

            try {
                n.getNode().getOwnerDocument().renameNode(n.getNode(), n.getNode().getNamespaceURI(), textField.getText());
            } catch(Exception exc) {
                lbSearchStatus.setText(exc.getLocalizedMessage());
            }
            setText(n.toString());
        }

        @Override
        public void updateItem(DOMNode item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                if (isEditing()) {
                    if (textField != null) {
                        textField.setText(getEditingString());
                    }
                    setText(null);
                    setGraphic(textField);
                } else {
                    setText(getDisplayString());
                    setGraphic(getTreeItem().getGraphic());
                    setContextMenu(addMenu);
                }
            }
        }

        private void createTextField() {
            textField = new TextField(getDisplayString());
            textField.setOnKeyReleased(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent t) {
                    if (t.getCode() == KeyCode.ENTER) {
                        commitEdit(getItem());
                    } else if (t.getCode() == KeyCode.ESCAPE) {
                        cancelEdit();
                    }
                }
            });
        }

        private String getDisplayString() {
            return getItem().toString();
        }

        private String getEditingString() {
            return getItem() == null ? "" : getItem().getNode().getNodeName();
        }
    }
}
