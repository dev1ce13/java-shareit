package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswerDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto addRequest(ItemRequestDto itemRequestDto, long userId);

    List<ItemRequestWithAnswerDto> getUserRequests(long userId);

    List<ItemRequestWithAnswerDto> getRequests(long userId, int from, Integer size);

    ItemRequestWithAnswerDto getRequestById(long requestId, long userId);
}
