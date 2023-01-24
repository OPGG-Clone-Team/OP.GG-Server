package com.example.opgg_clone_server.global.file.service.impl;

import com.example.opgg_clone_server.global.file.service.FileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class FileServiceTest {

    @Autowired
    FileService fileService;


    private MockMultipartFile getMockUploadFile() throws IOException {
        return new MockMultipartFile("file", "file.jpg", "image/jpg", new FileInputStream("/Users/solmin/Desktop/file/tistory/diary.jpg"));
    }

    @Test
    public void 파일저장_성공() throws Exception {
        //given, when
        String filePath = fileService.save(getMockUploadFile());

        //then
        File file = new File(filePath);
        assertThat(file.exists()).isTrue();

        //finally
        file.delete();//파일 삭제


    }

    @Test
    public void 파일삭제_성공() throws Exception {
        //given, when
        String filePath = fileService.save(getMockUploadFile());
        fileService.delete(filePath);

        //then
        File file = new File(filePath);
        assertThat(file.exists()).isFalse();
        
    }

}