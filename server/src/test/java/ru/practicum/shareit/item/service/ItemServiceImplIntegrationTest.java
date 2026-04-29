package ru.practicum.shareit.item.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ItemServiceImplIntegrationTest {

    private final ItemService itemService;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;


    @Test
    void getUserItemsShouldReturnItemsForUser() {
        User user = new User();
        user.setName("Name");
        user.setEmail("test@test.com");
        user = userRepository.save(user);

        Item item1 = new Item();
        item1.setName("Test1");
        item1.setDescription("Description");
        item1.setAvailable(true);
        item1.setOwner(user);
        itemRepository.save(item1);

        Item item2 = new Item();
        item2.setName("Test2");
        item2.setDescription("Description");
        item2.setAvailable(true);
        item2.setOwner(user);
        itemRepository.save(item2);

        List<ItemOwnerDto> items = itemService.findAllUserItems(user.getId());

        assertEquals(2, items.size());
        assertTrue(items.stream().anyMatch(i -> i.getName().equals("Test1")));
        assertTrue(items.stream().anyMatch(i -> i.getName().equals("Test2")));
    }

    @Test
    void searchItemShouldReturnEmptyList() {
        List<ItemDto> list1 = itemService.searchItem(null);
        assertTrue(list1.isEmpty());

        List<ItemDto> list2 = itemService.searchItem("");
        assertTrue(list2.isEmpty());
    }

    @Test
    void searchItemShouldReturnItemsMatchingText() {
        User owner = new User();
        owner.setName("Name");
        owner.setEmail("test@test.com");
        owner = userRepository.save(owner);
        Long userId = owner.getId();

        Item item = new Item();
        item.setOwner(owner);
        item.setAvailable(true);
        item.setDescription("Description");
        item.setName("Test");
        item = itemRepository.save(item);
        Long itemId = item.getId();

        List<ItemDto> results = itemService.searchItem("Description");
        assertEquals(1, results.size());
        assertEquals("Test", results.get(0).getName());
    }

    @Test
    void findItemByIdShouldReturnItemsOwner() {
        User owner = new User();
        owner.setName("Name");
        owner.setEmail("test@test.com");
        owner = userRepository.save(owner);

        User user1 = new User();
        user1.setName("Name2");
        user1.setEmail("test1@test.com");
        user1 = userRepository.save(user1);

        Item item1 = new Item();
        item1.setName("Test1");
        item1.setDescription("Description");
        item1.setAvailable(true);
        item1.setOwner(owner);
        itemRepository.save(item1);

        ItemOwnerDto itemOwnerDto = itemService.findItemById(item1.getId(), owner.getId());
        assertEquals("Test1", itemOwnerDto.getName());
        ItemOwnerDto itemOwnerDto1 = itemService.findItemById(item1.getId(), user1.getId());
        assertEquals("Test1", itemOwnerDto1.getName());


    }

    @Test
    void saveItemShouldReturnException() {
        User user = new User();
        user.setName("Name");
        user.setEmail("test@test.com");
        user = userRepository.save(user);

        NewItemRequest itemRequest = new NewItemRequest();
        itemRequest.setName("Item");
        itemRequest.setDescription("Description");
        itemRequest.setAvailable(true);

        assertThrows(ValidationException.class, () -> itemService.saveItem(null, itemRequest));
        assertThrows(NotFoundException.class, () -> itemService.saveItem(999L, itemRequest));
    }

    @Test
    void saveItemShouldReturnItemDto() {
        User user = new User();
        user.setName("Name");
        user.setEmail("test@test.com");
        user = userRepository.save(user);

        NewItemRequest itemRequest = new NewItemRequest();
        itemRequest.setName("Item");
        itemRequest.setDescription("Description");
        itemRequest.setAvailable(true);

        ItemDto itemDto = itemService.saveItem(user.getId(), itemRequest);
        assertEquals("Item", itemDto.getName());
    }

    @Test
    void updateItemShouldReturnException() {
        User user = new User();
        user.setName("Name");
        user.setEmail("test@test.com");
        user = userRepository.save(user);
        Long userId = user.getId();

        User user1 = new User();
        user1.setName("Name1");
        user1.setEmail("test1@test.com");
        user1 = userRepository.save(user1);
        Long userId1 = user1.getId();

        Item item = new Item();
        item.setOwner(user);
        item.setAvailable(true);
        item.setDescription("Description");
        item.setName("Test");
        item = itemRepository.save(item);
        Long itemId = item.getId();

        UpdateItemRequest updateItemRequest = new UpdateItemRequest();
        updateItemRequest.setName("Test1");
        updateItemRequest.setDescription("Update");

        ItemDto itemDto = itemService.updateItem(userId, itemId, updateItemRequest);

        assertEquals("Test1", itemDto.getName());
        assertEquals("Update", itemDto.getDescription());

        assertThrows(NotFoundException.class, () -> itemService.updateItem(userId, itemId + 1, updateItemRequest));
        assertThrows(NotFoundException.class, () -> itemService.updateItem(999L, itemId, updateItemRequest));
        assertThrows(ValidationException.class, () -> itemService.updateItem(userId1, itemId, updateItemRequest));

    }

    @Test
    void saveCommentShouldReturnException() {
        User owner = new User();
        owner.setName("Name");
        owner.setEmail("test@test.com");
        owner = userRepository.save(owner);
        Long userId = owner.getId();

        Item item = new Item();
        item.setOwner(owner);
        item.setAvailable(true);
        item.setDescription("Description");
        item.setName("Test");
        item = itemRepository.save(item);
        Long itemId = item.getId();

        User commenter = new User();
        commenter.setName("Name1");
        commenter.setEmail("test1@test.com");
        commenter = userRepository.save(commenter);
        Long commenterId = commenter.getId();

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(commenter);
        booking.setStart(LocalDateTime.now().plusHours(1));
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setStatus(BookingStatus.WAITING);
        bookingRepository.save(booking);

        Comment comment = new Comment();
        comment.setItem(item);
        comment.setAuthor(commenter);
        comment.setText("Text");
        comment = commentRepository.save(comment);

        NewCommentRequest newCommentRequest = new NewCommentRequest();
        newCommentRequest.setText("Text");
        assertThrows(ValidationException.class, () -> itemService.saveComment(commenterId, itemId, newCommentRequest));

    }

    @Test
    void updateItemFieldsShouldNotChangeItemWhenNoFieldsSet() {
        Item item = new Item();
        item.setName("Old");
        item.setDescription("Old Desc");
        item.setAvailable(true);

        UpdateItemRequest request = new UpdateItemRequest(); // все поля null
        ItemMapper.updateItemFields(item, request);

        assertEquals("Old", item.getName());
        assertEquals("Old Desc", item.getDescription());
        assertTrue(item.getAvailable());
    }

    @Test
    void mapToItemOwnerDtoShouldHandleNullBookings() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Item");
        item.setDescription("Desc");
        item.setAvailable(true);

        ItemOwnerDto dto = ItemMapper.mapToItemOwnerDto(item, null, null, List.of());
        assertNotNull(dto);
        assertNull(dto.getLastBooking());
        assertNull(dto.getNextBooking());
    }

    @Test
    void saveCommentShouldCreateCommentForApprovedPastBooking() {
        User owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@test.com");
        owner = userRepository.save(owner);

        Item item = new Item();
        item.setOwner(owner);
        item.setAvailable(true);
        item.setName("Test");
        item.setDescription("Test");
        itemRepository.save(item);

        User booker = new User();
        booker.setName("Booker");
        booker.setEmail("booker@test.com");
        booker = userRepository.save(booker);

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStart(LocalDateTime.now().minusDays(2));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);

        NewCommentRequest request = new NewCommentRequest();
        request.setText("text");

        CommentDto commentDto = itemService.saveComment(booker.getId(), item.getId(), request);
        assertEquals("text", commentDto.getText());
    }

    @Test
    void saveCommentShouldFailForWaitingOrFutureBooking() {
        User owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@test.com");
        owner = userRepository.save(owner);

        Item item = new Item();
        item.setOwner(owner);
        item.setAvailable(true);
        item.setName("Test");
        item.setDescription("Test");
        itemRepository.save(item);

        User booker = new User();
        booker.setName("Booker");
        booker.setEmail("booker@test.com");
        booker = userRepository.save(booker);
        Long bookerId = booker.getId();

        Booking futureBooking = new Booking();
        futureBooking.setItem(item);
        futureBooking.setBooker(booker);
        futureBooking.setStart(LocalDateTime.now().plusDays(1));
        futureBooking.setEnd(LocalDateTime.now().plusDays(2));
        futureBooking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(futureBooking);

        NewCommentRequest request = new NewCommentRequest();
        request.setText("Text");
        assertThrows(ValidationException.class,
                () -> itemService.saveComment(bookerId, item.getId(), request));

        Booking waitingBooking = new Booking();
        waitingBooking.setItem(item);
        waitingBooking.setBooker(booker);
        waitingBooking.setStart(LocalDateTime.now().minusDays(1));
        waitingBooking.setEnd(LocalDateTime.now().plusDays(1));
        waitingBooking.setStatus(BookingStatus.WAITING);
        bookingRepository.save(waitingBooking);

        assertThrows(ValidationException.class, () -> itemService.saveComment(bookerId, item.getId(), request));
    }


    @Test
    void updateItemFieldsShouldCoverAllFieldCombinations() {
        Item item = new Item();
        item.setName("Old");
        item.setDescription("Old Desc");
        item.setAvailable(false);

        UpdateItemRequest req1 = new UpdateItemRequest();
        req1.setName("New Name");
        ItemMapper.updateItemFields(item, req1);
        assertEquals("New Name", item.getName());

        UpdateItemRequest req2 = new UpdateItemRequest();
        req2.setDescription("New Desc");
        req2.setAvailable(true);
        ItemMapper.updateItemFields(item, req2);
        assertEquals("New Desc", item.getDescription());
        assertTrue(item.getAvailable());
    }

}

