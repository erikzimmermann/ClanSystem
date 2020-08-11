package de.codingair.clansystem.utils.clan.exceptions;

public class AlreadyAMemberException extends ClanException {
    public AlreadyAMemberException() {
    }

    public AlreadyAMemberException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlreadyAMemberException(Throwable cause) {
        super(cause);
    }
}
