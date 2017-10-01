package fr.Jivaa.ShowShop.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemDespawnEvent;

import fr.Jivaa.ShowShop.ShowShop;

public class SSItemListener implements Listener
{
	private ShowShop plugin;
	
	public SSItemListener(ShowShop plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onItemDespawn(ItemDespawnEvent event)
	{
		if(plugin.itemsByDrop.containsKey(event.getEntity().getEntityId()))
		{
			plugin.itemsByDrop.get(event.getEntity().getEntityId()).respawnItem();
		}
	}
}
