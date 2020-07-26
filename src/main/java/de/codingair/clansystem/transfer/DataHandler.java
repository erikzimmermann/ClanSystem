package de.codingair.clansystem.transfer;

public interface DataHandler {
    String GET_CHANNEL = "clansystem:get";
    String REQUEST_CHANNEL = "clansystem:request";

    void onEnable();

    void onDisable();
}
