package com.NewsMobile.ServiceImpl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.NewsMobile.Entity.Banners;
import com.NewsMobile.Repository.BannerRepo;
import com.NewsMobile.Service.BannerService;
import com.NewsMobile.fileService.FileService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Service
@RequiredArgsConstructor
@Slf4j
public class BannerServiceImpl implements BannerService {
	
	@Value("${link}")
	private String path;
	@Value("${server.port}")
	private String port;
	
	private String urlImage="/api/news/file/";
	private String urlVideo="/api/news/video/";
	private final FileService fileService;
	private final BannerRepo bannerRepo;
	
	@Override
	@Transactional
	public String addBanner(Banners banner , MultipartFile image, MultipartFile video)  throws Exception {
		String imagePath="";
		String videoPath="";
		try {
			 imagePath=fileService.saveFile(image);
			 videoPath=fileService.saveFile(video);
		}catch (Exception e) {
			throw new Exception("error during uploading file");
		}
		if(videoPath != null) {
			banner.setImageName(image.getOriginalFilename());
			banner.setVideoName(video.getOriginalFilename());
			banner.setImagePath(urlImage);
			banner.setVideoPath(urlVideo);
			bannerRepo.save(banner);
		}
		
		return "Created";
	}
	
	@Override
	@Transactional(readOnly = true)
	public Page<Banners> getAllBanners( Pageable pageable){
		Page<Banners> banners=bannerRepo.findAll(pageable);
		for(Banners banner:banners) {
			//fixing the image url
			String imagePath=banner.getImageName();
			String image=imagePath.replace(" ", "%20");
			banner.setImagePath(this.getImageUrl(image));
			//fixing the video url
			String videoPath=banner.getVideoPath();
			String video=videoPath.replace(" ", "%20");
			banner.setVideoPath(this.getVideoUrl(video));
		}
		return banners;
	}
	
	@Override
	@Transactional(readOnly = true)
	public Banners getBannerById(Long id) {
		Optional<Banners> banner=bannerRepo.findById(id);
		Banners ban=null;
		if(banner.isPresent()) {
			ban=banner.get();
			String imagePath=ban.getImageName();
			String image=imagePath.replace(" ", "%20");
			ban.setImagePath(this.getImageUrl(image));
			//fixing the video url
			String videoPath=ban.getVideoPath();
			String video=videoPath.replace(" ", "%20");
			ban.setVideoPath(this.getVideoUrl(video));
			return ban;
		}
		return ban;
	}
	
	public String getImageUrl(String name) {
		String url2=null;//"http://"+path+":"+port+url;
		url2="http://"+path+urlImage;
	    log.info(url2);
	    return url2+name;
    }
	
	
	public String getVideoUrl(String name) {
		String url2=null;//"http://"+path+":"+port+url;
		url2="http://"+path+urlVideo;
	    log.info(url2);
	    return url2+name;
    }

}
