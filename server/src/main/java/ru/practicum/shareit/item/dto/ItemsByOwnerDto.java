package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.Set;

@Data
@Builder
public class ItemsByOwnerDto {

    private long id;
    private String name;
    private String description;
    private Boolean available;
    private long ownerId;
    private Long requestId;
    private BookingDto nextBooking;
    private BookingDto lastBooking;
    private Set<CommentDto> comments;
}
