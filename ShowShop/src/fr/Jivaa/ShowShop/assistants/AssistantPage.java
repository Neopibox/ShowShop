package fr.Jivaa.ShowShop.assistants;

import org.bukkit.ChatColor;

public class AssistantPage
{
	private String title = "Generic Page";
	private String text = "Enter some weird text here.";
	private Assistant assistant = null;
	
	public AssistantPage(Assistant assistant)
	{
		this.assistant = assistant;
	}
	
	public void setTitle(String title) 
	{
		this.title = title;
	}
	
	public String getTitle()
	{
		return title;
	}
	
	public void setAssistant(Assistant assistant)
	{
		this.assistant = assistant;
	}
	
	public Assistant getAssistant()
	{
		return assistant;
	}
	
	public AssistantAction onPageInput(String text)
	{
		assistant.sendMessage(assistant.formatLine(ChatColor.YELLOW + "You: " + ChatColor.WHITE+text));
		return AssistantAction.CONTINUE;
	}
	
	public void setText(String text)
	{
		this.text = text;
	}
	
	public String getText() 
	{
		return text;
	}
	
	/*
	 * Page actions
	 */
	
	public void play()
	{
		String message = "";
		
		if(!getTitle().equals(""))
		{
			message += assistant.getSeparator()+"\n";
			message += assistant.formatLine(getTitle())+"\n";
		}
		
		message += assistant.getSeparator()+"\n";
		
		if(!getText().equals(""))
		{
			for(String line:getText().split("\n"))
			{
				message += assistant.formatLine(line)+"\n";
			}
			message += assistant.getSeparator();
		}
		
		assistant.sendMessage(message);

	}
	
	public void sendMessage(String text)
	{
		getAssistant().sendMessage(text);
	}
}