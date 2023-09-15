package com.NewsMobile.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.NewsMobile.Entity.GoodMorningNews;

@Repository
public interface GoodMorningNewsRepository extends JpaRepository<GoodMorningNews, Integer> {

}
