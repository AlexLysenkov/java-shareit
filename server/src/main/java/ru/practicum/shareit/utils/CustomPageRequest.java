package ru.practicum.shareit.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class CustomPageRequest extends PageRequest {
    public CustomPageRequest(Integer page, Integer size, Sort sort) {
        super(page, size, sort);
    }

    public CustomPageRequest(Integer page, Integer size) {
        this(page, size, Sort.unsorted());
    }
}
