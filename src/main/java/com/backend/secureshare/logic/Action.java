package com.backend.secureshare.logic;

import java.io.File;


import org.apache.tomcat.util.http.fileupload.FileUtils;

import com.backend.secureshare.exception.CryptoException;
import com.backend.secureshare.keygenerator.KeyGen;


public class Action {
	public static boolean encrypt(File f1, String password, String fileType, long size) throws Exception {
		String keyAES = KeyGen.getMd5AES(password);
		String keyDES = KeyGen.getMd5DES(password);
//		System.out.print(keyAES+"\n"+keyDES);
		
		File dir = new File(f1.getAbsoluteFile()+"_Encrypted_Chunks_Splits");
		dir.mkdir();
		if(size<=100000000) {
			Chunks.splitFile(f1.getAbsoluteFile(),"Original_File", 10, fileType);
			for(int i=1;i<=10;i++) {
				try {
					File encryptedChunkFile = new File(dir+"\\split" + i + fileType);
					encryptedChunkFile.createNewFile();
					File chunkFiles= new File(f1.getAbsoluteFile()+"Original_File_Splits\\split" + i + fileType);
					if(i%2==0) {
						CryptoAES.encrypt(keyAES,"maxEncryption", chunkFiles, encryptedChunkFile);
					}
					else{
						CryptoDES.encrypt(keyDES, chunkFiles, encryptedChunkFile);
					}
				}
				catch(CryptoException ex) {
		            System.out.println(ex.getMessage());
		            ex.printStackTrace();
		            return false;
				}
			}
			Chunks.joinFiles(f1.getAbsoluteFile(),"_Encrypted_Chunks", fileType, 10);
			
		}
		else if(size>100000000 && size<=1000000000) {
			Chunks.splitFile(f1.getAbsoluteFile(),"Original_File", 50, fileType);
			for(int i=1;i<=50;i++) {
				try {
					File encryptedChunkFile = new File(dir+"\\split" + i + fileType);
					encryptedChunkFile.createNewFile();
					File chunkFiles= new File(f1.getAbsoluteFile()+"Original_File_Splits\\split" + i + fileType);
					if(i%2==0) {
						CryptoAES.encrypt(keyAES,"maxEncryption", chunkFiles, encryptedChunkFile);
					}
					else{
						CryptoDES.encrypt(keyDES, chunkFiles, encryptedChunkFile);
					}
				}
				catch(CryptoException ex) {
		            System.out.println(ex.getMessage());
		            ex.printStackTrace();
		            return false;
				}
			}
			Chunks.joinFiles(f1,"_Encrypted_Chunks", fileType, 50);
		}
		else if(size>1000000000 && size<=10000000000L) {
			Chunks.splitFile(f1.getAbsoluteFile(),"Original_File", 100, fileType);
			for(int i=1;i<=100;i++) {
				try {
					File encryptedChunkFile = new File(dir+"\\split" + i + fileType);
					encryptedChunkFile.createNewFile();
					File chunkFiles= new File(f1.getAbsoluteFile()+"Original_File_Splits\\split" + i + fileType);
					if(i%2==0) {
						CryptoAES.encrypt(keyAES,"maxEncryption", chunkFiles, encryptedChunkFile);
					}
					else{
						CryptoDES.encrypt(keyDES, chunkFiles, encryptedChunkFile);
					}
					
				}
				catch(CryptoException ex) {
		            System.out.println(ex.getMessage());
		            ex.printStackTrace();
		            return false;
				}
			}
			Chunks.joinFiles(f1,"_Encrypted_Chunks", fileType, 100);
		}
		else if(size>10000000000L && size<=100000000000L) {
			Chunks.splitFile(f1.getAbsoluteFile(),"Original_File", 300, fileType);
			for(int i=1;i<=300;i++) {
				try {
					File encryptedChunkFile = new File(dir+"\\split" + i + fileType);
					encryptedChunkFile.createNewFile();
					File chunkFiles= new File(f1.getAbsoluteFile()+"Original_File_Splits\\split" + i + fileType);
					if(i%2==0) {
						CryptoAES.encrypt(keyAES,"maxEncryption", chunkFiles, encryptedChunkFile);
					}
					else{
						CryptoDES.encrypt(keyDES, chunkFiles, encryptedChunkFile);
					}
					
				}
				catch(CryptoException ex) {
		            System.out.println(ex.getMessage());
		            ex.printStackTrace();
		            return false;
				}
			}
			Chunks.joinFiles(f1,"_Encrypted_Chunks", fileType, 300);
		}
		FileUtils.deleteDirectory(dir);
		File o= new File(f1.getAbsoluteFile()+"Original_File_Splits");
		FileUtils.deleteDirectory(o);
		return true;
	}
	
	
	public static boolean decrypt(File f1, String password, String fileType, long size) throws Exception {
		String keyAES = KeyGen.getMd5AES(password);
		String keyDES = KeyGen.getMd5DES(password);
		System.out.print(keyAES+"\n"+keyDES);
		File dir = new File(f1.getAbsoluteFile()+"_Decrypted_Chunks_Splits");
		dir.mkdir();
		if(size<=100000000) {
			Chunks.splitFile(f1.getAbsoluteFile(),"Original_File", 10, fileType);
			for(int i=1;i<=10;i++) {
				try {
					File decryptedChunkFile = new File(dir+"\\split" + i + fileType);
					decryptedChunkFile.createNewFile();
					File chunkFiles= new File(f1.getAbsoluteFile()+"Original_File_Splits\\split" + i + fileType);
					if(i%2==0) {
						CryptoAES.decrypt(keyAES,"maxEncryption", chunkFiles, decryptedChunkFile);
					}
					else{
						CryptoDES.decrypt(keyDES, chunkFiles, decryptedChunkFile);
					}
				}
				catch(CryptoException ex) {
		            System.out.println(ex.getMessage());
		            ex.printStackTrace();
		            return false;
				}
			}
			Chunks.joinFiles(f1,"_Decrypted_Chunks",fileType,10);
		}
		
		else if(size>100000000 && size<=1000000000) {
			Chunks.splitFile(f1.getAbsoluteFile(),"Original_File", 50, fileType);
			for(int i=1;i<=50;i++) {
				try {
					File decryptedChunkFile = new File(dir+"\\split" + i + fileType);
					decryptedChunkFile.createNewFile();
					File chunkFiles= new File(f1.getAbsoluteFile()+"Original_File_Splits\\split" + i + fileType);
					if(i%2==0) {
						CryptoAES.decrypt(keyAES,"maxEncryption", chunkFiles, decryptedChunkFile);
					}
					else{
						CryptoDES.decrypt(keyDES, chunkFiles, decryptedChunkFile);
					}
				}
				catch(CryptoException ex) {
		            System.out.println(ex.getMessage());
		            ex.printStackTrace();
		            return false;
				}
			}
			Chunks.joinFiles(f1,"_Decrypted_Chunks",fileType,50);
		}
		else if(size>1000000000 && size<=10000000000L) {
			Chunks.splitFile(f1.getAbsoluteFile(),"Original_File", 100, fileType);
			for(int i=1;i<=100;i++) {
				try {
					File decryptedChunkFile = new File(dir+"\\split" + i + fileType);
					decryptedChunkFile.createNewFile();
					File chunkFiles= new File(f1.getAbsoluteFile()+"Original_File_Splits\\split" + i + fileType);
					if(i%2==0) {
						CryptoAES.decrypt(keyAES,"maxEncryption", chunkFiles, decryptedChunkFile);
					}
					else{
						CryptoDES.decrypt(keyDES, chunkFiles, decryptedChunkFile);
					}
				}
				catch(CryptoException ex) {
		            System.out.println(ex.getMessage());
		            ex.printStackTrace();
		            return false;
				}
			}
			Chunks.joinFiles(f1,"_Decrypted_Chunks",fileType,100);
		}
		else if(size>10000000000L && size<=100000000000L) {
			Chunks.splitFile(f1.getAbsoluteFile(),"Original_File", 300, fileType);
			for(int i=1;i<=300;i++) {
				try {
					File decryptedChunkFile = new File(dir+"\\split" + i + fileType);
					decryptedChunkFile.createNewFile();
					File chunkFiles= new File(f1.getAbsoluteFile()+"Original_File_Splits\\split" + i + fileType);
					if(i%2==0) {
						CryptoAES.decrypt(keyAES,"maxEncryption", chunkFiles, decryptedChunkFile);
					}
					else{
						CryptoDES.decrypt(keyDES, chunkFiles, decryptedChunkFile);
					}
				}
				catch(CryptoException ex) {
		            System.out.println(ex.getMessage());
		            ex.printStackTrace();
		            return false;
				}
			}
			Chunks.joinFiles(f1,"_Decrypted_Chunks",fileType,300);
		}
		FileUtils.deleteDirectory(dir);
		File o= new File(f1.getAbsoluteFile()+"Original_File_Splits");
		FileUtils.deleteDirectory(o);
		return true;
	}
}
