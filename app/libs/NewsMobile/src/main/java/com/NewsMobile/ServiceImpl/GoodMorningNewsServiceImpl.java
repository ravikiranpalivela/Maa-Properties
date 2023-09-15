package com.NewsMobile.ServiceImpl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.NewsMobile.Entity.GoodMorningNews;
import com.NewsMobile.Repository.GoodMorningNewsRepository;
import com.NewsMobile.Service.GoodMorningNewsService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class GoodMorningNewsServiceImpl implements GoodMorningNewsService {

	private final GoodMorningNewsRepository newsRepo;

	@Value("${spring.config.location}")
	private String location;
	@Value("${link}")
	private String path;
	@Value("${server.port}")
	private String port;
	
	private String url="/api/news/file/";
		
	
	@Override
	public GoodMorningNews Create(GoodMorningNews news) {
		GoodMorningNews newsEntity = null;
		try {			
			String imageName= null;
			String videoName = null;					
			try {
				if(news.getImage() != null) {
					 imageName = StringUtils.cleanPath(news.getImage().getOriginalFilename());
					 this.saveFile(location, imageName, news.getImage());
				}
				if(news.getVideo() != null) {
					 videoName = StringUtils.cleanPath(news.getVideo().getOriginalFilename());
					 this.saveFile(location, videoName, news.getVideo());
				}			
			} catch (IOException ioe) {
					ioe.printStackTrace();
			}
			news.setImageFilePath(url);
			news.setImageName(imageName);
			news.setVideoFilePath(url);
			news.setVideoName(videoName);
			newsEntity= newsRepo.save(news);

			
			/*
			 * if(news.getImage() != null) {
			 * newsEntity.setImageFilePath(this.getUrl(news.getImage().getOriginalFilename()
			 * .replace(" ", "%20"))); } if(news.getVideo() != null) {
			 * newsEntity.setVideoFilePath(this.getUrl(news.getVideo().getOriginalFilename()
			 * .replace(" ", "%20"))); }
			 */
			
			
		}catch(Exception r) {
			r.printStackTrace();
		}		
		return newsEntity;
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
			  //url2="http://"+path+":"+port+url;
			   url2="http://"+path+url;
			  log.info(url2);
		  }catch(Exception e) {
			  e.printStackTrace();
		  }
		 
	     return url2+name;
	  }
	 
	
	
	
	@Override
	public ResponseEntity<Resource> getImage( String filename) {
	    String filePath = location + File.separator + filename;
	    Resource resource;
	    try {
	        resource = new UrlResource("file:" + filePath);
	    } catch (MalformedURLException e) {
	        e.printStackTrace();
	        return ResponseEntity.notFound().build();
	    }
	    if (resource.exists()) {
	        return ResponseEntity.ok()
	                .contentType(MediaType.IMAGE_JPEG) 
	                .body(resource);
	    } else {
	        return ResponseEntity.notFound().build();
	    }
	}
	
	@Override
	public ResponseEntity<Resource> getVideo( String filename) {
	    String filePath = location + File.separator + filename;
	    Resource resource;
	    try {
	        resource = new UrlResource("file:" + filePath);
	    } catch (MalformedURLException e) {
	        e.printStackTrace();
	        return ResponseEntity.notFound().build();
	    }
	    if (resource.exists()) {
	        return ResponseEntity.ok()
	                .contentType(MediaType.APPLICATION_OCTET_STREAM) 
	                .body(resource);
	    } else {
	        return ResponseEntity.notFound().build();
	    }
	}
	
	@Override
	public List<GoodMorningNews> getAll() {
		log.info(" in the get all service impl");
		
		List<GoodMorningNews> newsList= newsRepo.findAll();
		try {
			List<GoodMorningNews> list= new ArrayList<>();
			for(GoodMorningNews  newList:newsList) {
				String image=newList.getImageName();
				String image3 = image.replace(" ", "%20");
				String video=newList.getVideoName();
				String video2 = video.replace(" ", "%20");						
				newList.setImageFilePath(this.getUrl(image3));
				newList.setVideoFilePath(this.getUrl(video2));
				list.add(newList);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return newsList;
	}

}
