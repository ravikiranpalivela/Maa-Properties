package com.NewsMobile.Service;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.NewsMobile.Entity.WishesEntity;

public interface WishesService {

	WishesEntity Create(WishesEntity news, MultipartFile image);


	List<WishesEntity> getAll();

}
