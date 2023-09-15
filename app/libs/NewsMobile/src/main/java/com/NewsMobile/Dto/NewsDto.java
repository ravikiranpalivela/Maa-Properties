package com.NewsMobile.Dto;

import java.sql.Date;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NewsDto {

	private Long newsId;
	
	private String category;
	

	private String language;
	

	private String imageName;
	
	private String imagePath;
	
	private String imageUrl;

	private String videoName;
	
	private String videoPath;
	
	private String videoUrl;
	
	private String sourceFrom;
	
	private String websiteUrl;
	
	private Date scheduleDate;
	
	private String location;
	
	private String title;

	private String description;

}
