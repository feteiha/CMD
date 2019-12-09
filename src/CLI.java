import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;

public class CLI {
	public static void main(String[] args) throws IOException, InterruptedException {
		Terminal terminal = new Terminal();
		Scanner in = new Scanner(System.in);	
		String user = "user>> ";
		System.out.print(user);
		String input = in.nextLine();
		while (!input.equals("exit")) {
			Parser parser = new Parser();
			if (input.contains("|")) 
				redirect(input,"|");
			
			else if (input.contains(">>")) 
				redirect(input,">>");
			
			else if (input.contains(">")) 
				redirect(input,">");
			
			else if (parser.parse(input,Terminal.getDirectory())) {
				if (parser.getCmd().equals("cp")) {
					ArrayList<String> cpArgs = parser.getArguments();
					String source = new String();
					String destination = new String();
					if (cpArgs.size() == 2) {
						source = cpArgs.get(0);
					}
					else {
						for (int i=0 ; i<cpArgs.size()-1 ; i++){
							cpArgs.set(i, cpArgs.get(i).replace(' ', '#'));
							source += cpArgs.get(i) + " ";
						}
					}
					destination = cpArgs.get(cpArgs.size()-1);
					terminal.cp(source, destination);
				}
				
				else if (parser.getCmd().equals("cd")) {
					if (parser.getArguments().size() == 0) 
						terminal.cd();
					
					else 
						terminal.cd(parser.getArguments().get(0));
				}
				
				else if (parser.getCmd().equals("ls")) {
					if (parser.getArguments().size() == 0) 
						terminal.ls();
					
					else 
						terminal.ls(parser.getArguments().get(0));
				}
				
				else if (parser.getCmd().equals("mv")) 
					terminal.mv(parser.getArguments().get(0), parser.getArguments().get(1));
				
				else if (parser.getCmd().equals("mkdir")) 
					terminal.mkdir(parser.getArguments().get(0));
				
				else if (parser.getCmd().equals("rmdir")) 
					terminal.rmdir(parser.getArguments().get(0));
				
				else if (parser.getCmd().equals("rm")) 
					terminal.rm(parser.getArguments());
				
				else if (parser.getCmd().equals("help")) 
					terminal.help();
				
				else if (parser.getCmd().equals("pwd")) 
					terminal.pwd();
				
				else if (parser.getCmd().equals("cat"))
					terminal.cat(parser.getArguments());
				
				else if (parser.getCmd().equals("more")) 
					terminal.more(parser.getArguments().get(0));
				
				else if (parser.getCmd().equals("args"))
					terminal.args(parser.getArguments().get(0));
				
				else if (parser.getCmd().equals("date")) 
					terminal.date();
				else if (parser.getCmd().equals("clear"))
					terminal.clear();
				
			}
			System.out.print(user);
			input = in.nextLine(); 
		}
		in.close();
	}
	
	public static void redirect(String input , String operator) throws IOException {
		Terminal terminal = new Terminal();
		Parser parserOne = new Parser();
		Parser parserTwo = new Parser();

		int indexOfOperator = input.indexOf(operator);
		String firstCommand = input.substring(0,indexOfOperator);
		input = input.substring(indexOfOperator+1);
		String secondCommand = input.substring(1);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		PrintStream old = System.out;
		System.setOut(ps);
		
		
		
		if(parserOne.parse(firstCommand, Terminal.getDirectory())){
			if (parserOne.getCmd().equals("pwd"))
				terminal.pwd();
			
			else if (parserOne.getCmd().equals("args"))
				terminal.args(parserOne.getArguments().get(0));
			
			else if (parserOne.getCmd().equals("help"))
				terminal.help();
			
			else if (parserOne.getCmd().equals("ls")){
				if (parserOne.getArguments().size() == 0) 
					terminal.ls();
				else 
					terminal.ls(parserOne.getArguments().get(0));
			}
			else if (parserOne.getCmd().equals("cd")){
				if (parserOne.getArguments().size() == 0) 
					terminal.cd();
				else 
					terminal.cd(parserOne.getArguments().get(0));
			}
			else if (parserOne.getCmd().equals("date"))
				terminal.date();	
		}
		
		System.out.flush();
		System.setOut(old);
		String data = new String(baos.toByteArray(), StandardCharsets.UTF_8);
		if (secondCommand.equals("more")) {
			File temp = new File(Terminal.getDirectory()+"lsPipeMore.txt");
			FileOutputStream fos = new FileOutputStream(temp);
			OutputStreamWriter osw = new OutputStreamWriter(fos);
			osw.write(data) ;
			osw.close();
			data = "lsPipeMore.txt";
		}
		if (operator.equals("|")) {
			if(parserTwo.parse(secondCommand+" \""+data+"\"" , Terminal.getDirectory())) {
				if (parserTwo.getCmd().equals("rmdir")) {
					data = data.replace("\r", "");
					data = data.replace("\n", "");
					terminal.rmdir(data);
				}
				else if (parserTwo.getCmd().equals("cd")) {
					terminal.cd(data);
				}
				else if (parserTwo.getCmd().equals("more")) {
					terminal.more("lsPipeMore.txt");
				}
			}
		}
		else if (operator.equals(">")) {
			//overWrite to a file
			terminal.singleRedirectOperator(data, secondCommand);
		}
		else if (operator.equals(">>")) {
			if (secondCommand.charAt(0) == ' ')
				secondCommand = secondCommand.substring(1);
				
			terminal.doubleRedirectOperator(data, secondCommand);
		}
	}

}