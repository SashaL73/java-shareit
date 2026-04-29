package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ItemMapperTest {
    @Test
    void mapToItemShouldMapFields() {
        User owner = new User();
        owner.setId(1L);

        NewItemRequest request = new NewItemRequest();
        request.setName("Test");
        request.setDescription("Test");
        request.setAvailable(true);
        request.setRequestId(100L);

        Item item = ItemMapper.mapToItem(request, owner);

        assertEquals("Test", item.getName());
        assertEquals("Test", item.getDescription());
        assertTrue(item.getAvailable());
        assertEquals(owner, item.getOwner());
        assertNotNull(item.getRequest());
        assertEquals(100L, item.getRequest().getId());
    }

    @Test
    void mapToItemDtoShouldMapFields() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Test");
        item.setDescription("Test");
        item.setAvailable(true);

        ItemDto dto = ItemMapper.mapToItemDto(item);

        assertEquals(item.getId(), dto.getId());
        assertEquals(item.getName(), dto.getName());
        assertEquals(item.getDescription(), dto.getDescription());
        assertEquals(item.getAvailable(), dto.getAvailable());
    }

    @Test
    void mapToItemOwnerDtoShouldMapWithBookingsAndComments() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Test");
        item.setDescription("Test");
        item.setAvailable(true);

        User booker = new User();
        booker.setId(2L);

        Booking last = new Booking();
        last.setId(10L);
        last.setBooker(booker);
        last.setStart(LocalDateTime.now().minusDays(2));
        last.setEnd(LocalDateTime.now().minusDays(1));

        Booking next = new Booking();
        next.setId(20L);
        next.setBooker(booker);
        next.setStart(LocalDateTime.now().plusDays(1));
        next.setEnd(LocalDateTime.now().plusDays(2));

        CommentDto comment = new CommentDto();
        comment.setId(100L);
        comment.setText("Good drill");

        ItemOwnerDto ownerDto = ItemMapper.mapToItemOwnerDto(item, last, next, List.of(comment));

        assertEquals(item.getId(), ownerDto.getId());
        assertNotNull(ownerDto.getLastBooking());
        assertNotNull(ownerDto.getNextBooking());
        assertEquals(1, ownerDto.getComments().size());
    }

    @Test
    void updateItemFields_shouldUpdateOnlyProvidedFields() {
        Item item = new Item();
        item.setName("Old name");
        item.setDescription("Old desc");
        item.setAvailable(false);

        UpdateItemRequest request = new UpdateItemRequest();
        request.setName("New name"); // только имя
        request.setAvailable(true);

        ItemMapper.updateItemFields(item, request);

        assertEquals("New name", item.getName());
        assertEquals("Old desc", item.getDescription()); // не изменилось
        assertTrue(item.getAvailable());
    }

}
