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
@Table(name = "WishesEntity")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WishesEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	
	@Nationalized
	private String title;
	
	@Nationalized 
	private String language;
	
	private String imageName;
	
	private String imageFilePath;
	
	private Date scheduleDate;
	
	private String imageUrl;

}
