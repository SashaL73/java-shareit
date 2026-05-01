package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ItemRequestServiceImplIntegrationTest {

    private final ItemRequestService itemRequestService;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final RequestRepository requestRepository;

    @Test
    void getAllRequestsByUserWithItems() {
        User user = new User();
        user.setName("Name");
        user.setEmail("test@test.com");
        user = userRepository.save(user);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("RequestDescription");
        itemRequest.setRequestor(user);
        itemRequest = requestRepository.save(itemRequest);

        Item item1 = new Item();
        item1.setName("Test1");
        item1.setDescription("Description");
        item1.setAvailable(true);
        item1.setOwner(user);
        item1.setRequest(itemRequest);
        itemRepository.save(item1);

        Item item2 = new Item();
        item2.setName("Test2");
        item2.setDescription("Description");
        item2.setAvailable(true);
        item2.setOwner(user);
        item2.setRequest(itemRequest);
        itemRepository.save(item2);

        List<ItemRequestDto> requestDtos = itemRequestService.getAllUserRequests(user.getId());

        assertEquals(1, requestDtos.size());

        ItemRequestDto dto = requestDtos.get(0);
        assertEquals("RequestDescription", dto.getDescription());
        assertEquals(2, dto.getItems().size());
        assertTrue(dto.getItems().stream().anyMatch(i -> i.getName().equals("Test1")));
        assertTrue(dto.getItems().stream().anyMatch(i -> i.getName().equals("Test2")));
    }

    @Test
    void createRequestShouldReturnRequest() {
        User user = new User();
        user.setName("Name");
        user.setEmail("test@test.com");
        user = userRepository.save(user);

        NewItemRequestDto newItemRequestDto = NewItemRequestDto.builder()
                .description("Description")
                .build();
        ItemRequestDto itemRequestDto = itemRequestService.createRequest(user.getId(), newItemRequestDto);
        assertEquals("Description", itemRequestDto.getDescription());
        assertThrows(NotFoundException.class, () -> itemRequestService.createRequest(999L, newItemRequestDto));
    }

    @Test
    void getRequestByIdShouldReturnRequest() {
        User user = new User();
        user.setName("Name");
        user.setEmail("test@test.com");
        user = userRepository.save(user);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("RequestDescription");
        itemRequest.setRequestor(user);
        itemRequest = requestRepository.save(itemRequest);

        ItemRequestDto itemRequestDto = itemRequestService.getRequestById(itemRequest.getId());

        assertEquals("RequestDescription", itemRequestDto.getDescription());
    }

    @Test
    void getRequestByIdShouldThrowNotFoundForInvalidId() {
        assertThrows(NotFoundException.class, () -> itemRequestService.getRequestById(999L));
    }

    @Test
    void getAllRequestsShouldReturnEmptyListWhenNoRequests() {
        User user = new User();
        user.setName("test");
        user.setEmail("test@test.com");
        user = userRepository.save(user);

        List<ItemRequestDto> result = itemRequestService.getAllRequests(user.getId());
        assertTrue(result.isEmpty());
    }

    @Test
    void getRequestByIdShouldThrowNotFound() {
        assertThrows(NotFoundException.class, () -> itemRequestService.getRequestById(999L));
    }

    @Test
    void getAllUserRequestsShouldReturnEmptyListForUserWithoutRequests() {
        User user = new User();
        user.setName("test");
        user.setEmail("test@test.com");
        user = userRepository.save(user);
        List<ItemRequestDto> result = itemRequestService.getAllUserRequests(user.getId());
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllUserRequestsShouldReturnRequestsWithEmptyItems() {
        User user = new User();
        user.setName("test");
        user.setEmail("test@test.com");
        user = userRepository.save(user);

        ItemRequest req = new ItemRequest();
        req.setDescription("Description");
        req.setRequestor(user);
        requestRepository.save(req);

        List<ItemRequestDto> result = itemRequestService.getAllUserRequests(user.getId());
        assertEquals(1, result.size());
        assertTrue(result.get(0).getItems().isEmpty());
    }


    @Test
    void createRequestShouldThrowNotFoundIfUserDoesNotExist() {
        NewItemRequestDto dto = NewItemRequestDto.builder()
                .description("Description")
                .build();

        assertThrows(NotFoundException.class, () -> itemRequestService.createRequest(999L, dto));
    }

    @Test
    void getAllRequestsShouldReturnRequestsFromOtherUsersWithItems() {
        User user1 = new User();
        user1.setName("User1");
        user1.setEmail("u1@test.com");
        user1 = userRepository.save(user1);

        User user2 = new User();
        user2.setName("User2");
        user2.setEmail("u2@test.com");
        user2 = userRepository.save(user2);

        ItemRequest request = new ItemRequest();
        request.setDescription("OtherUserRequest");
        request.setRequestor(user1);
        requestRepository.save(request);

        Item item = new Item();
        item.setName("Item1");
        item.setDescription("ItemDesc");
        item.setAvailable(true);
        item.setOwner(user2);
        item.setRequest(request);
        itemRepository.save(item);

        List<ItemRequestDto> dtos = itemRequestService.getAllRequests(user2.getId());
        assertEquals(1, dtos.size());
        assertEquals("OtherUserRequest", dtos.get(0).getDescription());
        assertEquals(1, dtos.get(0).getItems().size());
        assertEquals("Item1", dtos.get(0).getItems().get(0).getName());
    }

    @Test
    void getRequestByIdShouldThrowNotFoundForNonexistentRequest() {
        assertThrows(NotFoundException.class,
                () -> itemRequestService.getRequestById(999L));
    }

    @Test
    void getAllUserRequestsShouldReturnRequestsWithAndWithoutItems() {
        User user = new User();
        user.setName("User");
        user.setEmail("user@test.com");
        user = userRepository.save(user);

        ItemRequest req1 = new ItemRequest();
        req1.setDescription("RequestWithoutItems");
        req1.setRequestor(user);
        requestRepository.save(req1);

        ItemRequest req2 = new ItemRequest();
        req2.setDescription("RequestWithItems");
        req2.setRequestor(user);
        req2 = requestRepository.save(req2);

        Item item = new Item();
        item.setName("ItemForRequest");
        item.setDescription("ItemDesc");
        item.setAvailable(true);
        item.setOwner(user);
        item.setRequest(req2);
        itemRepository.save(item);

        List<ItemRequestDto> dtos = itemRequestService.getAllUserRequests(user.getId());
        assertEquals(2, dtos.size());

        assertTrue(dtos.stream().anyMatch(d -> d.getItems().isEmpty()));

        assertTrue(dtos.stream().anyMatch(d -> !d.getItems().isEmpty()));
    }

}
