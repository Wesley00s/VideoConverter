package com.videoconverter.model;

import com.videoconverter.util.FormatUtils;

import java.io.File;

public record VideoFile(File file) {

    public String getPath() {
        return file.getAbsolutePath();
    }

    public File getFile() {
        return file;
    }

    public long getSizeInBytes() {
        return file.length();
    }

    public String getFormattedSize() {
        return FormatUtils.formatBytes(getSizeInBytes());
    }

    @Override
    public String toString() {
        return file.getName() + " (" + getFormattedSize() + ")";
    }
}