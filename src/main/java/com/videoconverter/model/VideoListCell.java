package com.videoconverter.model;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;

public class VideoListCell extends ListCell<VideoFile> {

    private final HBox root;
    private final Label nameLabel;
    private final Label pathLabel;
    private final Label sizeLabel;

    public VideoListCell() {
        SVGPath icon = new SVGPath();
        icon.setContent("M18 4l2 4h-3l-2-4h-2l2 4h-3l-2-4H8l2 4H7L5 4H4c-1.1 0-1.99.9-1.99 2L2 18c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V4h-4z");
        icon.getStyleClass().add("video-icon");

        nameLabel = new Label();
        nameLabel.getStyleClass().add("video-name");
        
        pathLabel = new Label();
        pathLabel.getStyleClass().add("video-path");

        VBox textContainer = new VBox(2, nameLabel, pathLabel);
        textContainer.setAlignment(Pos.CENTER_LEFT);

        sizeLabel = new Label();
        sizeLabel.getStyleClass().add("video-size");

        root = new HBox(15);
        root.setAlignment(Pos.CENTER_LEFT);
        root.getStyleClass().add("video-cell-root");
        
        HBox.setHgrow(textContainer, Priority.ALWAYS);
        
        root.getChildren().addAll(icon, textContainer, sizeLabel);
    }

    @Override
    protected void updateItem(VideoFile item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setGraphic(null);
            setText(null);
        } else {
            nameLabel.setText(item.getFile().getName());
            pathLabel.setText(item.getPath());
            sizeLabel.setText(item.getFormattedSize());

            setGraphic(root);
            setText(null);
        }
    }
}