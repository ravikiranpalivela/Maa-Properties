package com.NewsMobile.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.NewsMobile.Entity.Videos;

@Repository
public interface VideosRepo extends JpaRepository<Videos, Long>{

}
