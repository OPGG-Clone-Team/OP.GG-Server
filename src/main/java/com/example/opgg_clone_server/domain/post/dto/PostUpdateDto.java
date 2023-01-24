package com.example.opgg_clone_server.domain.post.dto;

import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public record PostUpdateDto(
        Optional<String> title,
        Optional<String> content,
        Optional<MultipartFile> uploadFile) {
}
