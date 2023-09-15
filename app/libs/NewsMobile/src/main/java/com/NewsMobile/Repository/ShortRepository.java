package com.NewsMobile.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.NewsMobile.Entity.ShortEntity;

@Repository
public interface ShortRepository extends JpaRepository<ShortEntity, Integer>{

}
