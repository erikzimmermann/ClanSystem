package de.codingair.clansystem.utils.clan.exceptions;

public class NotInvitedException extends InviteException {
    public NotInvitedException() {
    }

    public NotInvitedException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotInvitedException(Throwable cause) {
        super(cause);
    }
}
