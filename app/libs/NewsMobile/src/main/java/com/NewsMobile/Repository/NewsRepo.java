package com.NewsMobile.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.NewsMobile.Entity.NewsEntity;

@Repository
public interface NewsRepo extends JpaRepository<NewsEntity, Long>{

}
