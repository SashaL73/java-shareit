package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOwnerId(Long userId);

    @Query("select i from Item i " +
            "where i.available = true " +
            "and ( lower(i.name) like lower(concat('%', ?1, '%')) " +
            " or lower(i.description) like lower(concat('%', ?1, '%')))")
    List<Item> search(String text);

    @Query("select i from Item i " +
            "where i.request.id in :requestIds")
    List<Item> findRequestItems(@Param("requestIds") List<Long> requestIds);
}
