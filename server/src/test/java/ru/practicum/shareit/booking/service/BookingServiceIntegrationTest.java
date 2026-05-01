package ru.practicum.shareit.booking.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.UserStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class BookingServiceIntegrationTest {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingService bookingService;
    private final BookingRepository bookingRepository;

    private User createUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return userRepository.save(user);
    }

    private Item createItem(String name, User owner, boolean available) {
        Item item = new Item();
        item.setName(name);
        item.setDescription("Description");
        item.setAvailable(available);
        item.setOwner(owner);
        return itemRepository.save(item);
    }

    private Booking createBooking(User booker, Item item, BookingStatus status) {
        Booking booking = new Booking();
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setStatus(status);
        return bookingRepository.save(booking);
    }

    @Test
    void findAllBookingByUserOrItemOwnerShouldReturnBookings() {
        User user = createUser("Test", "test@test.com");
        Item item = createItem("TestItem", user, true);

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStart(LocalDateTime.now().plusHours(1));
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setStatus(BookingStatus.WAITING);
        bookingRepository.save(booking);

        List<BookingDto> bookingsBooker = bookingService.findAllBookingByUserOrItemOwner(
                user.getId(),
                BookingState.ALL,
                UserStatus.BOOKER
        );

        assertEquals(1, bookingsBooker.size());
        assertEquals("TestItem", bookingsBooker.get(0).getItem().getName());
        assertEquals(BookingStatus.WAITING, bookingsBooker.get(0).getStatus());

        List<BookingDto> bookingsOwner = bookingService.findAllBookingByUserOrItemOwner(
                user.getId(),
                BookingState.ALL,
                UserStatus.OWNER
        );

        assertEquals(1, bookingsOwner.size());
        assertEquals("TestItem", bookingsOwner.get(0).getItem().getName());
        assertEquals(BookingStatus.WAITING, bookingsOwner.get(0).getStatus());
    }

    @Test
    void createBookingShouldReturnBookingAndChekException() {
        User user = createUser("Test", "test@test.com");
        Item item = createItem("TestItem", user, true);
        Long ownerId = user.getId();

        User booker = new User();
        booker.setName("Test1");
        booker.setEmail("test1@test.com");
        booker = userRepository.save(booker);
        Long bookerId = booker.getId();

        NewBookingRequest request = NewBookingRequest.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        BookingDto bookingDto = bookingService.createBooking(bookerId, request);

        assertEquals("Test1", bookingDto.getBooker().getName());
        assertEquals("TestItem", bookingDto.getItem().getName());
        assertThrows(NotFoundException.class, () -> bookingService.createBooking(ownerId, request));
        assertThrows(NotFoundException.class, () -> bookingService.createBooking(999L, request));
        request.setItemId(request.getItemId() + 1);
        assertThrows(NotFoundException.class, () -> bookingService.createBooking(bookerId, request));
        request.setItemId(request.getItemId() - 1);
        request.setEnd(LocalDateTime.now().minusDays(2));
        assertThrows(ValidationException.class, () -> bookingService.createBooking(bookerId, request));

    }

    @Test
    void approvedBookingShouldReturnApprovedAndChackValidationException() {
        User user = createUser("Test", "test@test.com");
        Long userId = user.getId();

        User user1 = new User();
        user1.setName("Test1");
        user1.setEmail("test1@test.com");
        user1 = userRepository.save(user);
        Long userId1 = user.getId();

        Item item = createItem("TestItem", user, true);

        Booking booking = new Booking();
        booking.setBooker(user);
        booking.setStatus(BookingStatus.WAITING);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking = bookingRepository.save(booking);
        Long bookingId = booking.getId();

        Booking booking1 = new Booking();
        booking1.setBooker(user);
        booking1.setStatus(BookingStatus.APPROVED);
        booking1.setItem(item);
        booking1.setStart(LocalDateTime.now().plusDays(1));
        booking1.setEnd(LocalDateTime.now().plusDays(2));
        booking1 = bookingRepository.save(booking1);
        Long bookingId1 = booking1.getId();

        BookingDto bookingDto = bookingService.approvedBooking(user.getId(), booking.getId(), true);

        assertEquals(BookingStatus.APPROVED, bookingDto.getStatus());
        assertThrows(ValidationException.class, () -> bookingService.approvedBooking(userId, bookingId, null));
        assertThrows(ValidationException.class, () -> bookingService.approvedBooking(userId1, bookingId, true));
        assertThrows(ValidationException.class, () -> bookingService.approvedBooking(userId, bookingId1, true));

    }

    @Test
    void createBookingShouldThrowValidationExceptionItemNotAvailable() {
        User user = createUser("Test", "test@test.com");
        Item item = createItem("TestItem", user, false);

        User booker = new User();
        booker.setName("Test1");
        booker.setEmail("test1@test.com");
        booker = userRepository.save(booker);
        Long bookerId = booker.getId();

        NewBookingRequest request = NewBookingRequest.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        assertThrows(ValidationException.class, () -> bookingService.createBooking(bookerId, request));
    }

    @Test
    void findBookingShouldReturnBooking() {
        User user = createUser("Test", "test@test.com");
        Long ownerId = user.getId();
        Item item = createItem("TestItem", user, true);

        User booker = new User();
        booker.setName("Test1");
        booker.setEmail("test1@test.com");
        booker = userRepository.save(booker);
        Long bookerId = booker.getId();

        Booking booking = new Booking();
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setStatus(BookingStatus.WAITING);
        booking = bookingRepository.save(booking);

        BookingDto bookingDto = bookingService.findBooking(booking.getId(), bookerId);

        assertEquals("Test1", bookingDto.getBooker().getName());
        assertThrows(NotFoundException.class, () -> bookingService.findBooking(ownerId, bookerId));

    }

    @Test
    void approvedBookingShouldHandleRejectedStatus() {
        User owner = createUser("Test", "test@test.com");
        ;
        Long ownerId = owner.getId();

        User booker = new User();
        booker.setName("Test1");
        booker.setEmail("test1@test.com");
        booker = userRepository.save(booker);

        Item item = createItem("TestItem", owner, true);

        Booking booking = new Booking();
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setStatus(BookingStatus.REJECTED);
        booking = bookingRepository.save(booking);
        Long bookingId = booking.getId();

        assertThrows(ValidationException.class,
                () -> bookingService.approvedBooking(ownerId, bookingId, true));
    }

    @Test
    void findAllBookingByUserOrItemOwnerShouldHandleDifferentStates() {
        User user = createUser("User", "user@test.com");
        Item item = createItem("Drill", user, true);
        createBooking(user, item, BookingStatus.WAITING);
        createBooking(user, item, BookingStatus.APPROVED);

        for (BookingState state : BookingState.values()) {
            bookingService.findAllBookingByUserOrItemOwner(user.getId(), state, UserStatus.BOOKER);
        }
    }

    @Test
    void createBookingShouldFailForPastStartDate() {
        User owner = createUser("Owner", "owner@test.com");
        User booker = createUser("Booker", "booker@test.com");

        Item item = createItem("Drill", owner, true);

        NewBookingRequest request = NewBookingRequest.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().minusDays(1))
                .build();

        assertThrows(ValidationException.class, () ->
                bookingService.createBooking(booker.getId(), request));
    }

}
