package com.NewsMobile.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.NewsMobile.Entity.Banners;

public interface BannerService {

	String addBanner(Banners banner, MultipartFile image, MultipartFile video) throws Exception;

	Page<Banners> getAllBanners(Pageable pageable);

	Banners getBannerById(Long id);

}
