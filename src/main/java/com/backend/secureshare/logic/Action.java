package com.backend.secureshare.logic;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.apache.tomcat.util.http.fileupload.FileUtils;

import com.backend.secureshare.entities.Document;
import com.backend.secureshare.exception.CryptoException;
import com.backend.secureshare.keygenerator.KeyGen;


public class Action {
    
    public static boolean encryptInAction(File tempFile,String fileType, int noOfSplit, String keyAES,String keyDES) throws Exception {
        Chunks.splitFile(tempFile,"Original_File", noOfSplit, fileType);
        File dir = new File(tempFile.getAbsolutePath()+"_Encrypted_Chunks_Splits");
        dir.mkdir();
        for(int i=1;i<=noOfSplit;i++) {
            try {
                File encryptedChunkFile = new File(tempFile.getAbsolutePath()+"_Encrypted_Chunks_Splits\\split" + i + fileType);
                encryptedChunkFile.createNewFile();
                File chunkFiles= new File(tempFile.getAbsolutePath()+"Original_File_Splits\\split" + i + fileType);
                if(i%2==0) {
                    CryptoAES.encrypt(keyAES, chunkFiles, encryptedChunkFile);
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
        Chunks.joinFiles(tempFile,"_Encrypted_Chunks", fileType, noOfSplit);
        return true;
    }
    
    
    public static boolean encrypt(Document doc, String password, String fileType) throws Exception {
        String keyAES = KeyGen.getMd5AES(password);
        String keyDES = KeyGen.getMd5DES(password);
        
        System.out.println(keyAES+"\n"+keyDES);
        
        byte[] fileData = doc.getFileData();
        long size = doc.getFileSize();
            
        File tempFile = new File("src//"+doc.getTitle());
        tempFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(tempFile);
        fos.write(fileData);
        fos.close();
//        System.out.print(tempFile.getAbsolutePath());
        boolean ret=false;
        if(size<=100000000) {
            ret= encryptInAction(tempFile, fileType, 10, keyAES, keyDES);
        }
        else if(size>100000000 && size<=1000000000) {
            ret= encryptInAction(tempFile, fileType, 50, keyAES, keyDES);
        }
        else if(size>1000000000 && size<=10000000000L) {
            ret= encryptInAction(tempFile, fileType, 100, keyAES, keyDES);
        }
        else if(size>10000000000L && size<=100000000000L) {
            ret= encryptInAction(tempFile, fileType, 300, keyAES, keyDES);
        }
        File o = new File(tempFile.getAbsolutePath());
        o.delete();
        o= new File(tempFile.getParentFile()+"\\"+doc.getTitle()+"_Encrypted_Chunks_Splits");
        FileUtils.deleteDirectory(o);
        o=new File(tempFile.getParentFile()+"\\"+doc.getTitle()+"Original_File_Splits");
        FileUtils.deleteDirectory(o);
        return ret;
    }

	static boolean decryptInAction(File tempFile,String fileType, int noOfSplit, String keyAES,String keyDES) throws Exception {
	    Chunks.splitFile(tempFile,"Original_File", noOfSplit, fileType);
        File dir = new File(tempFile.getAbsolutePath()+"_Decrypted_Chunks_Splits");
        dir.mkdir();
        for(int i=1;i<=noOfSplit;i++) {
            try {
                File decryptedChunkFile = new File(tempFile.getAbsolutePath()+"_Decrypted_Chunks_Splits\\split" + i + fileType);
                decryptedChunkFile.createNewFile();
                File chunkFiles= new File(tempFile.getAbsolutePath()+"Original_File_Splits\\split" + i + fileType);
                if(i%2==0) {
                    CryptoAES.decrypt(keyAES, chunkFiles, decryptedChunkFile);
                }
                else{
                    CryptoDES.decrypt(keyDES, chunkFiles, decryptedChunkFile);
                }
            }
            catch(Exception ex) {
//                System.out.println(ex.getMessage());
                ex.printStackTrace();
                return false;
            }
        }
        Chunks.joinFiles(tempFile,"_Decrypted_Chunks", fileType, noOfSplit);
        return true;
	}
	
	public static boolean decrypt(Document doc, String password, String fileType) throws Exception {
		String keyAES = KeyGen.getMd5AES(password);
		String keyDES = KeyGen.getMd5DES(password);
		
		System.out.println(keyAES+"\n"+keyDES);
		
		byte[] fileData = doc.getFileData();
        long size = doc.getFileSize();
            
        File tempFile = new File("src//"+doc.getTitle());
        tempFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(tempFile);
        fos.write(fileData);
        fos.close();
//        System.out.print(tempFile.getAbsolutePath());
        
        boolean ret=false;
        if(size<=100000000) {
            ret= decryptInAction(tempFile, fileType, 10, keyAES, keyDES);
        }
        else if(size>100000000 && size<=1000000000) {
            ret= decryptInAction(tempFile, fileType, 50, keyAES, keyDES);
        }
        else if(size>1000000000 && size<=10000000000L) {
            ret= decryptInAction(tempFile, fileType, 100, keyAES, keyDES);
        }
        else if(size>10000000000L && size<=100000000000L) {
            ret= decryptInAction(tempFile, fileType, 300, keyAES, keyDES);
        }
        File o = new File(tempFile.getAbsolutePath());
        o.delete();
        o= new File(tempFile.getParentFile()+"\\"+doc.getTitle()+"_Decrypted_Chunks_Splits");
        FileUtils.deleteDirectory(o);
        o=new File(tempFile.getParentFile()+"\\"+doc.getTitle()+"Original_File_Splits");
        FileUtils.deleteDirectory(o);
        return ret;
	}
}

class Chunks {
    public static void splitFile(File inputFile, String t, int no_of_file, String fileType) throws Exception{
        RandomAccessFile raf = new RandomAccessFile(inputFile, "r");
        long sourceSize = raf.length();
        long bytesPerSplit = sourceSize / no_of_file;
        long remainingBytes = sourceSize % no_of_file;

        int maxReadBufferSize = 8 * 1024; // 8KB
        File directory = new File(inputFile +t+ "_Splits");
        directory.mkdir();
        for (int destIx = 1; destIx <= no_of_file; destIx++) {
            File newFile = new File(inputFile +t+ "_Splits\\split" + destIx + fileType);
            newFile.createNewFile();
            BufferedOutputStream bw = new BufferedOutputStream(new FileOutputStream(inputFile + t+"_Splits\\split" + destIx + fileType));
            if (bytesPerSplit > maxReadBufferSize) {
                long numReads = bytesPerSplit / maxReadBufferSize;
                long numRemainingRead = bytesPerSplit % maxReadBufferSize;
                for (int i = 0; i < numReads; i++) {
                    readWrite(raf, bw, maxReadBufferSize);
                }
                if (numRemainingRead > 0) {
                    readWrite(raf, bw, numRemainingRead);
                }
            } else {
                readWrite(raf, bw, bytesPerSplit);
            }
            bw.close();
        }
        if (remainingBytes > 0) {
            BufferedOutputStream bw = new BufferedOutputStream(new FileOutputStream("split." + no_of_file + 1));
            readWrite(raf, bw, remainingBytes);
            bw.close();
        }
        raf.close();
    }
    
    
    //Join File//
    static void joinFiles(File[] files, File inputFile, String fileType) throws Exception {
        int maxReadBufferSize = 8 * 1024;
//        System.out.println(inputFile.getName().substring(0,inputFile.getName().length()-4));
        
        File inputFileParent = inputFile.getParentFile();
        String outputFilePath = inputFileParent.getAbsolutePath() +"\\"+inputFile.getName().substring(0,inputFile.getName().length()-4)+ "-Output" + fileType;
        System.out.println(outputFilePath);
        BufferedOutputStream bw = new BufferedOutputStream(new FileOutputStream(outputFilePath));

        RandomAccessFile raf = null;
        for (File file : files) {
            raf = new RandomAccessFile(file, "r");
            long numReads = raf.length() / maxReadBufferSize;
            long numRemainingRead = raf.length() % maxReadBufferSize;
            for (int i = 0; i < numReads; i++) {
                readWrite(raf, bw, maxReadBufferSize);
            }
            if (numRemainingRead > 0) {
                readWrite(raf, bw, numRemainingRead);
            }
            raf.close();

        }
        bw.close();
    }

    public static void joinFiles(File inputFile,String t,String fileType, int numberOfFiles) throws Exception {

        File[] files = new File[numberOfFiles];
        for (int i = 1; i <= numberOfFiles; i++) {
            files[i - 1] = new File(inputFile + t+"_Splits\\split" + i + fileType);
        }
        joinFiles(files,inputFile, fileType);
    }
    
    static void readWrite(RandomAccessFile raf, BufferedOutputStream bw, long numBytes) throws IOException {
        byte[] buf = new byte[(int) numBytes];
        int val = raf.read(buf);
        if (val != -1) {
            bw.write(buf,0,val);
        }
    }
}
