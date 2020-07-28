package de.codingair.clansystem.utils.clan.exceptions;

public class SameRankException extends PermissionException {
    public SameRankException() {
    }

    public SameRankException(String message, Throwable cause) {
        super(message, cause);
    }

    public SameRankException(Throwable cause) {
        super(cause);
    }
}
