package ru.practicum.shareit.booking.exeption;

public class BookingValidateException extends IllegalArgumentException {
    public BookingValidateException(String message) {
        super(message);
    }
}
