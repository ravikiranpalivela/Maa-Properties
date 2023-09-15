package com.NewsMobile.ServiceImpl;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.NewsMobile.Dto.NewsDto;
import com.NewsMobile.Entity.NewsEntity;
import com.NewsMobile.Repository.NewsRepo;
import com.NewsMobile.Service.NewsService;
import com.NewsMobile.fileService.FileService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NewsServiceImpl implements NewsService{
	
	@Value("${link}")
	private String path;
	@Value("${server.port}")
	private String port;
	
	private String urlImage="/api/news/file/";
	private String urlVideo="/api/news/video/";
	
	private final NewsRepo newsRepo;
	private final FileService fileService;

	@Override
	@Transactional
	public NewsDto addNews(NewsEntity newsEntity) throws Exception  {
		NewsDto news = new NewsDto();
		String imagePath="";
		String videoPath="";
		try {
			if(newsEntity.getImage() != null) {
				imagePath=fileService.saveFile(newsEntity.getImage());
			}if(newsEntity.getVideo() != null) {
				videoPath=fileService.saveFile(newsEntity.getVideo());
			}
			 
			 
		}catch (Exception e) {
			throw new Exception("error during uploading file");
		}
		
		if(imagePath != null) {
			if(newsEntity.getImage() != null) {				
				newsEntity.setImageName(newsEntity.getImage().getOriginalFilename());
			}if(newsEntity.getVideo() != null) {
				newsEntity.setVideoName(newsEntity.getVideo().getOriginalFilename());
			}
			
			
			newsEntity.setImagePath(urlImage);
			newsEntity.setVideoPath(urlVideo);
			newsRepo.save(newsEntity);			
			
			news.setNewsId(newsEntity.getNewsId());
			news.setCategory(newsEntity.getCategory());
			news.setLanguage(newsEntity.getLanguage());
			
			if(newsEntity.getImage() != null) {
				String imagePat=newsEntity.getImage().getOriginalFilename();
				String image=imagePat.replace(" ", "%20");
				news.setImagePath(this.getImageUrl(image));
				news.setImageName(imagePat);
				
			}
			news.setImageUrl(newsEntity.getImageUrl());
			
			
			if(newsEntity.getVideo() != null) {
				String videoPat=newsEntity.getVideo().getOriginalFilename();
				String video=videoPat.replace(" ", "%20");
				news.setVideoPath(this.getVideoUrl(video));
				news.setVideoName(videoPat);
			}
			
			
			news.setVideoUrl(newsEntity.getVideoUrl());
			news.setSourceFrom(newsEntity.getSourceFrom());
			news.setWebsiteUrl(newsEntity.getWebsiteUrl());		
			news.setScheduleDate(newsEntity.getScheduleDate());
			news.setLocation(newsEntity.getLocation());
			news.setTitle(newsEntity.getTitle());
			news.setDescription(newsEntity.getDescription());
			
			
		}
		
		
		return news;
	}
	
	@Override
	@org.springframework.transaction.annotation.Transactional(readOnly = true)
	public List<NewsEntity> getAll(){
		List<NewsEntity> allNews= newsRepo.findAll();
		List<NewsEntity> list=new ArrayList<>();
		allNews.forEach(news->{
			if(news.getImageName() !=null) {
				//fixing the image url
				String imagePath=news.getImageName();
				String image=imagePath.replace(" ", "%20");
				news.setImagePath(this.getImageUrl(image));
			}
			if(news.getVideoName()!=null) {
				//fixing the video url
				String videoPath=news.getVideoName();
				String video=videoPath.replace(" ", "%20");
				news.setVideoPath(this.getVideoUrl(video));
			}
			
			list.add(news);
		});
		return list;
	}
	
	@Override
	public NewsEntity getById(Long id) {
		Optional<NewsEntity> news=newsRepo.findById(id);
		NewsEntity newS=null;
		if(news.isPresent()) {
			 newS=news.get();
			//fixing the image url
			String imagePath=newS.getImageName();
			String image=imagePath.replace(" ", "%20");
			newS.setImagePath(this.getImageUrl(image));
			//fixing the video url
			String videoPath=newS.getVideoPath();
			String video=videoPath.replace(" ", "%20");
			newS.setVideoPath(this.getVideoUrl(video));
			return newS;
		}
		return newS;
	}
	
	public String getImageUrl(String name) {
		//String url2=//"http://"+path+":"+port+url;
		
		String url2="http://"+path+urlImage;
	    log.info(url2);
	    return url2+name;
    }
	
	public String getVideoUrl(String name) {
		//String url2=//"http://"+path+":"+port+url;
		
		String url2="http://"+path+urlVideo;
	    log.info(url2);
	    return url2+name;
    }

}
