package com.NewsMobile.Entity;

import java.sql.Date;

import org.hibernate.annotations.Nationalized;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.Column;
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

@Entity
@Table(name = "news2")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GoodMorningNews {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	//@Column(columnDefinition = "nvarchar")
	@Nationalized
	private String title;
	//@Column(columnDefinition = "nvarchar")
	@Nationalized 
	private String language;
	//@Column(columnDefinition = "nvarchar")
	@Nationalized 
	private String description;
	//@Column(columnDefinition = "nvarchar")
	@Nationalized 
	private String location;
	private String imageUrl;
	private String videoUrl;
	private String postType;
	private String imageName;
	private String imageFilePath;
	private String videoName;
	private String videoFilePath;
	private Date scheduleDate;
	private String websiteUrl;
	private String inputlanguage;
	
	@Transient
	private MultipartFile image;
	@Transient
	private MultipartFile video;
}
