package ru.practicum.shareit.item.mapper;

import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.List;

@NoArgsConstructor
public class ItemMapper {
    public static Item mapToItem(NewItemRequest request, User owner) {
        Item item = new Item();
        item.setOwner(owner);
        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setAvailable(request.getAvailable());
        return item;
    }

    public static ItemDto mapToItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        return itemDto;
    }

    public static ItemOwnerDto mapToItemOwnerDto(Item item, Booking last, Booking next, List<CommentDto> commentDtoList) {
        ItemOwnerDto itemOwnerDto = new ItemOwnerDto();
        itemOwnerDto.setId(item.getId());
        itemOwnerDto.setName(item.getName());
        itemOwnerDto.setDescription(item.getDescription());
        itemOwnerDto.setAvailable(item.getAvailable());
        if (last != null) {
            itemOwnerDto.setLastBooking(new BookingShortDto(
                    last.getId(), last.getBooker().getId(), last.getStart(), last.getEnd()));
        }
        if (next != null) {
            itemOwnerDto.setNextBooking(new BookingShortDto(
                    next.getId(), next.getBooker().getId(), next.getStart(), next.getEnd()));
        }
        itemOwnerDto.setComments(commentDtoList);
        return itemOwnerDto;
    }

    public static Item updateItemFields(Item item, UpdateItemRequest request) {
        if (request.hasName()) {
            item.setName(request.getName());
        }

        if (request.hasDescription()) {
            item.setDescription(request.getDescription());
        }

        if (request.hasAvailable()) {
            item.setAvailable(request.getAvailable());
        }

        return item;
    }
}
