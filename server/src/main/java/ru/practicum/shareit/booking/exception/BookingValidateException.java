package ru.practicum.shareit.booking.exception;

public class BookingValidateException extends IllegalArgumentException {
    public BookingValidateException(String message) {
        super(message);
    }
}
