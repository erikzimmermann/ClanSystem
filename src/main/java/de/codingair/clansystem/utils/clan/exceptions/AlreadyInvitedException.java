package de.codingair.clansystem.utils.clan.exceptions;

public class AlreadyInvitedException extends InviteException {
    public AlreadyInvitedException() {
    }

    public AlreadyInvitedException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlreadyInvitedException(Throwable cause) {
        super(cause);
    }
}
