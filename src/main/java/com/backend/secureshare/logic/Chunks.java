package com.backend.secureshare.logic;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;


public class Chunks {
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
        BufferedOutputStream bw = new BufferedOutputStream(new FileOutputStream(inputFile.getAbsolutePath().substring(0,inputFile.toString().length()-4) + "-Output"+ fileType));
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