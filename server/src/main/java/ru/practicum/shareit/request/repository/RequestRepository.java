package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.request.entity.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    @Query("SELECT r FROM Request r WHERE r.requestor.id = ?1")
    List<Request> findAllByRequestorId(Long requestorId);

    @Query("SELECT i FROM Item i WHERE i.request.id IN :requestIds")
    List<Item> findByRequestIdIn(@Param("requestIds") List<Long> requestIds);
}