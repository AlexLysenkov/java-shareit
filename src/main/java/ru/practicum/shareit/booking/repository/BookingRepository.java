package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdOrderByStartDesc(Long userId);

    List<Booking> findAllByBookerIdAndStatusOrderById(Long userId, Status status);

    List<Booking> findAllByBookerIdAndEndIsBefore(Long userId, LocalDateTime time, Sort sort);

    List<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfter(Long userId, LocalDateTime time,
                                                                 LocalDateTime time2, Sort sort);

    List<Booking> findAllByBookerIdAndStartIsAfter(Long userId, LocalDateTime time, Sort sort);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long userId);

    List<Booking> findAllByItemOwnerIdAndEndIsBefore(Long userId, LocalDateTime time, Sort sort);

    List<Booking> findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfter(Long userId, LocalDateTime time,
                                                                    LocalDateTime time2, Sort sort);

    List<Booking> findAllByItemOwnerIdAndStartIsAfter(Long userId, LocalDateTime time, Sort sort);

    List<Booking> findAllByItemOwnerIdAndStatusEquals(Long userId, Status status);

    List<Booking> findAllByBookerIdAndItemIdAndEndIsBefore(Long userId, Long itemId, LocalDateTime time, Sort sort);

    Optional<Booking> findFirstByItemIdAndEndIsBeforeAndStatusIs(Long itemId, LocalDateTime time,
                                                                 Status status, Sort sort);

    Optional<Booking> findFirstByItemIdAndStartIsAfterAndStatusIs(Long itemId, LocalDateTime time,
                                                                  Status status, Sort sort);
}
