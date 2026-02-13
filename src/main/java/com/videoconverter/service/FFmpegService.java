package com.videoconverter.service;

import com.videoconverter.view.LogWindow;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class FFmpegService {

    private static final String CODEC = "hevc_nvenc";
    private static final String CRF = "16";
    private static final String PRESET = "medium";

    public boolean convertVideo(File inputFile) throws IOException, InterruptedException {
        String outputName = inputFile.getName().substring(0, inputFile.getName().lastIndexOf('.')) + "_HEVC.mp4";
        File outputFile = new File(inputFile.getParent(), outputName);
        Process process = startFFmpegProcess(inputFile, outputFile);
        logProcessOutput(process);
        int exitCode = process.waitFor();
        return exitCode == 0;
    }

    public void moveOriginalFile(File inputFile) throws IOException {
        File processedDir = new File(inputFile.getParent(), "Processed");
        Files.createDirectories(processedDir.toPath());
        Path source = inputFile.toPath();
        Path target = new File(processedDir, inputFile.getName()).toPath();

        Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
    }

    private Process startFFmpegProcess(File inputFile, File outputFile) throws IOException {
        ProcessBuilder builder = new ProcessBuilder(
                "ffmpeg",
                "-y",
                "-i", inputFile.getAbsolutePath(),
                "-c:v", CODEC,
                "-crf", CRF,
                "-preset", PRESET,
                "-g", "120",
                "-keyint_min", "60",
                "-x265-params", "no-open-gop=1",
                "-pix_fmt", "yuv420p10le",
                "-color_primaries", "1",
                "-color_trc", "1",
                "-colorspace", "1",
                "-c:a", "aac",
                "-b:a", "320k",
                "-tag:v", "hvc1",
                outputFile.getAbsolutePath()
        );
        builder.redirectErrorStream(true);
        return builder.start();
    }
    private void logProcessOutput(Process process) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);

                LogWindow.getInstance().appendLog(line);
            }
        }
    }
}