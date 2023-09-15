package com.NewsMobile.Controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.NewsMobile.Dto.ShortDto;
import com.NewsMobile.Entity.ShortEntity;
import com.NewsMobile.Service.ShortService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/short")
@RequiredArgsConstructor
public class ShortController {

	private final ShortService shortService;

	@PostMapping()
	@ResponseStatus(value = HttpStatus.CREATED)
	public ResponseEntity<ShortDto> createNews( ShortEntity news) throws IOException {
		log.debug("News Created");		
		return shortService.Create(news);
	}

	@GetMapping("/getall")
	public List<ShortEntity> getAll(){
		log.info("get all news");
		return shortService.getAll();
	}
	
	@GetMapping("/{shortId}")
	public ResponseEntity<Object> getById(@PathVariable Integer shortId) {
		log.debug("/api/short id:{}"+ shortId);
		return shortService.getById(shortId);
	}
}
