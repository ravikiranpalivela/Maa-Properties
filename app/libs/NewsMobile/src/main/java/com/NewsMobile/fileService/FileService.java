package com.NewsMobile.fileService;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
	
	public String saveFile( MultipartFile multipartFile) throws IOException;

}
