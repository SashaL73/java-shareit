package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.UserStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;

import java.util.List;

public interface BookingService {

    BookingDto createBooking(Long userId, NewBookingRequest request);

    BookingDto approvedBooking(Long ownerId,Long bookingId, Boolean approved);

    BookingDto findBooking(Long bookingId, Long userId);

    List<BookingDto> findAllBookingByUserOrItemOwner(Long userId, BookingState state, UserStatus status);

}
