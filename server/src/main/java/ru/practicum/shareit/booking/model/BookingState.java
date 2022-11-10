package ru.practicum.shareit.booking.model;

public enum BookingState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static BookingState from(String value) {
        for (BookingState state : BookingState.values()) {
            if (state.toString().equals(value)) {
                return state;
            }
        }
        return null;
    }
}
