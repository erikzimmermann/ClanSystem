package de.codingair.clansystem.utils;

public interface DataModule {
    void load();
    void destroy();
    void save(boolean autoSaver);
}
