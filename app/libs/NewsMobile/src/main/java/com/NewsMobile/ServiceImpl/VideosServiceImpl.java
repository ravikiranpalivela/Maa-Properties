package com.NewsMobile.ServiceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.NewsMobile.Entity.Videos;
import com.NewsMobile.Repository.VideosRepo;
import com.NewsMobile.Service.VideosService;
import com.NewsMobile.fileService.FileService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Service
@RequiredArgsConstructor
@Slf4j
public class VideosServiceImpl  implements VideosService{
	
	@Value("${link}")
	private String path;
	@Value("${server.port}")
	private String port;
	
	private String urlImage="/api/news/file/";
	private String urlVideo="/api/news/video/";
	private final FileService fileService;
	private final VideosRepo videosRepo;
	
	@Override
	@Transactional
	public String addVideos(Videos vEntity,  MultipartFile image, MultipartFile video ) throws Exception {
		String imagePath="";
		String videoPath="";
		try {
			 imagePath=fileService.saveFile(image);
			 videoPath=fileService.saveFile(video);
		}catch (Exception e) {
			throw new Exception("error during uploading file");
		}
		if(imagePath != null) {
			vEntity.setImageName(image.getOriginalFilename());
			vEntity.setVideoName(video.getOriginalFilename());
			vEntity.setImagePath(urlImage);
			vEntity.setVideoPath(urlVideo);
			videosRepo.save(vEntity);
			return "Videos Added";
		}
		return "Error during file upload";
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<Videos> getAllVideos(Pageable pageable) {
		List<Videos> videos=videosRepo.findAll();
		for(Videos v:videos) {
			//fixing the image url
			String imagePath=v.getImageName();
			String image=imagePath.replace(" ", "%20");
			v.setImagePath(this.getImageUrl(image));
			//fixing the video url
			String videoPath=v.getVideoPath();
			String video=videoPath.replace(" ", "%20");
			v.setVideoPath(this.getVideoUrl(video));
		}
		return videos;
	}
	
	
	@Override
	@Transactional(readOnly = true)
	public Videos getVideoById(Long id) {
		Optional<Videos> video=videosRepo.findById(id);
		Videos result=null;
		if(video.isPresent()) {
			result=video.get();
			String imagePath=result.getImageName();
			String image=imagePath.replace(" ", "%20");
			result.setImagePath(this.getImageUrl(image));
			//fixing the video url
			String videoPath=result.getVideoPath();
			String vid=videoPath.replace(" ", "%20");
			result.setVideoPath(this.getVideoUrl(vid));
			return result;
		}
		return result;
	}
	
	
	public String getImageUrl(String name) {
		//String url2="http://"+path+":"+port+url;
		String url2="http://"+path+urlImage;
	    log.info(url2);
	    return url2+name;
    }
	
	public String getVideoUrl(String name) {
		//String url2="http://"+path+":"+port+url;
		String url2="http://"+path+urlVideo;
	    log.info(url2);
	    return url2+name;
    }

}
