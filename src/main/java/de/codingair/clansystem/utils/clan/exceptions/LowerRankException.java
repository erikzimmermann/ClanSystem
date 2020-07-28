package de.codingair.clansystem.utils.clan.exceptions;

public class LowerRankException extends PermissionException {
    public LowerRankException() {
    }

    public LowerRankException(String message, Throwable cause) {
        super(message, cause);
    }

    public LowerRankException(Throwable cause) {
        super(cause);
    }
}
