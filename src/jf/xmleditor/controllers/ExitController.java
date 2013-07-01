/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jf.xmleditor.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;

/**
 * FXML Controller class
 *
 * @author Hoàng
 */
public class ExitController implements Initializable {
    private ObjectProperty<EventHandler<Event>> onCancelAction = new SimpleObjectProperty<>();
    private ObjectProperty<EventHandler<Event>> onOKAction = new SimpleObjectProperty<>();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    public void setOnCancelAction(EventHandler<Event> eh) {
        onCancelAction.set(eh);
    }
    
    @FXML protected void processCancelAction(Event t) {
        if(onCancelAction.get() != null) {
            onCancelAction.get().handle(t);
        }
    }
    
    public void setOnOKAction(EventHandler<Event> eh) {
        onOKAction.set(eh);
    }
    
    @FXML protected void processOKAction(Event t) {
        if(onOKAction.get() != null) {
            onOKAction.get().handle(t);
        }
    }
}
