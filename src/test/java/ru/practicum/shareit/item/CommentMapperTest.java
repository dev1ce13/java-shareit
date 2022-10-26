package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public class CommentMapperTest {

    @Test
    public void mapToCommentTest() {
        User user = new User(1L, "Name", "email@mail.ru");
        var commentDto = CommentDto.builder()
                .id(1L)
                .text("text")
                .build();

        Comment result = CommentMapper.mapToComment(commentDto, user, 1L);

        Assertions.assertEquals(1, result.getId());
        Assertions.assertEquals("text", result.getText());
        Assertions.assertEquals(user.getName(), result.getAuthor().getName());
        Assertions.assertEquals(1L, result.getItemId());
    }

    @Test
    public void mapToCommentDtoTest() {
        User user = new User(1L, "Name", "email@mail.ru");
        var comment = new Comment(1L, "Text", user, 1L, LocalDateTime.now());

        CommentDto result = CommentMapper.mapToCommentDto(comment);

        Assertions.assertEquals(1, result.getId());
        Assertions.assertEquals("Text", result.getText());
        Assertions.assertEquals(user.getName(), result.getAuthorName());
        Assertions.assertEquals(1L, result.getItemId());
    }
}
