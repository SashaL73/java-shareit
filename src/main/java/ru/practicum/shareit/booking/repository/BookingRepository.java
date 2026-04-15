package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerIdOrderByStartDesc(Long userId);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
            Long userId, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(
            Long userId, LocalDateTime end);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(
            Long userId, LocalDateTime start);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(
            Long userId, BookingStatus status);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long ownerId);

    List<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
            Long ownerId, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(
            Long ownerId, LocalDateTime end);

    List<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(
            Long ownerId, LocalDateTime start);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(
            Long ownerId, BookingStatus status);

    Boolean existsByBookerIdAndItemIdAndStatusAndEndBefore(
            Long userId, Long itemId, BookingStatus status, LocalDateTime now);

    Optional<Booking> findFirstByItemIdAndStartBeforeOrderByStartDesc(Long itemId, LocalDateTime now);

    Optional<Booking> findFirstByItemIdAndStartAfterOrderByStartAsc(Long itemId, LocalDateTime now);


    @Query("select b from Booking b " +
            "where b.item.id in :itemIds " +
            "and b.start < :now " +
            "order by b.start desc"
    )
    List<Booking> findLastBookings(@Param("itemIds") List<Long> itemIds,
                                   @Param("now") LocalDateTime now);

    @Query("select b from Booking b " +
            "where b.item.id in :itemIds " +
            "and b.start > :now " +
            "order by b.start asc"
    )
    List<Booking> findNextBookings(@Param("itemIds") List<Long> itemIds,
                                   @Param("now") LocalDateTime now);

}
