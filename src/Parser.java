import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Parser {
	ArrayList<String> args; // Will be filled by arguments extracted by parse method
	String cmd; // Will be filled by the command extracted by parse method
	String directory;

	public boolean parse(String input, String directory) throws IOException {
		this.directory = directory;
		args = new ArrayList<String>();
		if (input.contains("|")) {
			return ParsePipe(input);
		}
		int indexOfSpace = input.indexOf(" ");
		if (indexOfSpace == -1 && checkCommand(input)) {
			cmd = input;
		}
		else {
			cmd = input.substring(0,indexOfSpace);
			input = input.substring(indexOfSpace+1);
			int len = input.length();
			
			for (int i=0 ; i<len ; i++) {
				indexOfSpace = input.indexOf(" ");
				if (indexOfSpace == -1)
				{
					if (input.length() != 0) {
						args.add(input);
					}
					break;
				}
				String argument = input.substring(0,indexOfSpace);
				input = input.substring(indexOfSpace+1);
				if (argument.charAt(0) == '"')
				{
					argument = argument.replaceAll("\"", "");
					int indexOfSecondQuote = input.indexOf('"');
					argument += " " + input.substring(0,indexOfSecondQuote);
					input = input.substring(indexOfSecondQuote+1);
					if (input.length() != 0 && input.charAt(0) == ' ' )
						input = input.substring(1);
				}
				args.add(argument);
			}
		}
		if (checkCommand(cmd)==false) return false;
		if (cmd.equals("cp")) return ParseCP();
		else if (cmd.equals("mkdir") || cmd.equals("rmdir")){
			return ParseDir();
		}
		else if (cmd.equals("mv")) {
			return ParseMove();
		}
		else if (cmd.equals("cd") || cmd.equals("ls")) {
			if (args.size() > 1) {
				return false;
			}
			return true;
		}
		else if (cmd.equals("cat")) {
			if (args.size()==0) {
				System.out.println("Needs at least one path!");
				return false;
			}
			return true;
				
		}
		else if (cmd.equals("args")) {
			boolean parseArgsReturn = ParseArgs();
			if (parseArgsReturn) {
				return true;
			}
			else {
				System.out.println("Error, command not found!");
				return false;
			}
		}
		else if (cmd.equals("pwd") || cmd.equals("clear") || cmd.equals("date") || cmd.equals("help")) {
			return ParseNoArg();
		}
		else if (cmd.equals("rm")) {
			return ParseRm();
		}
		else if (cmd.equals("more")) {
			return ParseMore(); 
		}
		return false;
	}
	
	private boolean ParseDir() throws IOException {
		if (args.size()==1)
			return true;
		return false;
		
	}
	
	private boolean ParseMove() 
	{
	  if (args.size() < 2 || args.size() > 2 )	{		  
		 return false ;
	  }
	  else if (args.size() == 2)
	  {	  
		File file = new File(getPath(args.get(0)));
		File file2 = new File(getPath(args.get(1)));
		if ((file.isFile() || file.isDirectory()) && file2.isDirectory())
			return true ;
		else {
			System.out.println("File not found");
			return false ;
		}
	  }
	 return true ;
	}
	
	private boolean ParseMore()
    {
		if (args.size()==1){
			File file = new File(getPath(args.get(0)));
			if (!file.isFile() || !file.exists()){
				System.out.println("File not found");
				return false ;
			}
			return true;
		}
        return false;
    }
	
	private boolean ParseNoArg() throws IOException {
		if (args.size()>0){
			System.out.println("Doesn't take arguments.");
			return false;
		}
		return true;
	}
	
	private boolean ParseArgs() {
		if (args.size() == 1)
			return checkCommand(args.get(0));
		return false;
	}
	
	private boolean ParseCP() {
		if (args.size()>2) {
			for (int i=0 ; i<args.size()-1;i++) {
				File file = new File(getPath(args.get(i)));
				if (!file.isFile()){
					System.out.println("No file named " + file.getName());
					return false;
				}
			}
			File file = new File(getPath(args.get(args.size()-1)));
			if (!file.isDirectory()){
				System.out.println("Target is not directory");
				return false;
			}
		}
		else if (args.size() == 2) {
			File file = new File(getPath(args.get(0)));
			boolean f1 = file.isFile();
			if (!f1){
				System.out.println("No file named " + file.getName());
				return false;
			}
		}
		else
			return true;
		return true;
	}
	
	private boolean ParseRm() {
		for (int i=0 ; i<args.size() ; i++){
			File file = new File(getPath(args.get(i)));
			if (!file.isFile()) {
				System.out.println(file.getName() + " file not exists");
				return false;
			}
		}
		return true;
	}
	
	private boolean ParsePipe(String input) {
		int indexOfPipe = input.indexOf("|");
		String firstCommand = input.substring(0,indexOfPipe);
		input = input.substring(indexOfPipe+1);
		String secondCommand = input;
		String Cmd_1 = firstCommand.substring(0,firstCommand.indexOf(" "));
		String Cmd_2 = secondCommand.substring(0,secondCommand.indexOf(" "));
		if (!checkCommand(Cmd_1) || !checkCommand(Cmd_2)) {
			return false;
		}
		return true;
	}
	
	private boolean checkCommand(String command) {
		if (command.equals("cp")      || command.equals("cd")    || command.equals("ls")   ||
			command.equals("cat")     || command.equals("more")  ||command.equals("mkdir") || 
			command.equals("rmdir")   || command.equals("mv")    ||command.equals("rm")    ||
			command.equals("date")    || command.equals("help")  ||command.equals("pwd")   ||
			command.equals("clear")   || command.equals("args")) {
			return true;
		}
		else
			return false;
	}
	
	public String getPath(String path) {
		String correctPath;
		
		if(path.contains("\"")) {
			path = path.replaceAll("\"", "");
		}
		
		if(path == "..") {
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
		return correctPath;
	}
	
	
	public String getCmd() {
		return cmd;
	}
	
	public ArrayList<String> getArguments() {
		return args;
	};
}
