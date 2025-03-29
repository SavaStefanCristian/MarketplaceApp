package org.marketplace.client;

import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class ControllerAbstractAnchor extends ControllerAbstract{
    protected AnchorPane parentAnchorPane;
    protected Parent parentRoot;

}
