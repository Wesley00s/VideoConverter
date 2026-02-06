package com.videoconverter;

import com.videoconverter.model.VideoFile;
import com.videoconverter.model.VideoListCell;
import com.videoconverter.service.ConversionTask;
import com.videoconverter.util.FormatUtils;
import com.videoconverter.view.LogWindow;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainController {

    @FXML
    private ListView<VideoFile> fileListView;
    @FXML
    private Label statusLabel;
    @FXML
    private Label totalSizeLabel;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Button convertButton;
    @FXML
    private Button clearButton;

    private boolean isConversionFinished = false;

    private static final List<String> ALLOWED_EXTENSIONS = List.of(".mov", ".mp4", ".mkv", ".avi", ".m4v");

    @FXML
    protected void onShowLogsClick() {
        LogWindow.getInstance().show();
    }

    public void initialize() {
        fileListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        fileListView.setCellFactory(listView -> new VideoListCell());

        setupDragAndDrop();
        setupKeyBindings();

        resetConversionState();

        LogWindow.getInstance();
    }

    private void resetConversionState() {
        statusLabel.textProperty().unbind();
        progressBar.progressProperty().unbind();

        isConversionFinished = false;
        fileListView.getItems().clear();

        progressBar.setVisible(false);
        progressBar.setProgress(0);

        convertButton.setText("Start Conversion");
        convertButton.getStyleClass().removeAll("success-button");
        updateUI();
    }

    private void setupKeyBindings() {
        fileListView.setOnKeyPressed((KeyEvent event) -> {
            if (event.getCode() == KeyCode.DELETE || event.getCode() == KeyCode.BACK_SPACE) {
                removeSelectedFiles();
            }
        });
    }

    @FXML
    protected void onRemoveSelectedClick() {
        removeSelectedFiles();
    }

    @FXML
    protected void onClearListClick() {
        resetConversionState();
    }

    private void setupDragAndDrop() {
        fileListView.setOnDragOver((DragEvent event) -> {
            if (event.getDragboard().hasFiles()) {
                boolean hasVideo = event.getDragboard().getFiles().stream().anyMatch(this::isValidVideo);
                if (hasVideo) {
                    event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                    if (!fileListView.getStyleClass().contains("drag-over")) {
                        fileListView.getStyleClass().add("drag-over");
                    }
                }
            }
            event.consume();
        });

        fileListView.setOnDragExited((DragEvent event) -> {
            fileListView.getStyleClass().remove("drag-over");
            event.consume();
        });

        fileListView.setOnDragDropped((DragEvent event) -> {
            boolean success = false;
            if (event.getDragboard().hasFiles()) {
                if (isConversionFinished) {
                    resetConversionState();
                }

                addFilesToList(event.getDragboard().getFiles());
                fileListView.getStyleClass().remove("drag-over");
                success = true;
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }

    @FXML
    protected void onSelectFilesClick(ActionEvent event) {
        if (isConversionFinished) {
            resetConversionState();
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Add ProRes Videos");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Video", "*.mov", "*.mp4", "*.mkv", "*.avi"),
                new FileChooser.ExtensionFilter("All", "*.*")
        );

        Window stage = ((Node) event.getSource()).getScene().getWindow();
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(stage);

        if (selectedFiles != null) {
            addFilesToList(selectedFiles);
        }
    }

    private void addFilesToList(List<File> files) {
        int ignoredCount = 0;

        for (File file : files) {
            if (isValidVideo(file)) {
                boolean exists = fileListView.getItems().stream()
                        .anyMatch(v -> v.getPath().equals(file.getAbsolutePath()));
                if (!exists) {
                    fileListView.getItems().add(new VideoFile(file));
                }
            } else {
                ignoredCount++;
            }
        }

        updateUI();
    }

    private void removeSelectedFiles() {
        List<VideoFile> selectedItems = fileListView.getSelectionModel().getSelectedItems();
        if (selectedItems.isEmpty()) return;

        List<VideoFile> itemsToRemove = new ArrayList<>(selectedItems);
        fileListView.getItems().removeAll(itemsToRemove);
        fileListView.getSelectionModel().clearSelection();

        updateUI();
    }

    private void updateUI() {
        int count = fileListView.getItems().size();

        long totalBytes = fileListView.getItems().stream()
                .mapToLong(VideoFile::getSizeInBytes)
                .sum();

        totalSizeLabel.setText("Total Size: " + FormatUtils.formatBytes(totalBytes));

        if (count == 0) {
            statusLabel.setText("Waiting for files...");
        } else {
            statusLabel.setText(count + " file(s) in queue.");
        }
    }

    private boolean isValidVideo(File file) {
        String name = file.getName().toLowerCase(Locale.ROOT);
        return ALLOWED_EXTENSIONS.stream().anyMatch(name::endsWith);
    }

    @FXML
    protected void onConvertClick() {
        if (isConversionFinished) {
            resetConversionState();
            return;
        }

        if (fileListView.getItems().isEmpty()) {
            showAlert("No files", "Please add files to convert.");
            return;
        }

        setUiLocked(true);
        progressBar.setVisible(true);
        progressBar.setProgress(0);

        ConversionTask task = new ConversionTask(fileListView.getItems());

        statusLabel.textProperty().bind(task.messageProperty());
        progressBar.progressProperty().bind(task.progressProperty());

        task.setOnSucceeded(e -> {
            setUiLocked(false);
            statusLabel.textProperty().unbind();
            statusLabel.setText("Conversion Completed!");
            convertButton.setText("New Conversion");
            isConversionFinished = true;
            fileListView.getSelectionModel().clearSelection();

            showAlert("Success", "All videos converted successfully!");
        });

        task.setOnFailed(e -> {
            setUiLocked(false);
            statusLabel.textProperty().unbind();
            statusLabel.setText("Error occurred.");
            Throwable error = task.getException();
            showAlert("Error", "Conversion failed: " + error.getMessage());
            isConversionFinished = false;
        });

        new Thread(task).start();
    }

    private void setUiLocked(boolean locked) {
        fileListView.setDisable(locked);
        convertButton.setDisable(locked);
        clearButton.setDisable(locked);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}