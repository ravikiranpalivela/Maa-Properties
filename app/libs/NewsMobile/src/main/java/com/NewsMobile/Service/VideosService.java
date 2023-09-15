package com.NewsMobile.Service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.NewsMobile.Entity.Videos;

public interface VideosService {

	String addVideos(Videos vEntity, MultipartFile image, MultipartFile video) throws Exception;

	List<Videos> getAllVideos(Pageable pageable);

	Videos getVideoById(Long id);

}
