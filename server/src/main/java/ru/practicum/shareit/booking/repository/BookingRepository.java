package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerIdOrderByStartDesc(Long userId, Pageable pageable);

    List<Booking> findByBookerIdAndStatusOrderById(Long userId, Status status, Pageable pageable);

    @Query("SELECT b FROM Booking AS b " +
            "WHERE b.booker.id = :booker_id " +
            "AND b.end < current_timestamp " +
            "ORDER BY b.start DESC")
    List<Booking> findPastBookings(@Param("booker_id") Long userId, Pageable pageable);

    @Query("SELECT b FROM Booking AS b " +
            "WHERE b.booker.id = :booker_id " +
            "AND b.start < current_timestamp " +
            "AND b.end > current_timestamp " +
            "ORDER BY b.start DESC")
    List<Booking> findCurrentBookings(@Param("booker_id") Long userId, Pageable pageable);

    @Query("SELECT b FROM Booking AS b " +
            "WHERE b.booker.id = :booker_id " +
            "AND b.start > current_timestamp " +
            "ORDER BY b.start DESC")
    List<Booking> findFutureBookings(@Param("booker_id") Long userId, Pageable pageable);

    @Query("SELECT b from Booking AS b " +
            "JOIN b.item AS i " +
            "WHERE i.owner.id = :owner_id " +
            "ORDER BY b.start DESC")
    List<Booking> findByOwnerId(@Param("owner_id") Long userId, Pageable pageable);

    @Query("SELECT b FROM Booking AS b " +
            "JOIN b.item AS i " +
            "WHERE i.owner.id = :owner_id " +
            "AND b.end < :time " +
            "ORDER by b.start DESC")
    List<Booking> findPastOwners(@Param("owner_id") Long userId, @Param("time") LocalDateTime time, Pageable pageable);

    @Query("SELECT b FROM Booking AS b " +
            "JOIN b.item AS i " +
            "WHERE i.owner.id = :owner_id " +
            "AND b.end > :time " +
            "AND b.start < :time " +
            "ORDER BY b.start DESC")
    List<Booking> findCurrentOwners(@Param("owner_id") Long userId, @Param("time") LocalDateTime time,
                                    Pageable pageable);

    @Query("SELECT b FROM Booking AS b " +
            "JOIN b.item AS i " +
            "WHERE i.owner.id = :owner_id " +
            "AND b.start > :time " +
            "ORDER BY b.start DESC")
    List<Booking> findFutureOwners(@Param("owner_id") Long userId, @Param("time") LocalDateTime time,
                                   Pageable pageable);

    @Query("SELECT b FROM Booking AS b " +
            "JOIN b.item AS i " +
            "WHERE i.owner.id = :owner_id " +
            "AND b.status = :status " +
            "ORDER BY b.start DESC")
    List<Booking> findOwnersAndStatusEquals(@Param("owner_id") Long userId, @Param("status") Status status,
                                            Pageable pageable);

    @Query("SELECT b FROM Booking AS b " +
            "WHERE b.booker.id = :booker_id " +
            "AND b.item.id = :item_id " +
            "AND b.end < current_timestamp")
    List<Booking> findBookersAndItems(@Param("booker_id") Long userId, @Param("item_id") Long itemId);

    Optional<Booking> findFirstByItemIdAndEndIsBeforeAndStatusIs(Long itemId, LocalDateTime time,
                                                                 Status status, Sort sort);

    Optional<Booking> findFirstByItemIdAndStartIsAfterAndStatusIs(Long itemId, LocalDateTime time,
                                                                  Status status, Sort sort);

    List<Booking> findFirstByItemIdInAndEndIsBeforeOrderByEndDesc(List<Long> itemIds, LocalDateTime time);

    List<Booking> findFirstByItemIdInAndStartIsAfterOrderByStart(List<Long> itemIds, LocalDateTime time);
}
