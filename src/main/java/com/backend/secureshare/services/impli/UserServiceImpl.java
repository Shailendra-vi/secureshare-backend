package com.backend.secureshare.services.impli;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.secureshare.entities.User;
import com.backend.secureshare.exception.ResourceNotFoundException;
import com.backend.secureshare.payloads.UserDto;
import com.backend.secureshare.repositories.UserRepo;
import com.backend.secureshare.services.UserServices;

@Service
public class UserServiceImpl implements UserServices {
    @Autowired
    private UserRepo userRepo;

    @Override
    public UserDto createUser(UserDto userDto) {
        User savedUser= this.userRepo.save(dtoToUser(userDto));
        return userToDto(savedUser);
    }

    @Override
    public UserDto updateUser(UserDto userDto, Integer userId) {
        User user=this.userRepo.findById(userId).orElseThrow(()-> new ResourceNotFoundException("User"," id ", userId));
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        
        User updatedUser = this.userRepo.save(user);
        return userToDto(updatedUser);
    }

    @Override
    public UserDto getUserById(Integer userId) {
        User user=this.userRepo.findById(userId).orElseThrow(()-> new ResourceNotFoundException("User"," id ", userId));
        return userToDto(user);
    }

    @Override
    public List<UserDto> getAllUser() {
        List<UserDto> allUser=this.userRepo.findAll().stream().map(user->this.userToDto(user)).collect(Collectors.toList());
        return allUser;
    }

    @Override
    public void deleteUser(Integer userId) {
        User user=this.userRepo.findById(userId).orElseThrow(()-> new ResourceNotFoundException("User"," id ", userId));
        this.userRepo.delete(user);
    }
    
    private User dtoToUser(UserDto userDto) {
        User user = new User();
        user.setId(userDto.getId());
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        return user;
    }
    
    private UserDto userToDto(User user) {
        UserDto userDto=new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        userDto.setPassword(user.getPassword());
        return userDto;
    }

}
