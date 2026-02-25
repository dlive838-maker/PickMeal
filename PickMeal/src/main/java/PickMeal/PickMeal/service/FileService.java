package PickMeal.PickMeal.service;

import PickMeal.PickMeal.domain.File;
import PickMeal.PickMeal.mapper.FileMapper;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {

    private final FileMapper fileMapper;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    private final AmazonS3 amazonS3;

    public List<File> findByBoardId(long boardId){
        return fileMapper.findByBoardId(boardId);
    }


    public String uploadImageToS3(MultipartFile multipartFile){
        if(multipartFile.isEmpty()) return null;

        try{
            String originalName = multipartFile.getOriginalFilename();
            String extension = originalName.substring(originalName.lastIndexOf("."));
            String storedName = UUID.randomUUID().toString() + extension;

            ObjectMetadata objMeta = new ObjectMetadata();
            objMeta.setContentType(multipartFile.getContentType());
            InputStream inputStream = multipartFile.getInputStream();
            objMeta.setContentLength(inputStream.available());

            amazonS3.putObject(bucket, storedName, inputStream, objMeta);
            inputStream.close();

            return "https://s3.ap-northeast-2.amazonaws.com/" + bucket + "/" + storedName;
        } catch (IOException e){
            throw new RuntimeException("S3 파일 업로드 실패", e);

        }
    }

    public void saveFile(long boardId, MultipartFile multipartFile){
        if (multipartFile.isEmpty()) {
            return;
        }

        try {
            // 1. 원본 파일명 추출
            String originalName = multipartFile.getOriginalFilename();

            // 2. 서버 저장용 고유 파일명 생성
            String uuid = UUID.randomUUID().toString();
            String extension = originalName.substring(originalName.lastIndexOf("."));
            String storedName = uuid + extension; // ex: a1b2...png

            ObjectMetadata objMeta = new ObjectMetadata();
            objMeta.setContentType(multipartFile.getContentType());
            InputStream inputStream = multipartFile.getInputStream();
            objMeta.setContentLength(inputStream.available());

            amazonS3.putObject(bucket, storedName, inputStream, objMeta);
            inputStream.close();

            String s3Url = amazonS3.getUrl(bucket, storedName).toString();

            // 5. DB 저장을 위한 객체 생성
            File fileEntity = new File();
            fileEntity.setBoardId(boardId);
            fileEntity.setOriginalName(originalName);
            fileEntity.setStoredName(storedName);
            fileEntity.setFilePath(s3Url);

            // 6. DB 저장
            fileMapper.save(fileEntity);

        } catch (IOException e) {
            throw new RuntimeException("파일 저장 중 오류가 발생했습니다.", e);
        }
    }

    @Transactional
    public void deleteByBoardId(long bno){
        List<File> files = fileMapper.findByBoardId(bno);

        for(File file : files){
            amazonS3.deleteObject(bucket, file.getStoredName());
        }
        fileMapper.deleteByBoardId(bno);
    }

    public File findById(long fileId) {
        return fileMapper.findById(fileId);
    }



}