package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswerDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto addRequest(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @Valid @RequestBody ItemRequestDto itemRequestDto
    ) {
        return itemRequestService.addRequest(itemRequestDto, userId);
    }

    @GetMapping
    public List<ItemRequestWithAnswerDto> getUserRequests(
            @RequestHeader("X-Sharer-User-Id") long userId
    ) {
        return itemRequestService.getUserRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestWithAnswerDto> getRequests(
           @RequestHeader("X-Sharer-User-Id") long userId,
           @RequestParam(name = "from", defaultValue = "0", required = false) int from,
           @RequestParam(name = "size", required = false) Integer size
    ) {
        validateParams(from, size);
        return itemRequestService.getRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestWithAnswerDto getRequestById(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long requestId
    ) {
        return itemRequestService.getRequestById(requestId, userId);
    }

    private void validateParams(int from, Integer size) {
        if (from < 0) {
            throw new IllegalArgumentException("Parameter from must be => 0");
        }
        if (size != null) {
            if (size <= 0) {
                throw new IllegalArgumentException("Parameter size must be > 0");
            }
        }
    }
}
