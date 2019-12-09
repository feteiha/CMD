import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;
import org.apache.commons.io.FileUtils;

public class Terminal {
	
	private static String directory = "C:\\Users\\Khaled-Predator\\Desktop\\FCI\\FCI - Y3 - T1\\CS241 - Operating System - 1\\Assignment\\temp\\";
	
	public void clear() {
		for (int i=0 ; i<150 ; i++) 
			System.out.println("");
		
	}

    public void date(){
		java.util.Date date=new java.util.Date();  
		System.out.println(date); 
    }
	
    public void ls() {
		ls (directory);
	}
	
	public void cd(String destinationPath) {
		File checkDir = new File(getPath(destinationPath));
        if(!checkDir.exists()){
            System.out.println("Error! Directory not Found");
            return;
        }
        directory = getPath(destinationPath);
	}
	
	public void cd() {
		directory = "F:\\";
	}
	
	public void cp(String sourcePath, String destinationPath ) throws IOException {
		ArrayList<String> sources = new ArrayList<String>();
		int indexOfSpace = sourcePath.indexOf(" ");
		if (indexOfSpace == -1) {
			File file = new File(getPath(sourcePath));
			if (!file.exists())
				System.out.println("No source named: " + sourcePath);
			else {
				sources.add(getPath(sourcePath));
			}
				
		}
		else {
			sources.add(getPath( sourcePath.substring(0,indexOfSpace)));
			sourcePath = sourcePath.substring(indexOfSpace+1);
			int len = sourcePath.length();
			
			for (int i=0 ; i<len ; i++) {
				indexOfSpace = sourcePath.indexOf(" ");
				if (indexOfSpace == -1)
				{
					if (sourcePath.length() != 0) {
						sources.add(sourcePath);
					}
					break;
				}
				String argument = sourcePath.substring(0,indexOfSpace);
				sourcePath = sourcePath.substring(indexOfSpace+1);
				sources.add(getPath(argument));
			}
		}
		for (int i=0 ; i<sources.size();i++)
			sources.set(i, sources.get(i).replace('#', ' '));
		
		if (sources.size() == 1) {
			File file = new File(getPath(destinationPath));
			if (!file.exists()) {
				file.createNewFile();
			}
			@SuppressWarnings("resource")
			FileChannel src = new FileInputStream(getPath(sourcePath)).getChannel();
			@SuppressWarnings("resource")
			FileChannel dest = new FileOutputStream(getPath(destinationPath)).getChannel();
			dest.transferFrom(src, 0, src.size());
			src.close();
			dest.close();
		}
		else {
			File Destination = new File(getPath(destinationPath));
			File src;
			
			if (Destination.isDirectory()) {
				for(int i=0 ; i<sources.size();i++) {
					src = new File(sources.get(i));
					try {
					    FileUtils.copyFileToDirectory(src, Destination);
					} catch (IOException e) {
						System.out.println("Copy failed");
					    e.printStackTrace();
					}
				}
			    System.out.println("Copied Successfully");
			}
			else {
				System.out.println("Target is not directory");
			}
		}
	}
	
	public void more(String FilePath) throws IOException
    {
        String input = null;
        FileReader FileReader = new FileReader(getPath(FilePath));
        BufferedReader bufferedReader = new BufferedReader(FileReader);
        @SuppressWarnings("resource")
		Scanner in = new Scanner(System.in);
        input = bufferedReader.readLine();
        
        int counter = 0;
        while(input != null){
        	System.out.println(input);
        	input = bufferedReader.readLine();
            counter++;
            if (counter % 10 == 0) {
            	in.nextLine();
            }
        }
        bufferedReader.close();

        if (FilePath.contains("lsPipeMore.txt")){
        	File toDelete = new File(getPath(FilePath));
        	toDelete.delete();
        }
    }
	
	public void mkdir(String dir) throws IOException {
		Path path = Paths.get(getPath(dir));
		Files.createDirectories(path);
	}
	
	public void mv(String sourcePath, String destinationPath) throws IOException {
        File f1 = new File (getPath(sourcePath)) ;
        File f2 = new File (getPath(destinationPath)) ;
        
        
        if (sourcePath.contains(".txt") && f2.isDirectory())
        {
           FileUtils.moveFileToDirectory(f1, f2, true);
        }	

        if (f1.isDirectory())
        {	
        	f1.listFiles();
        	if (f1.length() < 0 )	
        		System.out.println("The File is Empty please enter non empty file") ;
	         
        	else {   
        		try {
        			FileUtils.moveDirectoryToDirectory(f1, f2,true);
        		}	catch (IOException e) {
        			System.out.println("Failed to move the file"); 
        		}

        	}
    	}
        if (sourcePath.contains(".txt") && destinationPath.contains(".txt"))
        {  
        	if (f1.isFile() && f2.isFile())
           {
        	  if (f1.length()!=0)
        	  {	  
                 File file = new File(getPath(sourcePath)); 
                 if(file.renameTo(new File(getPath(destinationPath)))) { 
                       file.delete(); 
                       System.out.println("File moved successfully"); 
                 }
        	  }  
        	  else 
                   System.out.println("The File is empty");  
           } 
           else
        	   System.out.println("There is no file to move, enter correct path"); 
        }
	}
	
	public void ls(String Source) {
		File f = new File(getPath(Source)); 
        File[] files = f.listFiles();
        String output = new String();
        if (f.isDirectory()) {
           if (f.length() < 0)
        	   System.out.println("Empty") ;
           else {
        	   for (int i = 0; i < files.length; i++) 
        		   output += (files[i].getName() + "\n");
        	   if (!(output.length()==0))
        		   output = output.substring(0,output.length()-1);
    	       System.out.println(output);
    	   }
        }
        else 
        	System.out.println("There is no directory of that name");
		
	}
	
	public void rmdir(String directoryName) {
		String toDeleteDirectory = getPath(directoryName);
		File folder = new File(toDeleteDirectory);
		if (folder.exists() == false) {
			System.out.println("Directory not found!");
			return;
		}
		else if (folder.isFile()) {
			System.out.println("Use \"rm\" to delete files.");
			return;
		}
		if(folder.list().length>0){
			System.out.println("Error! Directory is not empty!");
		}
		else{
			if (getPath(directoryName).equals( directory)) {
				directory = getPath("..");
			}
			folder.delete();
		}
	}
	
	public void doubleRedirectOperator (String toWrite, String filepath) throws IOException
    {
		BufferedWriter fileAppend = new BufferedWriter(new FileWriter(getPath(filepath), true)); 
		fileAppend.write(toWrite);
		fileAppend.newLine();
		fileAppend.close();
	}
	
	public void singleRedirectOperator(String toWrite, String filepath) throws IOException {
		if (!filepath.contains(".txt")) {
			System.out.println("Not a txt file");
			return;
		}
		File fout = new File(getPath(filepath));
		if (!fout.exists()) {
			fout.createNewFile();
		}
		FileOutputStream fos = new FileOutputStream(fout);
		OutputStreamWriter osw = new OutputStreamWriter(fos);
		osw.write(toWrite) ;
		osw.close();
	}		

	public void rm(ArrayList<String> files) throws IOException{
		for (int i=0 ; i<files.size();i++) {
			File file = new File(getPath(files.get(i)));
			file.delete(); 
		}
	}
	
	public String getPath(String path) {
		String correctPath;
		
		if(path.contains("\"")) {
			path = path.replaceAll("\"", "");
		}
		if(path.equals("..")) {
			String tempDirectory = directory.substring(0,  directory.length()-1);
			for(int i = tempDirectory.length() -1; i>0; i--) {
				if (tempDirectory.charAt(i) == '\\') {
					break;					
				}
				tempDirectory = tempDirectory.substring(0, tempDirectory.length() - 1);
				}
			correctPath = tempDirectory;
		}
		else if (!path.contains(":")) {
			correctPath = directory + path;				
			File file = new File(correctPath);
			if ( file.isDirectory()) {
				correctPath = directory + path + "\\";
			}
		}
		else {			
			correctPath = path;
		}
		if (!(correctPath.charAt(correctPath.length()-1) == '\\')) {
			correctPath = correctPath + "\\";
		}		
		return correctPath;
	}

	public void pwd() {
		System.out.println(directory);
	}
	
	public void cat(ArrayList<String> paths){
		for (int i=0; i<paths.size(); i++) {
			File file = new File(getPath(paths.get(i)));
				Scanner sc;
				try {
					sc = new Scanner(file);
					while (sc.hasNextLine()) {
						String word = sc.nextLine();
						System.out.println(word);
					}
					sc.close();
				} 
				catch (FileNotFoundException e) {
					if (file.isDirectory()) {
						System.out.print(paths.get(i));
						System.out.println(" is a directory");
						continue;
					}
					System.out.print(paths.get(i));
					System.out.println(" is not found");
					continue;
				}
		}
	}

	public static String getDirectory() {
		return directory;
	}
	
	public void args(String command) {
		if (command.equals("cd") || command.equals("ls")){
			System.out.println("arg1: Directory(optional)");
		}
		else if (command.equals("help") || command.equals("pwd")  || command.equals("clear") || command.equals("date") ){
			System.out.println("no arguments");
		}
		else if (command.equals("cp")) {
			System.out.println("arg1: SourceFile, arg2: DestinationFile" + "\nor");		
			System.out.println("arg(n): SourceFile, arg(n+1): DestinationDirectory");			
		}
		else if (command.equals("mv")){
			System.out.println("arg1: SourcePath, arg2: DestinationPath");
		}
		else if (command.equals("rmdir") || command.equals("rm") || command.equals("mkdir") || command.equals("more")){
			System.out.println("arg1: SourcePath");
		}
		else if (command.equals("cat")){
			System.out.println("args(n): SourcePath");
		}
		else if (command.equals("args")){
			System.out.println("arg1: command");
		}
		else{
			System.out.println("Error! Command not found!");
		}
	}

	public void help() {
		System.out.println("[cat]  reads files sequentially, writing them to standard output.");
        System.out.println("[cp] Copy SOURCE to DEST, or multiple SOURCE(s) to DIRECTORY");
        System.out.println("[mv] moves one or more files or directories from one place to another");
        System.out.println("[rm] command used to remove objects such as files, directories");
        System.out.println("[mkdir] is used to make a new directory");
        System.out.println("[rmdir] is used to remove a new directory");
        System.out.println("[more] more is a command to view the contents of a text file one screen at a time");
        System.out.println("[date] used to display the current date");
        System.out.println("[pwd] used to display the current directory");
	}

}


