package com.videoconverter.view;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogWindow {

    private static LogWindow instance;
    private final Stage stage;
    private final TextArea textArea;

    private LogWindow() {
        stage = new Stage();
        stage.setTitle("Application Logs");
        textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setStyle("-fx-control-inner-background: #1a1a1a; " +
                "-fx-font-family: 'Monospaced'; " +
                "-fx-highlight-fill: #00ff00; " +
                "-fx-highlight-text-fill: #000000; " +
                "-fx-text-fill: #00ff00;");

        VBox.setVgrow(textArea, Priority.ALWAYS);
        HBox toolbar = getToolbar();
        VBox root = new VBox(toolbar, textArea);
        Scene scene = new Scene(root, 700, 500);
        stage.setScene(scene);

        stage.setOnCloseRequest(event -> {
            stage.hide();
            event.consume();
        });
    }

    private HBox getToolbar() {
        Button saveButton = new Button("Save Logs to File...");
        saveButton.setStyle("-fx-background-color: #333; -fx-text-fill: white; -fx-border-color: #555; -fx-cursor: hand;");
        saveButton.setOnAction(e -> saveLogsToFile());

        Button clearButton = new Button("Clear");
        clearButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #888; -fx-cursor: hand;");
        clearButton.setOnAction(e -> textArea.clear());

        HBox toolbar = new HBox(10, saveButton, clearButton);
        toolbar.setPadding(new Insets(10));
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.setStyle("-fx-background-color: #2b2b2b; -fx-border-color: #444; -fx-border-width: 0 0 1 0;");
        return toolbar;
    }

    public static synchronized LogWindow getInstance() {
        if (instance == null) {
            instance = new LogWindow();
        }
        return instance;
    }

    public void show() {
        stage.show();
        stage.toFront();
    }

    public void appendLog(String text) {
        Platform.runLater(() -> {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            textArea.appendText("[" + timestamp + "] " + text + "\n");
        });
    }

    private void saveLogsToFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Log File");
        String defaultName = "ffmpeg_log_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm")) + ".txt";
        fileChooser.setInitialFileName(defaultName);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try {
                Files.writeString(file.toPath(), textArea.getText());
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Saved");
                alert.setHeaderText(null);
                alert.setContentText("Logs saved successfully to:\n" + file.getName());
                alert.showAndWait();
            } catch (IOException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Could not save log file:\n" + ex.getMessage());
                alert.showAndWait();
            }
        }
    }
}