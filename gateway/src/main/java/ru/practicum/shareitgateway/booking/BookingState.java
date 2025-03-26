package ru.practicum.shareitgateway.booking;

public enum BookingState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static BookingState from(String state) {
        if (state == null) {
            return ALL;
        }

        try {
            return BookingState.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ALL; // Вернём ALL, если не смогли найти такое состояние
        }
    }

}
