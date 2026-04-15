package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto createNewBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                       @Valid @RequestBody NewBookingRequest request) {
        return bookingService.createBooking(userId, request);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approved(@RequestHeader("X-Sharer-User-Id") long userId,
                               @PathVariable Long bookingId,
                               @RequestParam Boolean approved) {
        return bookingService.approvedBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @PathVariable Long bookingId) {
        return bookingService.findBooking(bookingId, userId);
    }

    @GetMapping()
    public List<BookingDto> getAllBookingsByUser(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @RequestParam(defaultValue = "ALL") BookingState state) {
        return bookingService.findAllBookingByUserOrItemOwner(userId, state, UserStatus.BOOKER);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingsByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                  @RequestParam(defaultValue = "ALL") BookingState state) {
        return bookingService.findAllBookingByUserOrItemOwner(userId, state, UserStatus.OWNER);
    }
}
