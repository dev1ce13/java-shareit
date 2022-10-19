package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemsByOwnerDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto add(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @Valid @RequestBody ItemDto itemDto
    ) {
        return itemService.add(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestBody ItemDto itemDto,
            @PathVariable long itemId
    ) {
        return itemService.update(userId, itemDto, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemsByOwnerDto getById(@PathVariable long itemId,
                                   @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getById(itemId, userId);
    }

    @GetMapping
    public List<ItemsByOwnerDto> getItemsByUserId(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(name = "from", defaultValue = "0", required = false) int from,
            @RequestParam(name = "size", required = false) Integer size
    ) {
        validateParams(from, size);
        return itemService.getItemsByUserId(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> getSearch(@RequestParam(name = "text") String text,
                                   @RequestParam(name = "from", defaultValue = "0", required = false) int from,
                                   @RequestParam(name = "size", required = false) Integer size
    ) {
        validateParams(from, size);
        return itemService.getSearch(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@PathVariable long itemId,
                                 @RequestHeader("X-Sharer-User-Id") long userId,
                                 @Valid @RequestBody CommentDto commentDto) {
        return itemService.addComment(commentDto, userId, itemId);
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
