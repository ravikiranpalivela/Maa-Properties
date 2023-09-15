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
import com.NewsMobile.Entity.WishesEntity;
import com.NewsMobile.Service.GoodMorningNewsService;
import com.NewsMobile.Service.WishesService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/Wishes")
@RequiredArgsConstructor
public class WishesController {
	
	private final WishesService wishesService;

	@PostMapping
	@ResponseStatus(value = HttpStatus.CREATED)
	public WishesEntity createNews(WishesEntity news,@RequestParam("image") MultipartFile image) {
		log.debug("News Created");		
		return wishesService.Create(news,image);
	}
		
	
	@GetMapping("/getall")
	public List<WishesEntity> getAll(){
		log.info("get all news");
		return wishesService.getAll();
	}
}
