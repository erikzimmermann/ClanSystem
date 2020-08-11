package de.codingair.clansystem.utils.clan.exceptions;

public class ClanNameNotAvailableException extends ClanException {
    public ClanNameNotAvailableException() {
    }

    public ClanNameNotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClanNameNotAvailableException(Throwable cause) {
        super(cause);
    }
}
