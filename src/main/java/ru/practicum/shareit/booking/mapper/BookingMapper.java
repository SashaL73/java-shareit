package ru.practicum.shareit.booking.mapper;


import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.item.dto.ItemDtoForBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDtoForBooking;

import static ru.practicum.shareit.booking.BookingStatus.WAITING;

public class BookingMapper {
    public static Booking mapToBooking(NewBookingRequest request, User booker, Item item) {
        Booking booking = new Booking();
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStart(request.getStart());
        booking.setEnd(request.getEnd());
        booking.setStatus(WAITING);
        return booking;
    }

    public static BookingDto mapToBookingDto(Booking booking) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setStatus(booking.getStatus());

        UserDtoForBooking userDto = new UserDtoForBooking();
        userDto.setId(booking.getBooker().getId());
        userDto.setName(booking.getBooker().getName());
        bookingDto.setBooker(userDto);

        ItemDtoForBooking itemDto = new ItemDtoForBooking();
        itemDto.setId(booking.getItem().getId());
        itemDto.setName(booking.getItem().getName());
        bookingDto.setItem(itemDto);
        return bookingDto;
    }
}
