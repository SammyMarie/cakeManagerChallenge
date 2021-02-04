package com.sammy.repository;

import com.sammy.entity.business.CakeDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CakeRepository extends JpaRepository<CakeDTO, Long> {
}