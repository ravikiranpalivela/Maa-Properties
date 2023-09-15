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


@Entity
@Table(name = "ShortEntity")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShortEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	
	@Nationalized
	private String title;
	
	@Nationalized 
	private String language;
	
	private String videoName;
	
	private String videoFilePath;
	
	private Date scheduleDate;
	
	private String videoUrl;
	
	@Transient
	private MultipartFile video;
	
	@Nationalized 
	private String videoDescription;
}
