package de.codingair.clansystem.utils.clan.exceptions;

public class HighestRankException extends PermissionException {
    public HighestRankException() {
    }

    public HighestRankException(String message, Throwable cause) {
        super(message, cause);
    }

    public HighestRankException(Throwable cause) {
        super(cause);
    }
}
