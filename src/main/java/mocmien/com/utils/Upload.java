package mocmien.com.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class Upload {
	@Value("${file.upload.dir:D:/upload/}")
	private String uploadDir;

	public String saveFile(MultipartFile file, String relativeFolder) throws IOException {
		if (file == null || file.isEmpty()) {
			return null;
		}

		Path dirPath = Paths.get(uploadDir + relativeFolder);
		if (!Files.exists(dirPath)) {
			Files.createDirectories(dirPath);
		}

		String originalFilename = file.getOriginalFilename();
		String fileName = System.currentTimeMillis() + "_" + originalFilename;
		Path filePath = dirPath.resolve(fileName);

		Files.write(filePath, file.getBytes());

		return relativeFolder + "/" + fileName;
	}

	public File getFile(String relativePath) {
		return new File(uploadDir + relativePath);
	}

	public boolean deleteFile(String relativePath) {
		if (relativePath == null || relativePath.isEmpty()) {
			return false;
		}
		File file = getFile(relativePath);
		return file.exists() && file.delete();
	}

	public String getUploadDir() {
		return uploadDir;
	}
}