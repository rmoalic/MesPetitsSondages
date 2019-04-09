package fr.ensibs.sondages.sounder;

import java.util.ArrayList;
import java.util.Scanner;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.Session;

import fr.ensibs.joram.Connector;
import fr.ensibs.joram.Helper;
import fr.ensibs.sondages.questions.*;

public class MainSounder {
	
	private int port;
	private String host;
	private Queue maqueue;
	private CreateSounderImpl createsounder;
	private Poll poll;
	
	private static void usage()
	{
		System.out.println("Usage: java -jar target/MainSounder-1.0.jar <server_host> <server_port>");
		System.out.println("Launch the sounder");
		System.out.println("with:");
		System.out.println("<server_host> the name of the server host");
		System.out.println("<server_port> the number of the server port");
		System.exit(0);
	}

	public static void main(String[] args) {
		if (args.length != 2 || args.equals("-h"))
			usage();
		
		String host = args[0];
		int port = Integer.parseInt(args[1]);
		
		MainSounder instance = new MainSounder(host, port);
	    try {
			instance.run();
		} catch (Exception e) {
			System.out.println("can't run");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public MainSounder(String host, int port) {
		this.host = host;
		this.port = port;
		this.createsounder=new CreateSounderImpl(this.host, this.port);
		this.createsounder.Load();
		this.poll=new Poll(null);
	}

	
	public void run() throws Exception {
		receiveQueue();
		   System.out.println("Enter commands:"
	                + "\n CREATE*<name>                                             to create a new questioner"
	                + "\n CREATEQUESTION*<question>*<name>*<Free/YesNo/Bounded>          to ask a new question"
	                + "\n LISTQUESTION*<name>                                       to obtain all the question of the user"
	                + "\n GETANSWER*<name>                                          to obtain an answer"
	                );
		   
	        Scanner scanner = new Scanner(System.in);
	        String line = scanner.nextLine();
	        while (!line.toLowerCase().equals("quit")) {
	            String[] command = line.split("\\*+");
	            switch (command[0].toUpperCase()) {
	                case "CREATE":{
	                	
	                	if(!this.createsounder.exist(command[1])){
	                		this.createsounder.createSounder(command[1]);
		                    System.out.println("Utilisateur crée");
	                	}
	                	else {System.out.println("Ce nom est déjà utilisé");}
	                    
	                    }
	                    
	                    break;
	                case "CREATEQUESTION": {
	                	
                		if(this.createsounder.exist(command[2])) {
                			
                			switch(command[3].toUpperCase()){
                			case"FREE":{
                				Question q= this.poll.ask(command[1], AnswerFree.class);
                				int id = this.createsounder.getId(command[2]);
                    			this.createsounder.addQuestion(id, q);
                				
                			}
                			break;
                			case"BOUNDED":{
                				Question q= this.poll.ask(command[1], AnswerBounded.class);
                				int id = this.createsounder.getId(command[2]);
                    			this.createsounder.addQuestion(id, q);
                				
                			}	
                			break;
                			case"YESNO":{
                				Question q= this.poll.ask(command[1], AnswerYesNo.class);
                				int id = this.createsounder.getId(command[2]);
                    			this.createsounder.addQuestion(id, q);
                				
                			}
                			break;
                			default:
        	                    System.err.println("Unknown type: "+command[4]);
                			
                    		
                			
                			}
                		
                		}
                	
	                		
	                	
	                }
	                    break;
	                case "LISTQUESTION": {
	                	if (this.createsounder.exist(command[1])) {
	                		 ArrayList<Question> question =this.createsounder.getSounder(this.createsounder.getId(command[1])).getQuestion();
	 	                    System.out.println("questions:");
							for (Question question1 : question) {
								System.out.println("- " + question1.getQuestion());
							}
	                		
	                	}
	                   
	                }
	                    break;
	                case "GETANSWER": {
	                	if(this.createsounder.exist(command[1])){
	                		
	                	}
	                	
	                	
	                	
	                	
	                	
	                	
	                	Sounder s =  this.createsounder.getSounder(this.createsounder.getId(command[1]));
	                	ArrayList<Question> question =s.getQuestion();
	                	for(int i=0; i<question.size();) {
	                		if (question.get(i).getQuestion().contentEquals(command[2])) {
	                			this.createsounder.askAnswer(s.getId(), question.get(i).getID());
	                		}
	                	}
	                	System.out.println("Cette question n'existe pas pour cet utilisateur");
						for (Question question1 : question) {
							System.out.println("- " + question1.getQuestion());
						}
	                    
	                }
	                    break;
	                
	                default:
	                    System.err.println("Unknown command: \"" + command[0] + "\"");
	            }
	            line = scanner.nextLine();
	        }
	        this.createsounder.Save();
	        System.out.println("Exit");
	        System.exit(0);
			
		
		}
	private void receiveQueue() {
		System.setProperty("java.naming.factory.initial", "fr.dyade.aaa.jndi2.client.NamingContextFactory");
		System.setProperty("java.naming.factory.host", this.host);
		System.setProperty("java.naming.factory.port", String.valueOf(this.port));
		
		Session session = Connector.getInstance().createSession();
		
		this.maqueue = Helper.getQueue(session, "response");
		
		Session sessionListener = Connector.getInstance().createSession();
		try {
			MessageConsumer consumer = sessionListener.createConsumer(this.maqueue);
			consumer.setMessageListener(message -> System.out.println(message.toString()));
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
}
