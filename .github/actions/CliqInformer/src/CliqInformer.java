import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.WRITE;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
public class CliqInformer {
	public static void main(String args[]) {
		System.out.println("Calling Cliq...");
		HttpURLConnection connection;
		Integer MAX_MESSAGE_LENGTH = 4096;
		String MESSAGE_BREAK = "\\n";
		Integer status = 400;
		boolean MESSAGE_SEND_FAILURE_ERROR = true;
		boolean INVALID_ENDPOINT_ERROR = true;
		boolean GITHUB_ERROR = true;
		String ERROR_MESSAGE = new String("Multiple Errors Occured");
		StringBuffer responseContent = new StringBuffer();
		try {
			String message;
			String CustomMessage;
			String ServerURL = "https://www.github.com/";
			String CliqChannelLink = args[0];
			if(CliqChannelLink.contains("message") && CliqChannelLink.contains("https://cliq.zoho") && CliqChannelLink.contains("/api/v2/") && CliqChannelLink.contains("?zapikey="))
			  INVALID_ENDPOINT_ERROR = false;
			CustomMessage = args[1];
			String[] AddedInfo = args[2].split("_\\+_");
			String Repository = AddedInfo[0];
			String RepositoryURL = ServerURL + Repository ;
			String Event = AddedInfo[1];
			String Actor = AddedInfo[3];
			String ActorURL = ServerURL + Actor;
			String[] EventWords = Event.split("_");
			Event = new String();
			for(String s: EventWords)
			  Event += s.substring(0,1).toUpperCase() + s.substring(1) + " ";
			Event = Event.trim();
			String Action = AddedInfo[2];
			if(!Action.equals(""))
			{
			  String[] ActionWords = Action.split("_");
			  Action = new String();
			  for(String s: ActionWords)
			    Action += s + " ";
			  Action = Action.trim();
			}
			if(Action.equals(""))
        		Action = "made";
			String CliqInformerURL = "https://workdrive.zohoexternal.com/external/047d96f793983933bbdb59deb9c44f5443b83a7188e278736405d4d733923181/download?directDownload=true";
			message = CustomMessage;
			if(CustomMessage.equals(""))
			{
				message = new String();
				if(Event.equals("Branch Protection Rule"))
				{
					String Branch_Manager = AddedInfo[3];
					String Rule = AddedInfo[4];
					String RuleID = AddedInfo[5];
					if(Action.equals("created"))
					{
						message = "[" + Branch_Manager + "](" + ServerURL + Branch_Manager + ") has created a new branch protection rule - [" + Rule + "](" + RepositoryURL + "/settings/branch_protection_rules/" + RuleID + ")";
					}
					else if(Action.equals("deleted"))
					{
						message = "[" + Branch_Manager + "](" + ServerURL + Branch_Manager + ") has deleted an existing branch protection rule";
					}
					else if(Action.equals("edited"))
					{
						message = "[" + Branch_Manager + "](" + ServerURL + Branch_Manager + ") has edited an existing branch protection rule - [" + Rule + "](" + RepositoryURL + "/settings/branch_protection_rules/" + RuleID + ")";
					}
					message = message + " " + RepositoryURL;
				}
				else if(Event.equals("Check Run"))
				{
					String Checker = AddedInfo[3];
					String CheckName = AddedInfo[4];
					String ChecksURL = AddedInfo[5];
					if(Action.equals("created"))
					{
						message = "[" + Checker + "](" + ServerURL + Checker + ") has created a new check run - [" + CheckName + "](" + ChecksURL + ")";
					}
					else if(Action.equals("completed"))
					{
						message = "The check run [" + CheckName + "](" + ChecksURL + ") created by [" + Checker + "](" + ServerURL + Checker + ") has been completed";
					}
					message = message + " " + ChecksURL;
				}
				else if(Event.equals("Check Suite"))
				{
					String CheckSuiter = AddedInfo[3];
					message = "The check suite created by [" + CheckSuiter + "](" + ServerURL + CheckSuiter + ") has been completed";
					message = message + " " + RepositoryURL;
				}
				else if(Event.equals("Create"))
				{
					String Creator = AddedInfo[3];
					String Ref = AddedInfo[4];
					String RefType = AddedInfo[5];
 					message = "[" + Creator + "](" + ServerURL + Creator + ") has created a new " + RefType + " - [" + Ref + "](" + ServerURL + Repository + "/tree/" + Ref + ")";
 					message = message + " " + RepositoryURL;
				}
				else if(Event.equals("Delete"))
				{
					String Deletor = AddedInfo[3];
					String Ref = AddedInfo[4];
					String RefType = AddedInfo[5];
					message = "[" + Deletor + "](" + ServerURL + Deletor + ") has deleted the " + RefType + " - " + Ref;
					message = message + " " + RepositoryURL;
				}
				else if(Event.equals("Deployment"))
				{
					String Deployer = AddedInfo[3];
					String DeploymentURL = AddedInfo[4];
					message = "A new deployment has been created for the repository - [" + Repository + "](" + RepositoryURL + ")";
					message = message + " " + DeploymentURL;
				}
				else if(Event.equals("Deployment Status"))
				{
					String Deployer = AddedInfo[3];
					String DeploymentURL = AddedInfo[4];
					String Status = AddedInfo[5];
					message = "The status of the deployment [" + DeploymentURL + "](" + DeploymentURL + ") associated with the [" + Repository + "](" + RepositoryURL + ") repository has been changed to " + Status;
					message = message + " " + DeploymentURL;
				}
				else if(Event.equals("Discussion"))
				{
					String Discusser = AddedInfo[3];
					String Discussion = AddedInfo[4];
					String DiscussionURL = AddedInfo[5];
					if(Action.equals("created"))
					{
						message = "[" + Discusser + "](" + ServerURL + Discusser + ") has created a new discussion - [" + Discussion + "](" + DiscussionURL + ")";
					}
					else if(Action.equals("deleted"))
					{
						message = "[" + Discusser + "](" + ServerURL + Discusser + ") has deleted the discussion - [" + Discussion + "](" + DiscussionURL + ")"; 
					}
					else if(Action.equals("edited"))
					{
						message = "[" + Discusser + "](" + ServerURL + Discusser + ") has edited the discussion - [" + Discussion + "](" + DiscussionURL + ")";
					}
					else if(Action.equals("pinned"))
					{
						message = "[" + Discusser + "](" + ServerURL + Discusser + ") has pinned the discussion - [" + Discussion + "](" + DiscussionURL + ")";
					}
					else if(Action.equals("unpinned"))
					{
						message = "[" + Discusser + "](" + ServerURL + Discusser + ") has unpinned the discussion - [" + Discussion + "](" + DiscussionURL + ")";
					}
					else if(Action.equals("labelled"))
					{
						String LabelName = AddedInfo[6];
						message = "[" + Discusser + "](" + ServerURL + Discusser + ") has labelled the discussion [" + Discussion + "](" + DiscussionURL + "] as [" + LabelName + "](" + RepositoryURL+ "/discussions?discussions_q=label%3A" + LabelName + ")";
					}
					else if(Action.equals("unlabelled"))
					{
						String LabelName = AddedInfo[6];
						message = "[" + Discusser + "](" + ServerURL + Discusser + ") has removed the discussion [" + Discussion + "](" + DiscussionURL + "] from the label [" + LabelName + "](" + RepositoryURL+ "/discussions?discussions_q=label%3A" + LabelName + ")";
					}
					else if(Action.equals("locked"))
					{
						message = "[" + Discusser + "](" + ServerURL + Discusser + ") has locked the discussion - [" + Discussion + "](" + DiscussionURL + ")";
					}
					else if(Action.equals("unlocked"))
					{
						message = "[" + Discusser + "](" + ServerURL + Discusser + ") has unlocked the discussion - [" + Discussion + "](" + DiscussionURL + ")";
					}
					else if(Action.equals("transferred"))
					{
						String NewRepository = AddedInfo[6];
						String NewRepositoryURL = ServerURL + NewRepository;
						message = "[" + Discusser + "](" + ServerURL + Discusser + ") has transfered the discussion [" + Discussion + "](" + DiscussionURL + "] from [" + Repository + "](" + RepositoryURL + ") to [" + NewRepository + "](" + NewRepositoryURL + ")";
					}
					else if(Action.equals("answered"))
					{
						message = "[" + Discusser + "](" + ServerURL + Discusser + ") has added an answer to the discussion - [" + Discussion + "](" + DiscussionURL + ")";
					}
					else if(Action.equals("unanswered"))
					{
						message = "[" + Discusser + "](" + ServerURL + Discusser + ") has unmarked an answer from the discussion - [" + Discussion + "](" + DiscussionURL + ")";
					}
					else if(Action.equals("category changed"))
					{
						String CategoryName = AddedInfo[6];
						message = "[" + Discusser + "](" + ServerURL + Discusser + ") has changed and added the discussion [" + Discussion + "](" + DiscussionURL + "] under the [" + CategoryName + "](" + RepositoryURL + "/discussions/categories/" + CategoryName + ") category";
					}
					message = message + " " + DiscussionURL;
				}
				else if(Event.equals("Push"))
				{
					String Pusher = AddedInfo[3];
					String Branch_Name = AddedInfo[4];
					String Branch_Type = AddedInfo[5];
					String Commit_URL = AddedInfo[6];
					String Compare_URL = AddedInfo[7];
					message ="[" + Pusher + "](" + ServerURL + Pusher + ") has pushed a new [code](" + Commit_URL + ") in the " + Branch_Type + " [" + Branch_Name + "](" + ServerURL + Repository + "/tree/" + Branch_Name + ")";
					message = message + " " + Compare_URL;
				}
				else if(Event.equals("Registry Package"))
				{
					String Publisher = AddedInfo[3];
					String RegistryPackageName = AddedInfo[4];
					String RegistryPackageVersion = AddedInfo[5];
					String RegistryPackageType = AddedInfo[6];
					String RegistryPackageURL = AddedInfo[7];
					if(Action.equals("published"))
					{
						message = "[" + Publisher + "](" + ServerURL + Publisher + ") has published a new " + RegistryPackageType + " registry package [" + RegistryPackageName + " " + RegistryPackageVersion + "](" + RegistryPackageURL + ")";
					}
					message = message + " " + RegistryPackageURL;
				}
				else if(Event.equals("Release"))
				{
					String Releaser = AddedInfo[3];
					String ReleaseName = AddedInfo[4];
					String ReleaseTagName = AddedInfo[5];
					String ReleaseURL = AddedInfo[6];
					if(Action.equals("published"))
					{
						message = "[" + Releaser + "](" + ServerURL + Releaser + ") has published a new release - [" + ReleaseName + " " + ReleaseTagName + "](" + ReleaseURL + ")";
					}
					else if(Action.equals("created"))
					{
						message = "[" + Releaser + "](" + ServerURL + Releaser + ") has created a new release - [" + ReleaseName + " " + ReleaseTagName + "](" + ReleaseURL + ")";
					}
					else if(Action.equals("prereleased"))
					{
						message = "[" + Releaser + "](" + ServerURL + Releaser + ") has moved [" + ReleaseName + " " + ReleaseTagName + "](" + ReleaseURL + ") to the prerelease stage";
					}
					else if(Action.equals("released"))
					{
						message = "[" + Releaser + "](" + ServerURL + Releaser + ") has released [" + ReleaseName + " " + ReleaseTagName + "](" + ReleaseURL + ")";
					}
					else if(Action.equals("edited"))
					{
						message = "[" + Releaser + "](" + ServerURL + Releaser + ") has edited and made changes to the release [" + ReleaseName + " " + ReleaseTagName + "](" + ReleaseURL + ")";
					}
					else if(Action.equals("deleted"))
					{
						message = "[" + Releaser + "](" + ServerURL + Releaser + ") has deleted a release [" + ReleaseName + " " + ReleaseTagName + "](" + ReleaseURL + ")";
					}
					message = message + " " + ReleaseURL;
				}
				else if(Event.equals("Repository Dispatch"))
				{
					String Trigger_Actor = AddedInfo[3];
					String WorkflowID = AddedInfo[4];
					String WorkflowURL = ServerURL + Repository + "/actions/runs/" + WorkflowID;
					message = "[" + Trigger_Actor + "](" + ServerURL + Trigger_Actor + ") has triggered a new repository dispatch - [" + Action + "](" + WorkflowURL + ")";
					message = message + " " + RepositoryURL;
				}
				else if(Event.equals("Schedule"))
				{
					String Trigger_Actor = AddedInfo[3];
					String Workflow = AddedInfo[4];
					String WorkflowID = AddedInfo[5];
					String WorkflowURL = RepositoryURL + "/actions/runs/" + WorkflowID;
					message = "[" + Trigger_Actor + "](" + ServerURL + Trigger_Actor + ") has scheduled a workflow [" + Workflow + "](" + WorkflowURL  + ")";
					message = message + " " + WorkflowURL;
				}
				else if(Event.equals("Status"))
				{
					String Trigger_Actor = AddedInfo[3];
					String Workflow = AddedInfo[4];
					String WorkflowID = AddedInfo[5];
					String Status = Workflow;
					String WorkflowURL = RepositoryURL + "/actions/runs/" + WorkflowID;
					message = "The status of the [" + Workflow + "](" + WorkflowURL + ") workflow has been updated as " + Status;
					message = message + RepositoryURL;
				}
				else if(Event.equals("Watch"))
				{
					String Watcher = AddedInfo[3];
					message = "[" + Watcher + "](" + ServerURL + Watcher + ") has pushed the [" + Repository + "](" + RepositoryURL + ") repository under the Watch category";
					message = message + " " + RepositoryURL;
				}
				else if(Event.equals("Workflow Dispatch"))
				{
					String Dispatcher = AddedInfo[3];
					String Workflow = AddedInfo[4];
					String WorkflowID = AddedInfo[5];
					String WorkflowURL = RepositoryURL + "/actions/runs/" + WorkflowID;
					message = "[" + Dispatcher + "](" + ServerURL + Dispatcher + ") has triggered the [" + Workflow + "](" + WorkflowURL  + ") workflow"; 
					message = message + " " + WorkflowURL;
				}
			}
			else
			{
				message = message.replace("(me)","[" + Actor + "](" + ActorURL + ")");
				message = message.replace("(repo)","[" + Repository + "](" + RepositoryURL + ")" );
				message = message.replace("(event)","*" + Event + "*");
				message = message.replace("(action)",Action);
			}
			ArrayList<String> messages = new ArrayList<String>();
			for(int i = 0 ; i < message.length() ;)
			{
			  String split_message;
			  if(i+MAX_MESSAGE_LENGTH < message.length())
			  {
			    split_message = message.substring(i,i+MAX_MESSAGE_LENGTH);
			    int displaced_length = MAX_MESSAGE_LENGTH;
			    if(split_message.contains(MESSAGE_BREAK))
			    {
			      displaced_length = split_message.lastIndexOf(MESSAGE_BREAK) + 2;
			      split_message = message.substring(i,i+displaced_length);
			      split_message = split_message.replaceAll("\\\\n","");
			    }
			    else if(split_message.contains("\n"))
			    {
			      displaced_length = split_message.lastIndexOf("\n") + 1;
			      split_message = message.substring(i,i+displaced_length);
			    }
			    else if(split_message.contains("."))
			    {
			      displaced_length = split_message.lastIndexOf(".") + 1;
			      split_message = message.substring(i,i+displaced_length);
			    }
			    i += displaced_length;
			  }
			  else
			  {
			    split_message = message.substring(i,message.length());
			    i+= MAX_MESSAGE_LENGTH;
			  }
			  messages.add(split_message);
			}
			for(String msg : messages)
			{
			  msg = msg.replace("\"","'");
			  String TextParams = "{\n\"text\":\"" + msg + "\",\n\"bot\":\n{\n\"name\":\"CliqInformer\",\n\"image\":\"" + CliqInformerURL + "\"}}";
			  connection = (HttpURLConnection) new URL(CliqChannelLink).openConnection();
			  connection.setRequestMethod("POST");
			  connection.setRequestProperty("Content-Type","application/json");
			  connection.setDoOutput(true);
			  OutputStream os = connection.getOutputStream();
			  os.write(TextParams.getBytes());
			  os.flush();
			  os.close();
			  System.out.println(TextParams);
			  status = connection.getResponseCode();
			  if(status > 299) {
				  BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
				  String line;
				  while((line = reader.readLine()) != null) {
					  responseContent.append(line);
				  }
			    reader.close();
			  }
			  else
			  {
				  BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				  String line;
				  while((line = reader.readLine()) != null) {
					  responseContent.append(line);
				  }
				  reader.close();
			  }
			  if(status != 204)
			    ERROR_MESSAGE = responseContent.toString();
			}
			/*String ServerURL = args[3];
			String Workflow = args[5];
			String Actor = args[6];
			String RunId = args[7];
			String Ref = args[8];
			String RefType = args[9];
			String ActorURL = ServerURL + "/" + Actor;
			String RepositoryURL = ServerURL + "/" + Repository;
			String WorkflowURL = RepositoryURL + "/actions/runs/" + RunId;
			String RefURL = RepositoryURL;
			if(Ref.contains("pull"))
			  RefURL = RefURL + "/pull/";
			else
			  RefURL = RefURL + "/tree/";
			if(Ref.split("/").length > 2)
			  Ref = Ref.split("/")[2];
			RefURL = RefURL + Ref;
			CustomMessage = args[10];
			message = message.replace("(me)","[" + Actor + "](" + ActorURL + ")");
			message = message.replace("(workflow)","[" + Workflow + "](" + WorkflowURL + ")" );
			message = message.replace("(repo)","[" + Repository + "](" + RepositoryURL + ")" );
			message = message.replace("(event)","*" + Event + "*");
			message = message.replace("(action)",Action);
			message = message.replace("(ref)",RefType + " [" + Ref + "](" + RefURL + ")" );
			if(message.length() > MAX_MESSAGE_LENGTH)
			{
			  message = CustomMessage;
			  message = message.replace("(me)","*" + Actor + "*");
			  message = message.replace("(workflow)","*" + Workflow + "*" );
			  message = message.replace("(repo)","*" + Repository + "*" );
			  message = message.replace("(event)","*" + Event + "*");
			  message = message.replace("(action)",Action);
			  message = message.replace("(ref)",RefType + " *" + Ref + "*" );
			}*/
			var githubOutput = System.getenv("GITHUB_OUTPUT");
			if(Objects.nonNull(githubOutput))
			    GITHUB_ERROR = false;
			if(status == 204)
			  MESSAGE_SEND_FAILURE_ERROR = false;
			if(INVALID_ENDPOINT_ERROR)
			  ERROR_MESSAGE = "Invalid Endpoint. Endpoint must be of format : <Zoho Cliq Channel API Endpoint>?zapikey=<Zoho Cliq Webhook Token>";
			else if(GITHUB_ERROR)
			  ERROR_MESSAGE = "Environmental Variable GITHUB_OUTPUT missing";
			else if(MESSAGE_SEND_FAILURE_ERROR)
			  ERROR_MESSAGE = ERROR_MESSAGE;
			else if(status == 204)
			  ERROR_MESSAGE = "CliqInformer executed Successfully";
			writeGithubOutput(status,ERROR_MESSAGE);
		}  catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally
		{
		  try
		  {
		    var githubOutput = System.getenv("GITHUB_OUTPUT");
		    var file = Path.of(githubOutput);
		    if(file.getParent() != null) Files.createDirectories(file.getParent());
		    if(MESSAGE_SEND_FAILURE_ERROR)
		    {
		      ERROR_MESSAGE = "Unknown Error Occured : " + ERROR_MESSAGE;
		    }
		    writeGithubOutput(status,ERROR_MESSAGE);
		  }
		  catch(Exception e)
		  {
		    ERROR_MESSAGE = "Sorry we couldn't process your request due to a technical error. Please Try again later.";
		    System.err.println("Unknown Error Occured : " + ERROR_MESSAGE);
		    System.exit(1);
		  }
		}
	}
	
	// To Split and Seperate the Message from the JSON
	public static String splitMessage(String JSON)
	{
	  JSON = JSON.substring(JSON.indexOf("{"), JSON.indexOf("}"));
	  String[] JSONArray = JSON.split(",");
	  for(String s : JSONArray)
	    if(s.contains("\"message\":"))
	      return s.substring(s.indexOf(":")+1,s.length());
	  return "Error Description not Provided";
	}
	
	// used to write a Github Output so that the Shell Runner can Read
	public static void writeGithubOutput(Integer Status , String ErrorMessage) throws IOException
	{
		Map env = System.getenv();
		for(String key : env.keySet)
			System.out.println(key + env.get("key"));
	  var githubOutput = System.getenv("GITHUB_OUTPUT");
    var file = Path.of(githubOutput);
	  var lines = ("message-status=" + Status).lines().toList();
		Files.write(file, lines, UTF_8 , CREATE , APPEND , WRITE);
		lines = ("error-message=" + ErrorMessage).lines().toList();
		Files.write(file, lines, UTF_8 , CREATE , APPEND , WRITE);
		System.out.println("Message - Status : " + Status);
	}
}
