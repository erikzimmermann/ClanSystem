package de.codingair.clansystem.utils.clan.exceptions;

public class PermissionException extends ClanException {
    public PermissionException() {
    }

    public PermissionException(String message, Throwable cause) {
        super(message, cause);
    }

    public PermissionException(Throwable cause) {
        super(cause);
    }
}
