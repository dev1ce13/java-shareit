package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemByRequestDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ItemRequestWithAnswerDto {

    private long id;
    private String description;
    private User requester;
    private LocalDateTime created;
    private List<ItemByRequestDto> items;
}
