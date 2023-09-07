package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerId(Long ownerId, Pageable pageable);

    @Query("SELECT it FROM Item AS it " +
            "WHERE it.available = TRUE AND " +
            "(UPPER(it.name) LIKE UPPER(CONCAT('%', :text,'%')) " +
            "OR UPPER(it.description) LIKE UPPER(CONCAT('%', :text,'%')))")
    List<Item> getAvailableItemByText(@Param("text") String text, Pageable pageable);

    List<Item> findAllByRequestId(Long requestId);

    List<Item> findByRequestIdIn(List<Long> requestIds);
}
