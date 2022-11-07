package com.backend.secureshare.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="users")
@NoArgsConstructor // to make object of user
@Getter
@Setter
public class User {
	//primary key
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	//to change username and set can not be empty
	@Column(name="user_name", nullable=false, length=100)
	private String name;
	
	@Column(name="user_email", nullable=false, length=100)
	private String email;
	
	@Column(name="user_password", nullable=false, length=100)
	private String password;
}
