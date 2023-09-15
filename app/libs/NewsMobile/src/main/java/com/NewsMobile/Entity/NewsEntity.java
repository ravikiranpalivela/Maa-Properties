package com.NewsMobile.Entity;

import java.sql.Date;

import org.hibernate.annotations.Nationalized;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor 
@Entity
@Table(name="main_news")
public class NewsEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long newsId;
	
	private String category;
	
	@Nationalized
	private String language;
	
	@Transient
	private MultipartFile image;
	private String imageName;
	
	private String imagePath;
	
	private String imageUrl;
	
	@Transient
	private MultipartFile video;
	private String videoName;
	
	private String videoPath;
	
	private String videoUrl;
	
	private String sourceFrom;
	
	private String websiteUrl;
	
	private Date scheduleDate;
	
	private String location;
	
	@Nationalized
	private String title;
	
	@Nationalized
	private String description;

}
