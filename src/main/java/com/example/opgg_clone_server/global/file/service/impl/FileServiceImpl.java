package com.example.opgg_clone_server.global.file.service.impl;

import com.example.opgg_clone_server.global.file.exception.FileException;
import com.example.opgg_clone_server.global.file.exception.FileExceptionType;
import com.example.opgg_clone_server.global.file.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    @Value("${file.dir}")//application.yml 파일에 있는 file.dir의 내용을 가져옴
    private String fileDir;

    //TODO : 테스트코드 작성해야 함

    @Override
    public String save(MultipartFile multipartFile)  {
        String filePath = fileDir + UUID.randomUUID();
        try {
            multipartFile.transferTo(new File(filePath));
        }catch (IOException e){
            //파일 저장 에러!
            throw new FileException(FileExceptionType.FILE_CAN_NOT_SAVE);
        }


        return filePath;
    }

    @Override
    public void delete(String filePath) {
            File file = new File(filePath);

            //존재하지 않는데 굳이 지우나..?
            if(!file.exists()) return;

            if(!file.delete()) throw new FileException(FileExceptionType.FILE_CAN_NOT_DELETE);

    }
}