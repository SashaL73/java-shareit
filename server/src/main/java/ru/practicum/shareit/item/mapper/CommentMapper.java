package ru.practicum.shareit.item.mapper;


import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.NewCommentRequest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

@NoArgsConstructor
public class CommentMapper {

    public static Comment mapToComment(User author, Item item, NewCommentRequest request) {
        Comment comment = new Comment();
        comment.setAuthor(author);
        comment.setText(request.getText());
        comment.setItem(item);
        return comment;
    }

    public static CommentDto mapToCommentDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setAuthorName(comment.getAuthor().getName());
        commentDto.setCreated(comment.getDateOfComment().toString());
        return commentDto;
    }
}
