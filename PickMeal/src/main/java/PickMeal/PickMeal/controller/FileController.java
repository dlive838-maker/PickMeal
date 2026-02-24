package PickMeal.PickMeal.controller;

import PickMeal.PickMeal.domain.File;
import PickMeal.PickMeal.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.util.UriUtils;

import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;

@Controller
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @GetMapping("/image/{filename}")
    public ResponseEntity<Resource> displayImage(@PathVariable String filename) throws MalformedURLException {
        Resource resource = new UrlResource("file:" + uploadDir + filename);
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(resource);
    }

    @GetMapping("/files/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable long fileId) throws MalformedURLException{
        File file = fileService.findById(fileId);
        if(file == null){return ResponseEntity.notFound().build();}
        UrlResource resource = new UrlResource("file:" + file.getFilePath());

        String encodedOriginalName = UriUtils.encode(file.getOriginalName(), StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header("Content-Disposition",
                        "attachment; filename=\"" + encodedOriginalName + "\"")
                .body(resource);
    }

}
