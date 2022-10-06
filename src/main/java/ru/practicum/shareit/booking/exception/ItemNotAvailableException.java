package ru.practicum.shareit.booking.exception;

public class ItemNotAvailableException extends IllegalArgumentException {
    public ItemNotAvailableException(String message) {
        super(message);
    }
}
