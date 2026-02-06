package com.videoconverter.service;

import com.videoconverter.model.VideoFile;
import javafx.concurrent.Task;
import java.util.List;

public class ConversionTask extends Task<Void> {

    private final List<VideoFile> filesToConvert;
    private final FFmpegService ffmpegService;

    public ConversionTask(List<VideoFile> files) {
        this.filesToConvert = files;
        this.ffmpegService = new FFmpegService();
    }

    @Override
    protected Void call() {
        int total = filesToConvert.size();
        int current = 0;

        for (VideoFile video : filesToConvert) {
            if (isCancelled()) break;

            current++;
            updateMessage("Converting (" + current + "/" + total + "): " + video.getFile().getName());
            updateProgress(current - 1, total);

            try {
                boolean success = ffmpegService.convertVideo(video.getFile());
                
                if (success) {
                    updateMessage("Moving original file...");
                    ffmpegService.moveOriginalFile(video.getFile());
                } else {
                    System.err.println("Error during conversion: " + video.getPath());
                }

            } catch (Exception e) {
                updateMessage("Error during conversion: " + e.getMessage());
            }
            
            updateProgress(current, total);
        }
        
        updateMessage("All tasks completed!");
        return null;
    }
}