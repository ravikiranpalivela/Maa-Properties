package com.NewsMobile.ServiceImpl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.NewsMobile.Entity.GoodMorningNews;
import com.NewsMobile.Entity.WishesEntity;
import com.NewsMobile.Repository.GoodMorningNewsRepository;
import com.NewsMobile.Repository.WishesRepository;
import com.NewsMobile.Service.WishesService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class WishesServiceImpl implements WishesService{

	private final WishesRepository wishesRepo;
	
	@Value("${spring.config.location}")
	private String location;
	@Value("${link}")
	private String path;
	@Value("${server.port}")
	private String port;	
	private String url="/api/news/file/";
	
	
	@Override
	public WishesEntity Create(WishesEntity news, MultipartFile image) {
		WishesEntity wishesEntity = null;
		try {			
			String imageName = null;
			if(image != null) {
				 imageName = StringUtils.cleanPath(image.getOriginalFilename());
				 this.saveFile(location, imageName, image);
			}
			news.setImageFilePath(url+image.getOriginalFilename());
			news.setImageName(image.getOriginalFilename());
			wishesEntity= wishesRepo.save(news);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return wishesEntity;
	}



	@Override
	public List<WishesEntity> getAll() {
		List<WishesEntity> newsList= wishesRepo.findAll();
		try {
			List<WishesEntity> list= new ArrayList<>();
			for(WishesEntity  newList:newsList) {			
				String image=newList.getImageName();				
				newList.setImageFilePath(this.getUrl(image.replace(" ", "%20")));
				list.add(newList);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return newsList;
	}
	
	public void saveFile(String uploadDir, String fileName, MultipartFile multipartFile) throws IOException {
		Path uploadPath = Paths.get(uploadDir);

		if (!Files.exists(uploadPath)) {
			Files.createDirectories(uploadPath);
		}

		try (InputStream inputStream = multipartFile.getInputStream()) {
			Path filePath = uploadPath.resolve(fileName);
			Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException ioe) {
			throw new IOException("Could not save image file: " + fileName, ioe);
		}
	}
	
	public String getUrl(String name) {
		  String url2 = null;
		  try {
			  url2="http://"+path+url;
			  log.info(url2);
		  }catch(Exception e) {
			  e.printStackTrace();
		  }
		 
	     return url2+name;
	  }

}
