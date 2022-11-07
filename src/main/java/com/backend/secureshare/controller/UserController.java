package com.backend.secureshare.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.secureshare.services.UserServices;
import com.backend.secureshare.payloads.UserDto;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private UserServices userServices;
    
    //create user
    @PostMapping("/")
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto){
        UserDto createUserDto = this.userServices.createUser(userDto);
        return new ResponseEntity<>(createUserDto, HttpStatus.CREATED);
    }
    
    //update user
    @PutMapping("/{userId}")
    public ResponseEntity<UserDto> updateUser(@RequestBody UserDto userDto, @PathVariable Integer userId){
        UserDto updatedUser = this.userServices.updateUser(userDto, userId);
        return ResponseEntity.ok(updatedUser);
    }
    
    //Delete user
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer userId){
        this.userServices.deleteUser(userId);
        return ResponseEntity.ok("User deleted successfully");
    }
    
    //get all users
    @GetMapping("/")
    public ResponseEntity<List<UserDto>> getAllUser(){
        return ResponseEntity.ok(this.userServices.getAllUser());
    }
    
    //get single user
    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUser(@PathVariable Integer userId){
        return ResponseEntity.ok(this.userServices.getUserById(userId));
    }
}
