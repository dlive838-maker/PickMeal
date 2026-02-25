package PickMeal.PickMeal.controller;

import PickMeal.PickMeal.domain.File;
import PickMeal.PickMeal.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.core.region.Ec2MetadataRegionProvider;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;

@RestController
@Slf4j
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;

    @PostMapping("/file/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile multipartFile) throws IOException{

        try{
            String imgUrl = fileService.uploadImageToS3(multipartFile);

            return ResponseEntity.ok(imgUrl);
        }catch (Exception e){
            log.error("이미지 업로드 실패", e);
            return ResponseEntity.internalServerError().body("이미지 업로드에 실패했습니다.");
        }
    }

//    @GetMapping("/image/{filename}")
//    public ResponseEntity<Resource> displayImage(@PathVariable String filename) throws MalformedURLException {
//        Resource resource = new UrlResource("file:" + uploadDir + filename);
//        if (!resource.exists()) {
//            return ResponseEntity.notFound().build();
//        }
//        return ResponseEntity.ok().body(resource);
//    }

//    @GetMapping("/files/download/{fileId}")
//    public ResponseEntity<Resource> downloadFile(@PathVariable long fileId) throws MalformedURLException{
//        File file = fileService.findById(fileId);
//        if(file == null){return ResponseEntity.notFound().build();}
//        UrlResource resource = new UrlResource("file:" + file.getFilePath());
//
//        String encodedOriginalName = UriUtils.encode(file.getOriginalName(), StandardCharsets.UTF_8);
//        return ResponseEntity.ok()
//                .header("Content-Disposition",
//                        "attachment; filename=\"" + encodedOriginalName + "\"")
//                .body(resource);
//    }

}
