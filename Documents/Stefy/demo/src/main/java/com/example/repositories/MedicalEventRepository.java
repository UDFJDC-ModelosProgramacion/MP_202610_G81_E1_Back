package com.example.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.entities.MedicalEventEntity;

@Repository
public interface MedicalEventRepository extends JpaRepository<MedicalEventEntity, Long> {
}