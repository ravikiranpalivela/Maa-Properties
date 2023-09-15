package com.NewsMobile.ServiceImpl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.NewsMobile.Dto.ShortDto;
import com.NewsMobile.Entity.ShortEntity;
import com.NewsMobile.Repository.ShortRepository;
import com.NewsMobile.Service.ShortService;
import com.NewsMobile.fileService.FileService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Service
@RequiredArgsConstructor
@Slf4j
public class ShortServiceImpl implements ShortService{
	
	@Value("${spring.config.location}")
	private String location;
	@Value("${link}")
	private String path;
	@Value("${server.port}")
	private String port;	
	private String urlImage="/api/news/file/";
	private String urlVideo="/api/news/video/";
	private final FileService fileService;
	private final ShortRepository shortRepo;

	@Override
	@Transactional
	public ResponseEntity<ShortDto> Create(ShortEntity news) throws IOException {
		ShortDto wishesEntity = new ShortDto();
		ShortEntity entity=null;
			if(news.getVideo() != null) {
				String videoName=news.getVideo().getOriginalFilename();
				 this.saveFile(location, news.getVideo());
				 news.setVideoName(videoName);
				 news.setVideoFilePath(urlVideo);
				 wishesEntity.setVideoName(videoName);
				 wishesEntity.setVideoFilePath(this.getVideoUrl(videoName.replace(" ", "%20")));
			}
			entity= shortRepo.save(news);
			wishesEntity.setId(entity.getId());
			wishesEntity.setLanguage(entity.getLanguage());
			wishesEntity.setScheduleDate(entity.getScheduleDate());
			wishesEntity.setTitle(entity.getTitle());
			wishesEntity.setVideoDescription(entity.getVideoDescription());
			wishesEntity.setVideoUrl(entity.getVideoUrl());
		
		return ResponseEntity.ok(wishesEntity);
	}

	@Override
	public List<ShortEntity> getAll() {
		List<ShortEntity> newsList= shortRepo.findAll();
		try {
			List<ShortEntity> list= new ArrayList<>();
			for(ShortEntity  newList:newsList) {			
				String image=newList.getVideoName();				
				newList.setVideoFilePath(this.getVideoUrl(image.replace(" ", "%20")));
				list.add(newList);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return newsList;
	}
	
	
	@Override
	@Transactional(readOnly = true)
	public ResponseEntity<Object> getById(Integer id) {
		Optional<ShortEntity> shortNews=shortRepo.findById(id);
		if(shortNews.isPresent()) {
			return ResponseEntity.ok(shortNews.get());
		}
		return new ResponseEntity<>("No data found",HttpStatus.NOT_FOUND);
	}

	
	
	
	
	public void saveFile(String uploadDir, MultipartFile multipartFile) throws IOException {
		Path uploadPath = Paths.get(uploadDir);

		if (!Files.exists(uploadPath)) {
			Files.createDirectories(uploadPath);
		}

		try (InputStream inputStream = multipartFile.getInputStream()) {
			Path filePath = uploadPath.resolve(multipartFile.getOriginalFilename());
			Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException ioe) {
			throw new IOException("Could not save image file: " + multipartFile.getOriginalFilename(), ioe);
		}
	}
	
	public String getImageUrl(String name) {
		  String url2 = null;
		  try {
			  //url2="http://"+path+":"+port+url;
			   url2="http://"+path+urlImage;
			  log.info(url2);
		  }catch(Exception e) {
			  e.printStackTrace();
		  }
	     return url2+name;
	  }
	
	public String getVideoUrl(String name) {
		  String url2 = null;
		  try {
			  //url2="http://"+path+":"+port+url;
			   url2="http://"+path+urlVideo;
			  log.info(url2);
		  }catch(Exception e) {
			  e.printStackTrace();
		  }
	     return url2+name;
	  }
}
