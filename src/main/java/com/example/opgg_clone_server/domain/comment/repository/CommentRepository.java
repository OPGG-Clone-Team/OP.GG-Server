package com.example.opgg_clone_server.domain.comment.repository;

import com.example.opgg_clone_server.domain.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
