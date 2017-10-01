package fr.Jivaa.ShowShop.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

import fr.Jivaa.ShowShop.SSManager;
import fr.Jivaa.ShowShop.ShowShop;
import fr.Jivaa.ShowShop.assistants.Assistant;
import fr.Jivaa.ShowShop.assistants.BuyAssistant;
import fr.Jivaa.ShowShop.assistants.ReFillAssistant;
import fr.Jivaa.ShowShop.shops.Shop;
import fr.Jivaa.ShowShop.shops.ShopInfinite;

@SuppressWarnings("deprecation")
public class SSPlayerListener implements Listener
{
	private ShowShop plugin;
	
	public SSPlayerListener(ShowShop plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent event)
	{
		if(ShowShop.getInstance().isShowItem(event.getItem()))
			event.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerChat(PlayerChatEvent event)
	{
		Assistant.onPlayerChat(event);
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event)
	{
		Assistant.onPlayerMove(event);
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		Block b = event.getClickedBlock();
		Player p = event.getPlayer();
		
		try
		{
			if(b.getType() != Material.WALL_SIGN)
				return;
		} catch(Exception e){return;} 
		
		Block ShopBloc = SSManager.getBlockByWallSign(b);
		
		if(SSManager.isShop(ShopBloc.getLocation()) != true)
			return;
		
		Shop shop = SSManager.getShop(ShopBloc.getLocation());
		
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK)
		{
			event.setCancelled(true);
			
			if(shop.IsOwner(p.getName()))
			{
				if(shop instanceof ShopInfinite)
				{
					plugin.sendMessage(p, "Un magasin illimite n a pas besoin d'être reapprovisionne.");
					return;
				}
				else
				{
					Assistant ajout = new ReFillAssistant(p, shop);
					ajout.start();
				}
			}
			else if(plugin.getPermissionHandler().has(p, "acheteur", false))
			{
				Assistant achat = new BuyAssistant(p, shop);
				achat.start();
			}
			else
				plugin.sendMessage(p, "Vous n avez pas la permission de commercer");
		}
		else if(event.getAction() == Action.LEFT_CLICK_BLOCK)
		{
			String isInfinite = (shop instanceof ShopInfinite) ? "illimite" : "limite";
			
			p.sendMessage(ChatColor.YELLOW + "|---------------------------------------------------|");
			p.sendMessage(ChatColor.YELLOW + "| " + ChatColor.WHITE + "ShowShop");
			p.sendMessage(ChatColor.YELLOW + "|---------------------------------------------------|");
			p.sendMessage(ChatColor.YELLOW + "|---------------------------------------------------|");
			p.sendMessage(ChatColor.YELLOW + "| " + ChatColor.WHITE + "Informations sur le magasin");
			p.sendMessage(ChatColor.YELLOW + "|---------------------------------------------------|");
			p.sendMessage(ChatColor.YELLOW + "| " + ChatColor.WHITE + "Ce magasin appartient a " + ChatColor.YELLOW + shop.getNameOwner() + ChatColor.WHITE + " et est un magasin " + isInfinite);
			if(isInfinite.equals("limite"))
				p.sendMessage(ChatColor.YELLOW + "| " + ChatColor.WHITE + "Il reste " + ChatColor.YELLOW + shop.getNbObjet() + " " + shop.getItem().getItemName() + ChatColor.WHITE + " dans ce magasin");
			if(plugin.getPermissionHandler().has(p, "admin", true))
				p.sendMessage(ChatColor.YELLOW + "| " + ChatColor.WHITE + "L'id de ce magasin dans la configuration est " + ChatColor.YELLOW + plugin.listshop.indexOf(shop));
			p.sendMessage(ChatColor.YELLOW + "|---------------------------------------------------|");
		}
	}
}
