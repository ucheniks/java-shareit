package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerId(Long bookerId, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId AND b.start <= :now AND b.end >= :now")
    List<Booking> findCurrentByBookerId(Long bookerId, LocalDateTime now, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId AND b.end < :now")
    List<Booking> findPastByBookerId(Long bookerId, LocalDateTime now, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId AND b.start > :now")
    List<Booking> findFutureByBookerId(Long bookerId, LocalDateTime now, Pageable pageable);

    List<Booking> findByBookerIdAndStatus(Long bookerId, BookingStatus status, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId")
    List<Booking> findByOwnerId(Long ownerId, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId AND b.start <= :now AND b.end >= :now")
    List<Booking> findCurrentByOwnerId(Long ownerId, LocalDateTime now, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId AND b.end < :now")
    List<Booking> findPastByOwnerId(Long ownerId, LocalDateTime now, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId AND b.start > :now")
    List<Booking> findFutureByOwnerId(Long ownerId, LocalDateTime now, Pageable pageable);

    List<Booking> findByItem_OwnerIdAndStatus(Long ownerId, BookingStatus status, Pageable pageable);

    @Query("select b from Booking b " +
            "where b.item.id = :itemId " +
            "and b.status = ru.practicum.shareit.booking.BookingStatus.APPROVED " +
            "and b.end < :now " +
            "order by b.end desc")
    Optional<Booking> findLastBookingForItem(@Param("itemId") Long itemId, @Param("now") LocalDateTime now);

    @Query("select b from Booking b " +
            "where b.item.id = :itemId " +
            "and b.status = ru.practicum.shareit.booking.BookingStatus.APPROVED " +
            "and b.start > :now " +
            "order by b.start asc")
    Optional<Booking> findNextBookingForItem(@Param("itemId") Long itemId, @Param("now") LocalDateTime now);

    List<Booking> findByItemIdAndBookerIdAndEndBefore(Long itemId, Long bookerId, LocalDateTime end);
}

