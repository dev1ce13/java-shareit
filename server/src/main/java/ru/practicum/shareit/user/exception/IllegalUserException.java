package ru.practicum.shareit.user.exception;

public class IllegalUserException extends IllegalAccessError {

    public IllegalUserException(String message) {
        super(message);
    }
}
