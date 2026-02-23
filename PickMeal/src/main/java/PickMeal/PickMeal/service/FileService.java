package PickMeal.PickMeal.service;

import PickMeal.PickMeal.domain.File;
import PickMeal.PickMeal.mapper.FileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileMapper fileMapper;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public List<File> findByBoardId(long boardId){
        return fileMapper.findByBoardId(boardId);
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

            // [수정 포인트 1] 폴더 경로와 파일 전체 경로를 분리합니다.
            // uploadDir는 "C:/upload/" 같은 폴더 경로여야 합니다.
            String savePath = uploadDir;

            // [수정 포인트 2] 폴더가 없으면 폴더만 생성합니다 (파일 이름 제외)
            java.io.File uploadFolder = new java.io.File(savePath);
            if (!uploadFolder.exists()) {
                uploadFolder.mkdirs(); // mkdir()보다 mkdirs()가 더 안전합니다 (상위 폴더까지 생성)
            }

            // [수정 포인트 3] 저장할 파일 객체 생성 (폴더 경로 + 파일 이름)
            // File.separator는 운영체제에 맞는 구분자(\ 또는 /)를 넣어줍니다.
            java.io.File targetFile = new java.io.File(savePath + java.io.File.separator + storedName);

            // 4. 파일을 지정된 경로에 물리적으로 저장
            multipartFile.transferTo(targetFile);

            // 5. DB 저장을 위한 객체 생성
            File fileEntity = new File();
            fileEntity.setBoardId(boardId);
            fileEntity.setOriginalName(originalName);
            fileEntity.setStoredName(storedName);
            fileEntity.setFilePath(savePath + java.io.File.separator + storedName); // 전체 경로 저장

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
            String filePath = file.getFilePath();

            java.io.File deleteFile = new java.io.File(filePath);

            if(deleteFile.exists()){
                deleteFile.delete();
            }

            fileMapper.deleteByBoardId(bno);
        }
    }

    public File findById(long fileId) {
        return fileMapper.findById(fileId);
    }

}