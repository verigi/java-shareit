package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.enums.Status;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Collection<Booking> findAllByBookerId(Long bookerId);

    Collection<Booking> findAllByBookerIdAndStatus(Long bookerId, Status status);

    @Query("SELECT b FROM Booking AS b WHERE b.booker.id = ?1 AND CURRENT_TIMESTAMP BETWEEN b.start AND b.end")
    Collection<Booking> findAllCurrentBookingsByBookerId(Long bookerId);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1 AND CURRENT_TIMESTAMP > b.end")
    Collection<Booking> findAllPastBookingByBookerId(Long bookerId);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1 AND CURRENT_TIMESTAMP < b.start")
    Collection<Booking> findAllFutureBookingByBookerId(Long bookerId);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = ?1")
    Collection<Booking> findAllByOwnerId(Long ownerId);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = ?1 AND b.status = ?2")
    Collection<Booking> findAllByOwnerIdAndStatus(Long ownerId, Status status);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = ?1 AND CURRENT_TIMESTAMP BETWEEN b.start AND b.end")
    Collection<Booking> findAllCurrentBookingByOwnerId(Long ownerId);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = ?1 AND CURRENT_TIMESTAMP > b.end")
    Collection<Booking> findAllPastBookingByOwnerId(Long ownerId);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = ?1 AND CURRENT_TIMESTAMP < b.start")
    Collection<Booking> findAllFutureBookingByOwnerId(Long ownerId);

    @Query("SELECT b FROM Booking b WHERE b.item.id = :itemId AND b.booker.id = :bookerId AND b.end < :end")
    Optional<Booking> findByItemIdAndBookerIdAndEndBefore(@Param("itemId") Long itemId,
                                                          @Param("bookerId") Long bookerId,
                                                          @Param("end") LocalDateTime end);

    @Query("SELECT b.start FROM Booking b WHERE b.item.id = ?1 AND CURRENT_TIMESTAMP < b.start")
    List<LocalDateTime> findNextBookingStartByItemId(Long itemId);

    @Query("SELECT b.end FROM Booking b WHERE b.item.id = ?1 AND CURRENT_TIMESTAMP > b.end " +
            "AND b.status = 'APPROVED' ORDER BY b.end DESC")
    Optional<LocalDateTime> findLastBookingEndByItemId(Long itemId);

    @Query("SELECT b FROM Booking b WHERE b.item.id IN (?1) AND CURRENT_TIMESTAMP > b.end ORDER BY b.end DESC")
    List<Booking> findByItemInAndEndBefore(List<Long> ids);

    @Query("SELECT b FROM Booking b WHERE b.item.id IN (?1) AND CURRENT_TIMESTAMP < b.start ORDER BY b.start ASC")
    List<Booking> findByItemInAndStartAfter(List<Long> ids);
}