package com.NewsMobile.Controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.NewsMobile.Dto.NewsDto;
import com.NewsMobile.Entity.NewsEntity;
import com.NewsMobile.Service.NewsService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/main-news")
@RequiredArgsConstructor
public class NewsController {
	
	private final NewsService newsService;
	
	@PostMapping
	public NewsDto addNews( NewsEntity entity) throws Exception {
		log.info("api/main-news ");		
		return newsService.addNews(entity);
	}
	
	@GetMapping
	public List<NewsEntity> getAllNews() {
		log.debug("/api/main-news - getting all news ");
		return newsService.getAll();
	}
	
	@GetMapping("/{id}")
	public NewsEntity getNewsById(@PathVariable Long id) {
		log.debug("/api/main-news - loading news by id :{}" + id);
		return newsService.getById(id);
	}
	
	@GetMapping("/check")
	public String check() {
		return "working>>>>";
	}

}
