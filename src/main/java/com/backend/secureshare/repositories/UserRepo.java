package com.backend.secureshare.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.secureshare.entities.User;

public interface UserRepo extends JpaRepository<User,Integer>{

}
