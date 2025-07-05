package ru.practicum.shareit.booking;

public enum BookingState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static BookingState from(String value) {
        try {
            return BookingState.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Неизвестный параметр: " + value);
        }
    }

    public BookingStatus toStatus() {
        switch (this) {
            case WAITING:
                return BookingStatus.WAITING;
            case REJECTED:
                return BookingStatus.REJECTED;
            default:
                return null;
        }
    }
}
