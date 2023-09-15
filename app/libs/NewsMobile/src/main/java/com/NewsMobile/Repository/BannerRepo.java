package com.NewsMobile.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.NewsMobile.Entity.Banners;

@Repository
public interface BannerRepo extends JpaRepository<Banners, Long>{

}
