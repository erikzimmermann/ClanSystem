package de.codingair.clansystem.utils.clan.exceptions;

public class NotAMemberException extends ClanException {
    public NotAMemberException() {
    }

    public NotAMemberException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotAMemberException(Throwable cause) {
        super(cause);
    }
}
