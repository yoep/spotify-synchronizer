package org.synchronizer.spotify.ui.elements;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import org.synchronizer.spotify.ui.Icons;

public class SearchField extends TextField {
    public SearchField() {
        initializeIcon();
    }

    private void initializeIcon() {
        this.getChildren().add(createIconGraph());
        this.setPadding(new Insets(5, 5, 5, 20));
    }

    private Pane createIconGraph() {
        Text text = new Text(Icons.SEARCH);

        text.getStyleClass().add("icon");

        StackPane wrapperPane = new StackPane(text);
        wrapperPane.setManaged(false);
        wrapperPane.setAlignment(Pos.CENTER_LEFT);
        wrapperPane.setPadding(new Insets(25, 0, 0, 5));

        return wrapperPane;
    }
}
