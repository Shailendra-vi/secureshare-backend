package com.backend.secureshare.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
//import java.io.FileInputStream;
//import java.net.http.HttpHeaders;
//import java.io.IOException;
//import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

//import org.apache.tomcat.util.http.fileupload.IOUtils;
//import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.backend.secureshare.logic.Action;

//messagequeue
//synccall 


@RestController
@CrossOrigin
public class FileUploadController {
    
    private static String contentType="";
    private static String fileType="";
    
	@PostMapping("/upload-file")
	public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("password") String password, @RequestParam("mode") String mode) throws Exception{
		File f1 = new File("C:\\Users\\shail\\Desktop\\SecureShare\\SecureShare\\src\\main\\java\\com\\backend\\secureshare\\logic\\"+file.getOriginalFilename());
		BufferedOutputStream stream =new BufferedOutputStream(new FileOutputStream(f1));
		stream.write(file.getBytes());
		stream.close();
		String m=mode;
//		System.out.print("\n"+mode+"\n"+password);
		fileType=f1.getName().substring(f1.getName().length()-4);
		long size=file.getSize();
		boolean response = false;
		if(m.equals("enc")) {
			response=Action.encrypt(f1, password, fileType, size);
		}
		else if(m.equals("dec")) {
			response=Action.decrypt(f1, password, fileType, size);
		}
		if(!response){
	        return ResponseEntity.ok("Failed");
		}
		contentType=file.getContentType();
		return ResponseEntity.ok("Successfull");
	}

	@GetMapping("/download/{fileName:.+}")
	public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
	    Path path = Paths.get("C:\\Users\\shail\\Desktop\\SecureShare\\SecureShare\\src\\main\\java\\com\\backend\\secureshare\\logic\\"+fileName.substring(0,fileName.length()-4)+"-Output"+fileType);
	    Resource resource = null;
	    try {
	        resource = new UrlResource(path.toUri());
	    } catch (MalformedURLException e) {
	        System.out.print("Controller: "+e);
	    }
	    return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"").body(resource);
	}
}

