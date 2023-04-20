package com.backend.secureshare.services;

import java.util.List;

import com.backend.secureshare.payloads.UserDto;

public interface UserServices {
	UserDto createUser(UserDto user);
	UserDto updateUser(UserDto user, Integer userId);
	UserDto getUserById(Integer userId);
	List<UserDto> getAllUser();
	void deleteUser(Integer userId);
}
