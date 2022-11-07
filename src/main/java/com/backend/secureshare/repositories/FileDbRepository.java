package com.backend.secureshare.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backend.secureshare.model.DBModel;

@Repository
public interface FileDbRepository extends JpaRepository<DBModel, String> {

}
