package mocmien.com.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    /**
     * Upload file lên Cloudinary với folder tuỳ chọn
     * @param file MultipartFile cần upload
     * @param folder Folder trên Cloudinary, ví dụ: "avatars" hoặc "products/123"
     * @return URL an toàn (secure_url)
     */
    public String upload(MultipartFile file, String folder) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "folder", folder
                )
        );
        return uploadResult.get("secure_url").toString();
    }

    // Upload mặc định vào root nếu không truyền folder
    public String upload(MultipartFile file) throws IOException {
        return upload(file, "");
    }
}
