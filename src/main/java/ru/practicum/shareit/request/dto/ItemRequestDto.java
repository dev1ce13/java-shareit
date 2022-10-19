package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
public class ItemRequestDto {

    private long id;
    @NotNull
    @NotEmpty
    private String description;
    private User requester;
    private LocalDateTime created;
}
