package mocmien.com;

import mocmien.com.service.CloudinaryService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    private final CloudinaryService cloudinaryService;

    public UploadController(CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }

    /**
     * Upload file chung, folder có thể truyền param
     * @param file MultipartFile
     * @param folder folder trên Cloudinary, optional
     */
    @PostMapping
    public Map<String, String> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", required = false) String folder) {
        try {
            String url;
            if (folder != null && !folder.isEmpty()) {
                url = cloudinaryService.upload(file, folder);
            } else {
                url = cloudinaryService.upload(file);
            }
            return Map.of("url", url);
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("error", "Upload thất bại: " + e.getMessage());
        }
    }
}
