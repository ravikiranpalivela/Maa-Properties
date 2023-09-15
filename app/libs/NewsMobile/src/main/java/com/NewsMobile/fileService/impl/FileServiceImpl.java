package com.NewsMobile.fileService.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.NewsMobile.fileService.FileService;

@Service
public class FileServiceImpl implements FileService {
	
	@Value("${spring.config.location}")
	private String location;

	@Override
	public String saveFile(MultipartFile file) throws IOException {
		Path uploadPath = Paths.get(location);

		if (!Files.exists(uploadPath)) {
			Files.createDirectories(uploadPath);
		}

		try (InputStream inputStream = file.getInputStream()) {
			Path filePath = uploadPath.resolve(file.getOriginalFilename());
			Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException ioe) {
			throw new IOException("Could not save image file: " + file.getOriginalFilename(), ioe);
		}
		return location;
	}

}
