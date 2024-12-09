package ru.practicum.shareit.item.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.comment.entity.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("SELECT c FROM Comment c WHERE c.item.id = :itemId")
    List<Comment> findAllByItemId(@Param("itemId") Long itemId);

    @Query("SELECT c FROM Comment c WHERE c.item.id IN :ids")
    List<Comment> findAllCommentsByItemIn(List<Long> ids);
}