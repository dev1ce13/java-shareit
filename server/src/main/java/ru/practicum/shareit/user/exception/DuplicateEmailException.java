package ru.practicum.shareit.user.exception;

public class DuplicateEmailException extends IllegalArgumentException {
    public DuplicateEmailException(String message) {
        super(message);
    }
}
