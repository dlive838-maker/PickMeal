package PickMeal.PickMeal.controller;

import PickMeal.PickMeal.domain.File;
import PickMeal.PickMeal.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
@Controller
public class FileController {
    private final FileService fileService;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @GetMapping("/")
    public String index() {
        return "redirect:/upload.html";
    }

    @GetMapping("/images/{filename}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) throws MalformedURLException {
        Resource resource = new UrlResource("file:" + uploadDir + filename);

        if(!resource.exists()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(resource);
    }

    @GetMapping("/files/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable long fileId) throws MalformedURLException {
        File file = fileService.findById(fileId);

        if(file == null){
            return ResponseEntity.notFound().build();
        }
        UrlResource resource = new UrlResource("file:" + file.getFilePath());

        String encodeOriginalName = UriUtils.encode(file.getOriginalName(), StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header("Content-Disposition",
                        "attachment; filename=\"" + encodeOriginalName + "\"")
                .body(resource);
    }

    @PostMapping("/upload")
    @ResponseBody
    public String uploadFile(@RequestParam("boardId") Long boardId, @RequestParam("file") MultipartFile file) {
        fileService.saveFile(boardId, file);
        return "Upload successful";
    }

}
