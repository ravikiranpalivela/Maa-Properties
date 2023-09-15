package com.NewsMobile.Controller;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.NewsMobile.Entity.GoodMorningNews;
import com.NewsMobile.Service.GoodMorningNewsService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/news")
@RequiredArgsConstructor
public class GoodMorningNewsController {
	
	private final GoodMorningNewsService newsService;
	
	
	@PostMapping
	@ResponseStatus(value = HttpStatus.CREATED)
	public GoodMorningNews createNews(GoodMorningNews news) {
		log.debug("News Created");		
		return newsService.Create(news);
	}
		
	@GetMapping("/file/{fileName}")
	public ResponseEntity<Resource> getImage(@PathVariable String fileName){
		return newsService.getImage(fileName);
	}
	
	
	@GetMapping("/video/{fileName}")
	public ResponseEntity<Resource> getVideo(@PathVariable String fileName){
		return newsService.getVideo(fileName);
	}
	
	@GetMapping("/getall")
	public List<GoodMorningNews> getAll(){
		log.info("get all news");
		return newsService.getAll();
	}

}
