package fr.Jivaa.ShowShop.assistants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;

@SuppressWarnings("deprecation")
public class Assistant
{
	private List<AssistantPage> pages = new ArrayList<AssistantPage>();
	private String title = "Generic assistant";
	protected AssistantPage currentPage = null;
	private int currentPageIndex = 0;
	private Player player = null;
	private String heldBackChat = "";
	private Location assistantStartLocation = null;
	protected static Map<Player, Assistant> instances = new HashMap<Player, Assistant>();
	protected boolean active;

	public Assistant(Player p)
	{
		setPlayer(p);
	}

	public static boolean onPlayerChat(PlayerChatEvent event)
	{
		//Dispatch the chat to the right assistant
		Assistant current = null;
		for(Player p:instances.keySet())
		{
			event.getRecipients().remove(p);
			if(!event.getPlayer().equals(p))
				instances.get(p).heldBackChat += "<" + event.getPlayer().getName() + "> " + event.getMessage() + "\n";
		}
		
		if(instances.containsKey(event.getPlayer()))
			current = instances.get(event.getPlayer());
		else
			return false;
		
		//Handle the input, send it to the right page
		String text = event.getMessage();
		if(text == null || text.isEmpty())
			return false;
		
		if(current.getCurrentPage() == null)
			return false;
		
		event.setCancelled(true);
		switch(current.getCurrentPage().onPageInput(text))
		{
			case CANCEL:
			current.cancel();
			return false;
			
			case FINISH:
			current.stop();
			return true;
			
			case CONTINUE:
			current.currentPageIndex++;
			if(current.pages.size()>current.currentPageIndex)
			{
				current.currentPage = current.pages.get(current.currentPageIndex);
				current.currentPage.play();
			}
			else
				current.stop();
			return true;
			
			case REPEAT:
			current.repeatCurrentPage();
			current.currentPageIndex++;
			if(current.pages.size()>current.currentPageIndex)
			{
				current.currentPage = current.pages.get(current.currentPageIndex);
				current.currentPage.play();
			}
			else
				current.stop();
			return true;
			
			case SILENT_REPEAT:
			return true;
		}
		
		return true;
	}
	
	public static void onPlayerMove(PlayerMoveEvent event)
	{
		Assistant current = null;
		if(instances.containsKey(event.getPlayer()))
		{
			current = instances.get(event.getPlayer());
		}
		else
			return;
		if(current.assistantStartLocation == null)
			return;
		if(current.assistantStartLocation.toVector().distanceSquared(event.getTo().toVector()) > 25)
			current.cancel();
	}
	
	public void setPlayer(Player player)
	{
		this.player = player;
	}
	
	public Player getPlayer()
	{
		return player;
	}
	
	public void setCurrentPage(AssistantPage currentPage)
	{
		this.currentPage = currentPage;
	}
	
	public AssistantPage getCurrentPage()
	{
		return currentPage;
	}
	
	public void setTitle(String title)
	{
		this.title = title;
	}
	
	public String getTitle()
	{
		return title;
	}
	
	public List<AssistantPage> getPages()
	{
		return pages;
	}
	
	public void addPage(AssistantPage page)
	{
		pages.add(page);
		page.setAssistant(this);
		
		if(pages.size() == 1)
		{
			currentPage = page;
		}
	}
	
	/*
	 * Assistant events
	 */
	
	public void onAssistantCancel()
	{
		sendMessage(formatLine("Assistant annule."));
	}
	
	public void onAssistantFinish()
	{
		
	}
	
	/*
	 * Assistant actions
	 */
	
	public void start()
	{
		active = true;
		instances.put(getPlayer(),this);
		String message = getSeparator() + "\n";
		message += formatLine(getTitle()) + "\n";
		message += getSeparator();
		sendMessage(message);
		currentPage.play();
	}
	
	public void cancel()
	{
		closeScreen();
		onAssistantCancel();
		sendMessage(getSeparator());
		remove();
	}
	
	public void stop()
	{
		closeScreen();
		onAssistantFinish();
		sendMessage(getSeparator());
		remove();
	}
	
	protected void closeScreen()
	{
		active = false;
	}
	
	private void remove()
	{
		instances.remove(getPlayer());
		
		if(!heldBackChat.equals(""))
		{
			sendMessage(ChatColor.YELLOW + "Voici le chat retablit pour vous :");
			sendMessage(heldBackChat);
		}
	}
	
	/*
	 * Misc actions
	 */
	
	public void sendMessage(String text)
	{
		for(String line:text.split("\n"))
		{
			getPlayer().sendMessage(line);
		}
	}
	
	public String getSeparator()
	{
		return ChatColor.YELLOW + "|---------------------------------------------------|";
	}
	
	public String formatLine(String line)
	{
		return ChatColor.YELLOW + "| " + ChatColor.WHITE + line;
	}
	
	public void setAssistantStartLocation(Location assistantStartLocation)
	{
		this.assistantStartLocation = assistantStartLocation;
	}
	
	public Location getAssistantStartLocation()
	{
		return assistantStartLocation;
	}
	
	public void repeatCurrentPage()
	{
		pages.add(currentPageIndex + 1, getCurrentPage());
	}
}