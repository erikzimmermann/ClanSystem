package de.codingair.clansystem.utils.clan.exceptions;

public class LowestRankException extends PermissionException {
    public LowestRankException() {
    }

    public LowestRankException(String message, Throwable cause) {
        super(message, cause);
    }

    public LowestRankException(Throwable cause) {
        super(cause);
    }
}
