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
@Table(name="banners")
public class Banners {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long bannerId;
	
	@Nationalized
	private String title;
	
	@Nationalized
	private String language;
	
	@Transient
	private MultipartFile image;
    private String imageName;
	
	private String imagePath;
	
	@Transient
	private MultipartFile video;
	
    private String videoName;
	
	private String videoPath;
	
	@Nationalized
	private String description;
	
	private Date scheduleDate;
	
	private String websiteUrl;
	
	private String location;

}
