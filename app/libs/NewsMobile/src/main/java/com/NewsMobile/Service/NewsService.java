package com.NewsMobile.Service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.NewsMobile.Dto.NewsDto;
import com.NewsMobile.Entity.NewsEntity;

public interface NewsService {
	
	
	public NewsDto addNews(NewsEntity newsEntity ) throws Exception;

	List<NewsEntity> getAll();

	NewsEntity getById(Long id);

}
