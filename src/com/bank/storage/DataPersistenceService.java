package com.bank.storage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Service responsible for saving and loading bank account data to/from disk.
 * Uses Java Object Serialization for reliable, robust persistence.
 */
public class DataPersistenceService {
    private final String storageFilePath;

    public DataPersistenceService(String storageFilePath) {
        this.storageFilePath = storageFilePath;
    }

    public synchronized void saveData(BankData data) {
        try {
            Path path = Paths.get(storageFilePath).toAbsolutePath().normalize();
            if (path.getParent() != null && !Files.exists(path.getParent())) {
                Files.createDirectories(path.getParent());
            }

            File file = path.toFile();
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(data);
            }
        } catch (Exception e) {
            // Silently ignore or print debug notice without breaking execution
        }
    }

    public synchronized BankData loadData() {
        try {
            Path path = Paths.get(storageFilePath).toAbsolutePath().normalize();
            File file = path.toFile();
            if (!file.exists()) {
                return null; // Signals caller to initialize seed data
            }

            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                Object obj = ois.readObject();
                if (obj instanceof BankData) {
                    return (BankData) obj;
                }
            }
        } catch (Exception e) {
            // Silently fall back to seed data
        }
        return null;
    }

    public String getStorageFilePath() {
        return storageFilePath;
    }
}
