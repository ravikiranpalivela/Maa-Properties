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
public class ShortDto {

	private int id;
	
	private String title;
	
	private String language;
	
	private String videoName;
	
	private String videoFilePath;
	
	private Date scheduleDate;
	
	private String videoUrl;
	
	private String videoDescription;
	
	private MultipartFile video;
	
	
}
