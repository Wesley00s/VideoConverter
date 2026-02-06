package com.videoconverter;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        loadFonts("/fonts/JetBrainsMono-Regular.ttf");
        loadFonts("/fonts/JetBrainsMono-Bold.ttf");
        loadFonts("/fonts/JetBrainsMono-Italic.ttf");

        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        try {
            InputStream iconStream = getClass().getResourceAsStream("/images/app_logo.png");
            if (iconStream != null) {
                stage.getIcons().add(new Image(iconStream));
            } else {
                System.err.println("Image don't found in the path /images/app_logo.png");
            }
        } catch (Exception e) {
            System.err.println("Unable to load image: " + e.getMessage());
        }

        stage.setTitle("ProRes Converter");
        stage.setMinWidth(600);
        stage.setMinHeight(500);
        stage.setScene(scene);
        stage.show();
    }

    private void loadFonts(String path) {
        try {
            Font.loadFont(getClass().getResourceAsStream(path), 12);
        } catch (Exception e) {
            System.err.println("Error while loading font: " + path);
        }
    }
}

