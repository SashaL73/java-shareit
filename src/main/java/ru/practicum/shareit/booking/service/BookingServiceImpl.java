package ru.practicum.shareit.booking.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.UserStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;


    @Transactional
    @Override
    public BookingDto createBooking(Long userId, NewBookingRequest request) {
        if (!request.getEnd().isAfter(request.getStart())) {
            throw new ValidationException("Дата окончания должна быть позже даты начала");
        }

        User booker = findUserOrThrow(userId);
        Item item = findItemOrThrow(request.getItemId());
        if (!item.getAvailable()) {
            throw new ValidationException("Вещь сейчас недоступна для бронирования");
        }

        if (booker.getId().equals(item.getOwner().getId())) {
            throw new NotFoundException("Нельзя бронировать свою вещь");
        }

        Booking booking = bookingRepository.save(BookingMapper.mapToBooking(request, booker, item));
        return BookingMapper.mapToBookingDto(booking);
    }

    @Transactional
    @Override
    public BookingDto approvedBooking(Long ownerId, Long bookingId, Boolean approved) {
        if (approved == null) {
            throw new ValidationException("approved должен быть указан");
        }
        Booking booking = findBookingOrThrow(bookingId);

        if (!ownerId.equals(booking.getItem().getOwner().getId())) {
            throw new ValidationException("Пользователь не является владельцем вещи");
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        booking = bookingRepository.save(booking);
        return BookingMapper.mapToBookingDto(booking);
    }

    @Override
    public BookingDto findBooking(Long bookingId, Long userId) {
        findUserOrThrow(userId);
        Booking booking = findBookingOrThrow(bookingId);

        Long bookerId = booking.getBooker().getId();
        Long ownerId = booking.getItem().getOwner().getId();

        if (!userId.equals(bookerId) && !userId.equals(ownerId)) {
            throw new NotFoundException("Просмотр бронирования недоступен");
        }
        return BookingMapper.mapToBookingDto(booking);
    }

    @Override
    public List<BookingDto> findAllBookingByUserOrItemOwner(Long userId, BookingState state, UserStatus status) {
        findUserOrThrow(userId);

        List<Booking> bookings;
        LocalDateTime now = LocalDateTime.now();

        switch (state) {
            case ALL -> {
                if (status == UserStatus.BOOKER) {
                    bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
                    break;
                } else {
                    bookings = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId);
                    break;
                }
            }

            case CURRENT -> {
                if (status == UserStatus.BOOKER) {
                    bookings = bookingRepository
                            .findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now);
                    break;
                } else {
                    bookings = bookingRepository
                            .findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now);
                    break;
                }
            }

            case PAST -> {
                if (status == UserStatus.BOOKER) {
                    bookings = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, now);
                    break;
                } else {
                    bookings = bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, now);
                    break;
                }
            }

            case FUTURE -> {
                if (status == UserStatus.BOOKER) {
                    bookings = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, now);
                    break;
                } else {
                    bookings = bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(userId, now);
                    break;
                }
            }

            case WAITING -> {
                if (status == UserStatus.BOOKER) {
                    bookings = bookingRepository
                            .findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
                    break;
                } else {
                    bookings = bookingRepository
                            .findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
                    break;
                }
            }

            case REJECTED -> {
                if (status == UserStatus.BOOKER) {
                    bookings = bookingRepository
                            .findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
                    break;
                } else {
                    bookings = bookingRepository
                            .findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
                }
            }

            default -> {
                return new ArrayList<>();
            }

        }
        return bookings.stream()
                .map(BookingMapper::mapToBookingDto)
                .toList();
    }


    private User findUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Пользователь не найден id={}", id);
                    return new NotFoundException("Пользователь с id " + id + " не найден");
                });
    }

    private Item findItemOrThrow(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Вещь не найден id={}", id);
                    return new NotFoundException("Вещь с id " + id + " не найден");
                });
    }

    private Booking findBookingOrThrow(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Не найдено бронирование с id={}", id);
                    return new NotFoundException("Бронирование с id " + id + " не найдено");
                });
    }
}
