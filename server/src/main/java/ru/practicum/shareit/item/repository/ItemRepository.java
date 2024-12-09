package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.entity.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerId(Long ownerId);

    @Query("SELECT i FROM Item i WHERE (UPPER(i.name) LIKE UPPER(CONCAT('%', ?1, '%')) OR UPPER(i.description) " +
            "LIKE UPPER(CONCAT('%', ?1, '%'))) AND i.available = true")
    List<Item> search(String text);

    @Query("SELECT i FROM Item i WHERE i.request.id = :requestId")
    List<Item> findByRequestId(@Param("requestId") Long requestId);

    List<Item> findByRequestIdIn(List<Long> ids);
}