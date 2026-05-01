package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createRequest(Long userId, NewItemRequestDto requestDto);

    List<ItemRequestDto> getAllRequests(Long userId);

    ItemRequestDto getRequestById(Long requestId);

    List<ItemRequestDto> getAllUserRequests(Long userId);
}
