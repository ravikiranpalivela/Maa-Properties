package com.NewsMobile.Controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.NewsMobile.Entity.Banners;
import com.NewsMobile.Service.BannerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/banner")
@RequiredArgsConstructor
public class BannerController {
	
	private final BannerService bannerService;
	
	@PostMapping
	public String addBanner(Banners banner) throws Exception {
		log.debug("/api/banner - adding banner");
		return bannerService.addBanner(banner, banner.getImage(), banner.getVideo());
	}
	
	@GetMapping
	public Page<Banners> getAllBanner(Pageable pageable) {
		log.debug("/api/banner - loading all banners");
		return bannerService.getAllBanners(pageable);
	}
	
	@GetMapping("/{id}")
	public Banners getBannerById(@PathVariable Long id) {
		log.debug("/api/banner - getting banner by id :{}"+id);
		return bannerService.getBannerById(id);
	}

}
