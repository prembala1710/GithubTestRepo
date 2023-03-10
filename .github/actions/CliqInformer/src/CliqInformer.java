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
			CustomMessage = (String) System.getenv("CUSTOM_MESSAGE");
			String Actor = (String) System.getenv("GITHUB_ACTOR");
			String ActorURL = ServerURL + Actor;
			String Event = (String) System.getenv("GITHUB_EVENT_NAME");
			String[] EventWords = Event.split("_");
			String Repository = (String) System.getenv("GITHUB_REPOSITORY");
			String RepositoryURL = ServerURL + Repository;
			Event = new String();
			for(String s: EventWords)
			  Event += s.substring(0,1).toUpperCase() + s.substring(1) + " ";
			Event = Event.trim();
			String Action = (String) System.getenv("ACTION");
			if(!Action.equals("") || Action != null)
			{
			  String[] ActionWords = Action.split("_");
			  Action = new String();
			  for(String s: ActionWords)
			    Action += s + " ";
			  Action = Action.trim();
			}
			else
			{
				Action = "made";
			}
			System.out.println(Actor + " " + CustomMessage + " " + Event + Repository);
			System.out.println(System.getenv("GITHUB_ACTOR") + " " + System.getenv("CUSTOM_MESSAGE") + " " + System.getenv("GITHUB_EVENT_NAME") + " " + System.getenv("GITHUB_REPOSITORY") + System.getenv("ACTION"));
			String CliqInformerURL = "https://workdrive.zohoexternal.com/external/047d96f793983933bbdb59deb9c44f5443b83a7188e278736405d4d733923181/download?directDownload=true";
			message = CustomMessage;
			if(CustomMessage != null)
			{
				if(CustomMessage.equals("_+_"))
				{
					message = new String();
					if(Event.equals("Branch Protection Rule"))
					{
						String Branch_Manager = (String) System.getenv("GITHUB_ACTOR");
						String Rule = (String) System.getenv("BRANCH_RULE");
						String RuleID = (String) System.getenv("BRANCH_RULE_ID");
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
						message = message + " \\n" + RepositoryURL;
					}
					else if(Event.equals("Check Run"))
					{
						String Checker = (String) System.getenv("GITHUB_ACTOR");
						String CheckName = (String) System.getenv("CHECK_RUN_NAME");
						String ChecksURL = (String) System.getenv("CHECK_RUN_URL");
						if(Action.equals("created"))
						{
							message = "[" + Checker + "](" + ServerURL + Checker + ") has created a new check run - [" + CheckName + "](" + ChecksURL + ")";
						}
						else if(Action.equals("completed"))
						{
							message = "The check run [" + CheckName + "](" + ChecksURL + ") created by [" + Checker + "](" + ServerURL + Checker + ") has been completed";
						}
						message = message + " \\n" + ChecksURL;
					}
					else if(Event.equals("Check Suite"))
					{
						String CheckSuiter = (String) System.getenv("GITHUB_ACTOR");
						message = "The check suite created by [" + CheckSuiter + "](" + ServerURL + CheckSuiter + ") has been completed";
						message = message + " \\n" + RepositoryURL;
					}
					else if(Event.equals("Create"))
					{
						String Creator = (String) System.getenv("GITHUB_ACTOR");
						String Ref = (String) System.getenv("BRANCH_NAME");
						String RefType = (String) System.getenv("BRANCH_TYPE");
	 					message = "[" + Creator + "](" + ServerURL + Creator + ") has created a new " + RefType + " - [" + Ref + "](" + ServerURL + Repository + "/tree/" + Ref + ")";
	 					message = message + " \\n" + RepositoryURL;
					}
					else if(Event.equals("Delete"))
					{
						String Deletor = (String) System.getenv("GITHUB_ACTOR");
						String Ref = (String) System.getenv("BRANCH_NAME");
						String RefType = (String) System.getenv("BRANCH_TYPE");
						message = "[" + Deletor + "](" + ServerURL + Deletor + ") has deleted the " + RefType + " - " + Ref;
						message = message + " \\n" + RepositoryURL;
					}
					else if(Event.equals("Deployment"))
					{
						String Deployer = (String) System.getenv("GITHUB_ACTOR");
						String DeploymentURL = (String) System.getenv("DEPLOYMENT_URL");
						message = "A new deployment has been created for the repository - [" + Repository + "](" + RepositoryURL + ")";
						message = message + " \\n" + DeploymentURL;
					}
					else if(Event.equals("Deployment Status"))
					{
						String Deployer = (String) System.getenv("GITHUB_ACTOR");
						String DeploymentURL = (String) System.getenv("DEPLOYMENT_URL");
						String Status = (String) System.getenv("STATUS");
						message = "The status of the deployment [" + DeploymentURL + "](" + DeploymentURL + ") associated with the [" + Repository + "](" + RepositoryURL + ") repository has been changed to " + Status;
						message = message + " \\n" + DeploymentURL;
					}
					else if(Event.equals("Discussion"))
					{
						String Discusser = (String) System.getenv("GITHUB_ACTOR");
						String Discussion = (String) System.getenv("DISCUSSION");
						String DiscussionURL = (String) System.getenv("DISCUSSION_URL");
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
							String LabelName = (String) System.getenv("LABEL_NAME");
							message = "[" + Discusser + "](" + ServerURL + Discusser + ") has labelled the discussion [" + Discussion + "](" + DiscussionURL + "] as [" + LabelName + "](" + RepositoryURL+ "/discussions?discussions_q=label%3A" + LabelName + ")";
						}
						else if(Action.equals("unlabelled"))
						{
							String LabelName = (String) System.getenv("LABEL_NAME");
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
							String NewRepository = (String) System.getenv("NEW_REPOSITORY");
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
							String CategoryName = (String) System.getenv("CATEGORY_NAME");
							message = "[" + Discusser + "](" + ServerURL + Discusser + ") has changed and added the discussion [" + Discussion + "](" + DiscussionURL + "] under the [" + CategoryName + "](" + RepositoryURL + "/discussions/categories/" + CategoryName + ") category";
						}
						message = message + " \\n" + DiscussionURL;
					}
					else if(Event.equals("Discussion Comment"))
					{
						String Discusser = (String) System.getenv("GITHUB_ACTOR");
						String DiscussionTitle = (String) System.getenv("DISCUSSION_TITLE");
						String DiscussionComment = (String) System.getenv("DISCUSSION_COMMENT");
						String DiscussionURL = (String) System.getenv("DISCUSSION_URL");
						String CommentURL = (String) System.getenv("COMMENT_URL");
						if(Action.equals("created"))
						{
							message = "[" + Discusser + "](" + ServerURL + Discusser + ") has added a new [comment](" + CommentURL + ") to the discussion - [" + DiscussionTitle + "](" + DiscussionURL + ")\\n" + DiscussionComment ;
						}
						else if(Action.equals("edited"))
						{
							message = "[" + Discusser + "](" + ServerURL + Discusser + ") has edited a [comment](" + CommentURL + ") attached to the discussion - [" + DiscussionTitle + "](" + DiscussionURL + ")";
						}
						else if(Action.equals("deleted"))
						{
							message = "[" + Discusser + "](" + ServerURL + Discusser + ") has deleted a [comment](" + CommentURL + ") attached with the discussion - [" + DiscussionTitle + "](" + DiscussionURL + ")";
						}
						message = message + " \\n" + CommentURL;
					}
					else if(Event.equals("Fork"))
					{
						String Forker = (String) System.getenv("GITHUB_ACTOR");
						String Forkee = (String) System.getenv("NEW_REPOSITORY");
						String RepoOwner = (String) System.getenv("GITHUB_REPO_OWNER");
						String ForkerURL = ServerURL + Forker;
						String RepoOwnerURL = ServerURL + RepoOwner;
						String ForkeeURL = ServerURL + Forkee;
						message = "[" + Forker + "](" + ForkerURL + ") has forked [" + RepoOwner + "](" + RepoOwnerURL + ")'s [" + Repository + "](" + RepositoryURL + ") repository to [" + Actor + "](" + ActorURL + ")'s [" + Forkee + "](" + ForkeeURL + ") repository";
						message = message + " \\n" + ForkeeURL;
					}
					else if(Event.equals("Gollum"))
					{
						String PageHandler = (String) System.getenv("GITHUB_ACTOR");
						message = "A few changes has been made to the Wiki pages of [" + Repository + "](" + RepositoryURL + ") by " + "[" + PageHandler + "](" + ServerURL + PageHandler + ")";
					}
					else if(Event.equals("Page Build"))
					{
						String PageBuilder = (String) System.getenv("GITHUB_ACTOR");
						message = "A new page build has been created for the repository - [" + Repository + "](" + RepositoryURL + ") by " + "[" + PageBuilder + "](" + ServerURL + PageBuilder + ")";
					}
					else if(Event.equals("Public"))
					{
						String Publicizer = (String) System.getenv("GITHUB_ACTOR");
						message = "The [" + Repository + "](" + RepositoryURL + ") repository has been made public by [" + Publicizer + "](" + ServerURL + Publicizer + ")";
					}
					else if(Event.equals("Push"))
					{
						String Pusher = (String) System.getenv("GITHUB_ACTOR");
						String Branch_Name = (String) System.getenv("GITHUB_REF_NAME");
						String Branch_Type = (String) System.getenv("GITHUB_REF_TYPE");
						String Commit_URL = (String) System.getenv("COMMIT_URL");
						String Compare_URL = (String) System.getenv("COMPARE_URL");
						message ="[" + Pusher + "](" + ServerURL + Pusher + ") has pushed a new [code](" + Commit_URL + ") in the " + Branch_Type + " [" + Branch_Name + "](" + ServerURL + Repository + "/tree/" + Branch_Name + ")";
						message = message + " \\n" + Compare_URL;
					}
					else if(Event.equals("Registry Package"))
					{
						String Publisher = (String) System.getenv("GITHUB_ACTOR");
						String RegistryPackageName = (String) System.getenv("REGISTRY_PACKAGE_NAME");
						String RegistryPackageVersion = (String) System.getenv("REGISTRY_PACKAGE_VERSION");
						String RegistryPackageType = (String) System.getenv("REGISTRY_PACKAGE_TYPE");
						String RegistryPackageURL = (String) System.getenv("REGISTRY_PACKAGE_URL");
						if(Action.equals("published"))
						{
							message = "[" + Publisher + "](" + ServerURL + Publisher + ") has published a new " + RegistryPackageType + " registry package [" + RegistryPackageName + " " + RegistryPackageVersion + "](" + RegistryPackageURL + ")";
						}
						message = message + " \\n" + RegistryPackageURL;
					}
					else if(Event.equals("Release"))
					{
						String Releaser = (String) System.getenv("GITHUB_ACTOR");
						String ReleaseName = (String) System.getenv("RELEASE_NAME");
						String ReleaseTagName = (String) System.getenv("RELEASE_TAG");
						String ReleaseURL = (String) System.getenv("RELEASE_URL");
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
						message = message + " \\n" + ReleaseURL;
					}
					else if(Event.equals("Repository Dispatch"))
					{
						String Trigger_Actor = (String) System.getenv("GITHUB_ACTOR");
						String WorkflowID = (String) System.getenv("GITHUB_WORKFLOW");
						String WorkflowURL = ServerURL + Repository + "/actions/runs/" + WorkflowID;
						message = "[" + Trigger_Actor + "](" + ServerURL + Trigger_Actor + ") has triggered a new repository dispatch - [" + Action + "](" + WorkflowURL + ")";
						message = message + " \\n" + RepositoryURL;
					}
					else if(Event.equals("Schedule"))
					{
						String Trigger_Actor = (String) System.getenv("GITHUB_ACTOR");
						String Workflow = (String) System.getenv("GITHUB_WORKFLOW");
						String WorkflowID = (String) System.getenv("GITHUB_RUN_ID");
						String WorkflowURL = RepositoryURL + "/actions/runs/" + WorkflowID;
						message = "[" + Trigger_Actor + "](" + ServerURL + Trigger_Actor + ") has scheduled a workflow [" + Workflow + "](" + WorkflowURL  + ")";
						message = message + " \\n" + WorkflowURL;
					}
					else if(Event.equals("Status"))
					{
						String Trigger_Actor = (String) System.getenv("GITHUB_ACTOR");
						String Workflow = (String) System.getenv("GITHUB_WORKFLOW");
						String WorkflowID = (String) System.getenv("GITHUB_RUN_ID");
						String Status = (String) System.getenv("STATUS");
						String WorkflowURL = RepositoryURL + "/actions/runs/" + WorkflowID;
						message = "The status of the [" + Workflow + "](" + WorkflowURL + ") workflow has been updated as " + Status;
						message = message + " \\n" + RepositoryURL;
					}
					else if(Event.equals("Watch"))
					{
						String Watcher = (String) System.getenv("GITHUB_ACTOR");
						message = "[" + Watcher + "](" + ServerURL + Watcher + ") has pushed the [" + Repository + "](" + RepositoryURL + ") repository under the Watch category";
						message = message + " \\n" + RepositoryURL;
					}
					else if(Event.equals("Workflow Dispatch"))
					{
						String Dispatcher = (String) System.getenv("GITHUB_ACTOR");
						String Workflow = (String) System.getenv("GITHUB_WORKFLOW");
						String WorkflowID = (String) System.getenv("GITHUB_RUN_ID");
						String WorkflowURL = RepositoryURL + "/actions/runs/" + WorkflowID;
						message = "[" + Dispatcher + "](" + ServerURL + Dispatcher + ") has triggered the [" + Workflow + "](" + WorkflowURL  + ") workflow"; 
						message = message + " \\n" + WorkflowURL;
					}
				}
				else
				{
					message = message.replace("(me)","[" + Actor + "](" + ActorURL + ")");
					message = message.replace("(repo)","[" + Repository + "](" + RepositoryURL + ")" );
					message = message.replace("(event)","*" + Event + "*");
					message = message.replace("(action)",Action);
				}
				System.out.println(message);
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
			var githubOutput = (String) System.getenv("GITHUB_OUTPUT");
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
		    var githubOutput = (String) System.getenv("GITHUB_OUTPUT");
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
	  var githubOutput = (String) System.getenv("GITHUB_OUTPUT");
    var file = Path.of(githubOutput);
	  var lines = ("message-status=" + Status).lines().toList();
		Files.write(file, lines, UTF_8 , CREATE , APPEND , WRITE);
		lines = ("error-message=" + ErrorMessage).lines().toList();
		Files.write(file, lines, UTF_8 , CREATE , APPEND , WRITE);
		System.out.println("Message - Status : " + Status);
	}
}
