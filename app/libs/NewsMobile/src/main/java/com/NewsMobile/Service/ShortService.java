package com.NewsMobile.Service;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.NewsMobile.Dto.ShortDto;
import com.NewsMobile.Entity.ShortEntity;
import com.NewsMobile.Entity.WishesEntity;

public interface ShortService {

	ResponseEntity<ShortDto> Create(ShortEntity news) throws IOException;

	List<ShortEntity> getAll();

	ResponseEntity<Object> getById(Integer id);

}
