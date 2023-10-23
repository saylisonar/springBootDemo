package com.example.demo.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.opencsv.CSVWriter;

@RestController
public class CsvController {
	@GetMapping("/test")
	public String showStatus() throws URISyntaxException {
		String path = "D:/Tasks_Documents/CSV/demo.csv";
		createCsvFile();
		return "This is spring boot demo project.";
	}
	
	public static void createCsvFile() throws URISyntaxException {
		try {  
			    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH.mm.ss");  
			    Date date = new Date();  
			    String filedate = formatter.format(date);
			    String fileName = "Demo"+filedate;
		      File myObj = new File("D:/Tasks_Documents/CSV/"+fileName+".csv");  
		      if (myObj.createNewFile()) {  
		        System.out.println("File created: " + myObj.getName());  
		        System.out.println("Absolute path: " + myObj.getAbsolutePath());  
		        writeDataLineByLine(myObj.getAbsolutePath());
		      } else {  
		        System.out.println("File already exists.");  
		      }  
		    } catch (IOException e) {
		      System.out.println("An error occurred.");
		      e.printStackTrace();  
		    }  
	}
	
	public static void writeDataLineByLine(String filePath) throws URISyntaxException
	{
	    try {
	    	
	        FileWriter outputfile = new FileWriter(filePath);
	        CSVWriter writer = new CSVWriter(outputfile);
	  
	        // adding header to csv
	        String[] header = { "Name", "Class", "Marks" };
	        writer.writeNext(header);
	  
	        // add data to csv
	        
	        /*
	        String[] data1 = { "Aman", "10", "620" };
	        writer.writeNext(data1);
	        String[] data2 = { "Suraj", "10", "630" };
	        writer.writeNext(data2);
	        */
	        List<String[]> data = new ArrayList<String[]>();
	        data.add(new String[] { "Ramesh", "10", "640" });
	        data.add(new String[] { "Aman", "10", "620" });
	        data.add(new String[] { "Suraj", "10", "630" });
	        writer.writeAll(data);
	  
	        // closing writer connection
	        writer.close();
	        
	        
	        File myObj = new File(filePath);  
	        String from = filePath;
			String to = "D:/Tasks_Documents/CSV2/"+myObj.getName();
	        saveFile(from, to);
	        retryUploadingCsv(filePath);
	        
	    }
	    catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	public static void saveFile(String from, String to) throws IOException {
		Path src = Paths.get(from); 
		Path dest = Paths.get(to);
		Files.copy(src, dest);
	}		
	
public static void retryUploadingCsv(String filePath) throws IOException {
	
		File myObj = new File(filePath);  
		String from = filePath;
		String to = "D:/Tasks_Documents/CSV2/"+myObj.getName();
		
		//LOG.warn("-------------------------- Retrying uploading file {} time -------------",retryCounter);
		
		String fileName = myObj.getName();
		String localLocation = from.trim();
		String localFilePath = localLocation;
		
		System.out.println(fileName);
		
		ChannelSftp channelSftp = new ChannelSftp();
		try {
			channelSftp = getSftpSession();
		/*	JSch jsch = new JSch();
			String remoteHost = "172.31.2.30"; //IP
		    Session jschSession = jsch.getSession("deepakkhapre", remoteHost, 22);
		    jschSession.setPassword("bnt123");
		    java.util.Properties config = new java.util.Properties(); 
		    config.put("StrictHostKeyChecking", "no");
		    jschSession.setConfig(config);
		    jschSession.connect();
		    
		    */
			System.out.println("channelSftp"+channelSftp);
			channelSftp.connect();
			
			String remoteDirPath = "/home/readonlyuser/sanction_csv/" + fileName;
			//String remoteDirPath = "/Desktop/" + fileName;
			System.out.println("Remote Directory : "+remoteDirPath);
			System.out.println("localFilePath : "+localFilePath);
			channelSftp.put(localFilePath,remoteDirPath);
			
			//LOG.warn("-------------------------- Retrying uploading file {} time Completed -------------",retryCounter);
		}catch(Exception e) {
			System.out.println("Error in SanctionCsvRetryHandler in retryUploadingCsv method "+e);
			
		}
		finally {
			channelSftp.exit();
			System.out.println("-------------------------- Channel close -------------");
		}
	}
	
	private static ChannelSftp getSftpSession() throws JSchException {
		JSch jsch = new JSch();
		String remoteHost = "172.31.4.141"; //IP
	    Session jschSession = jsch.getSession("readonlyuser", remoteHost, 22);
	    jschSession.setPassword("R3ad0nly");
	    java.util.Properties config = new java.util.Properties(); 
	    config.put("StrictHostKeyChecking", "no");
	    jschSession.setConfig(config);
	    jschSession.connect();
	    System.out.println("-------------------------- Connected -------------");
	    return (ChannelSftp) jschSession.openChannel("sftp");
	}

	
}
