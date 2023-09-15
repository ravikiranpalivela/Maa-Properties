package com.NewsMobile.Controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.NewsMobile.Entity.Videos;
import com.NewsMobile.Service.VideosService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/videos")
@RequiredArgsConstructor
public class VideosController {
	
	private final VideosService videosService;
	
	@PostMapping
	public String addVideos( Videos entity) throws Exception {
		log.debug("/api/videos - adding videos");
		return videosService.addVideos(entity, entity.getImage(), entity.getVideo());
	}
	
	@GetMapping
	public List<Videos> getAllVideos(Pageable pageable) {
		log.debug("/api/videos - loading all  videos");
		return videosService.getAllVideos(pageable);
	}
	
	@GetMapping("/{id}")
	public Videos getVideoById(@PathVariable Long id) {
		log.debug("/api/videos/{id} - getting voidos by id");
		return videosService.getVideoById(id);
	}

}
