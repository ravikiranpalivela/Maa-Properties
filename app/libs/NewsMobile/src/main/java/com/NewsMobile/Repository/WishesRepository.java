package com.NewsMobile.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.NewsMobile.Entity.WishesEntity;

@Repository
public interface WishesRepository extends JpaRepository<WishesEntity, Integer>{

}
