package com.apocalypse.core;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Manages concurrent logging of simulation events to a file.
 */
public class LoggerManager {
    private BufferedWriter fileWriter;
    private final ReentrantLock lock;
    private final DateTimeFormatter formatter;

    /**
     * Initializes the LoggerManager with the specified log file.
     * @param fileName The path and name of the file to write logs to.
     */
    public LoggerManager(String fileName) {
        lock = new ReentrantLock();
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        try {
            fileWriter = new BufferedWriter(new FileWriter(fileName, true));
        } catch (IOException e) {
            System.err.println("Error opening log file: " + e.getMessage());
        }
    }

    /**
     * Writes an event message to the log file in a thread-safe manner.
     * @param message The event description to log.
     */
    public void logEvent(String message) {
        lock.lock();
        try {
            if (fileWriter != null) {
                String timestamp = LocalDateTime.now().format(formatter);
                fileWriter.write(timestamp + " - " + message);
                fileWriter.newLine();
                fileWriter.flush();
            }
        } catch (IOException e) {
            System.err.println("Error writing to log file: " + e.getMessage());
        } finally {
            lock.unlock();
        }
    }

    /**
     * Closes the active file writer to prevent resource leaks.
     */
    public void close() {
        try {
            if (fileWriter != null) {
                fileWriter.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing the log file: " + e.getMessage());
        }
    }
}
