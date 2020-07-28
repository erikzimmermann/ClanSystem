package de.codingair.clansystem.utils.clan.exceptions;

public class ClanException extends Exception {
    public ClanException() {
    }

    public ClanException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClanException(Throwable cause) {
        super(cause);
    }
}
