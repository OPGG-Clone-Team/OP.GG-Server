package com.example.opgg_clone_server.domain.post.dto;

import com.example.opgg_clone_server.domain.post.Post;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.util.Optional;

public record PostSaveDto(@NotBlank(message = "제목을 입력해주세요") String title,
                          @NotBlank(message = "내용을 입력해주세요") String content,
                          Optional<MultipartFile> uploadFile) {

    public Post toEntity() {
        return Post.builder().title(title).content(content).build();
    }
}