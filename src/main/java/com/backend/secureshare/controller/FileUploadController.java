package com.backend.secureshare.controller;


import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.backend.secureshare.entities.Document;
import com.backend.secureshare.entities.User;
import com.backend.secureshare.logic.Action;
import com.backend.secureshare.repository.DocumentRepository;
import com.backend.secureshare.repository.UserRepository;


@RestController
@CrossOrigin
public class FileUploadController {
    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;

    @Autowired
    public FileUploadController(UserRepository userRepository, DocumentRepository documentRepository) {
        this.userRepository = userRepository;
        this.documentRepository=documentRepository;
    }
    
    @PostMapping("/Login")
    public ResponseEntity<?> login(@RequestParam("email") String email, @RequestParam("password") String password) {
        // Find the user with the given email
        User user = userRepository.findByEmail(email);

        // If the user does not exist, return UNAUTHORIZED
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // If the user exists, check if the password matches
        if (user.getPassword().equals(password)) {
            return ResponseEntity.ok().body(user.getUsername());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/Signup")
    public ResponseEntity<?> signup(@RequestParam("email") String email, @RequestParam("password") String password) {
        
        User user = new User();
        user.setPassword(password);
        user.setEmail(email);
        if(userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User already exists.");
        }
        String uname;
        try {
            uname = UserName.generateUsername(email,userRepository);
            user.setUsername(uname);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error generating username");
        }

        userRepository.save(user);

        return ResponseEntity.ok().body(user.getUsername());
    }
    
	@PostMapping("/upload-file")
	public ResponseEntity<Resource> uploadFile(@RequestParam("file") MultipartFile file, 
	        @RequestParam("password") String password, @RequestParam("mode") String mode,
	        @RequestParam("username") String username) throws Exception{
	    
	    User user=userRepository.findByUsername(username);
	    Document doc=new Document();
	    doc.setTitle(file.getOriginalFilename());
	    doc.setUser(user);
	    doc.setFileData(file.getBytes());
	    doc.setFileSize(file.getSize());
	    documentRepository.save(doc);

		String fileType=doc.getTitle().substring(doc.getTitle().length()-4);
		System.out.println(fileType);
		
		boolean response = false;
		if(mode.equals("enc")) {
			response=Action.encrypt(doc, password, fileType);
		}
		else if(mode.equals("dec")) {
			response=Action.decrypt(doc, password, fileType);
		}
		if(!response){
		    System.out.print("Not successfull");
	        return ResponseEntity.ok().body(null);
		}
	
		Path path = Paths.get("src\\"+doc.getTitle().substring(0,doc.getTitle().length()-4)+"-Output"+fileType);
		System.out.println(path);
        Resource resource = null;
        try {
            resource = new UrlResource(path.toUri());
        } catch (MalformedURLException e) {
            System.out.print("Controller: "+e);
        }
        ResponseEntity<Resource> responseEntity = ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
        
//      Delete the file
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.schedule(() -> {
            try {
                Files.delete(path);
                System.out.println("File deleted: Output-" + doc.getTitle());
            } catch (Exception e) {
                System.out.println("Error deleting file: " + e.getMessage());
            }
        }, 5, TimeUnit.SECONDS);
        return responseEntity ;
        }

//	@GetMapping("/download/{fileName:.+}")
//	public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
//	    
//	}
}

