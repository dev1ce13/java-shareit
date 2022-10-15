package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public class CommentMapper {

    public static Comment mapToComment(CommentDto commentDto, User user, long itemId) {
        return new Comment(
                commentDto.getId(),
                commentDto.getText(),
                user,
                itemId,
                LocalDateTime.now()
        );
    }

    public static CommentDto mapToCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .itemId(comment.getItemId())
                .created(comment.getCreated())
                .build();
    }
}
