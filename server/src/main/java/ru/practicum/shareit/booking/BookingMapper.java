package ru.practicum.shareit.booking;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

@UtilityClass
public class BookingMapper {

    public Booking toBooking(BookingRequestDTO dto, Item item, User booker) {
        return Booking.builder()
                .item(item)
                .booker(booker)
                .start(dto.getStart())
                .end(dto.getEnd())
                .status(BookingStatus.WAITING)
                .build();
    }

    public BookingResponseDTO toBookingResponseDTO(Booking booking) {
        Item item = booking.getItem();
        User booker = booking.getBooker();

        BookingResponseDTO.ItemDTO itemDTO = new BookingResponseDTO.ItemDTO(item.getId(), item.getName());
        BookingResponseDTO.BookerDTO bookerDTO = new BookingResponseDTO.BookerDTO(booker.getId());

        return new BookingResponseDTO(
                booking.getId(),
                itemDTO,
                bookerDTO,
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus()
        );
    }
}


