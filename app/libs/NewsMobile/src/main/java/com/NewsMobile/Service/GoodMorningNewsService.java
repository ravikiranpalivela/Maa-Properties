package com.NewsMobile.Service;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.NewsMobile.Entity.GoodMorningNews;

public interface GoodMorningNewsService {
    
	public GoodMorningNews Create(GoodMorningNews news);
	
	public ResponseEntity<Resource> getImage( String filename);

	public List<GoodMorningNews> getAll();

	ResponseEntity<Resource> getVideo(String filename);
}
