package fr.Jivaa.ShowShop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.Map.Entry;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.ptibiscuit.framework.JavaPluginEnhancer;

import fr.Jivaa.ShowShop.data.IData;
import fr.Jivaa.ShowShop.data.YamlData;

import fr.Jivaa.ShowShop.listeners.SSBlockListener;
import fr.Jivaa.ShowShop.listeners.SSItemListener;
import fr.Jivaa.ShowShop.listeners.SSPlayerListener;
import fr.Jivaa.ShowShop.shops.Shop;

public class ShowShop extends JavaPluginEnhancer
{
	private static ShowShop instance;
	public IData data;
	public ArrayList<Shop> listshop = new ArrayList<Shop>();
	public ArrayList<SSItem> showItem = new ArrayList<SSItem>();
	// Achat
	public HashMap<String, Double> prixMinA = new HashMap<String, Double>();
	public HashMap<String, Double> prixMaxA = new HashMap<String, Double>();
	// Vente
	public HashMap<String, Double> prixMinV = new HashMap<String, Double>();
	public HashMap<String, Double> prixMaxV = new HashMap<String, Double>();
	public HashMap<Integer, SSItem> itemsByDrop = new HashMap<Integer, SSItem>(10000);
	private Economy economy = null;
	
	private String prefix = "[ShowShop] ";
	
	@Override
	public void onEnable()
	{
		this.setup(ChatColor.YELLOW + "[ShowShop]", "ss", true);
		ShowShop.instance = this;
		
		myLog.startFrame();
		myLog.addInFrame("ShowShop by Jivaa");
		myLog.addCompleteLineInFrame();
		
		String type = getConfig().getString("config.type");
		if(type.equalsIgnoreCase("yml"))
			data = new YamlData();
		else
			data = new YamlData();
		
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvents(new SSBlockListener(this), this);
		pm.registerEvents(new SSPlayerListener(this), this);
		pm.registerEvents(new SSItemListener(this), this);
		
        if(!setupEconomy())
        {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
		
		data.loadShop();
		loadLimits();
		myLog.addInFrame(listshop.size() + " shops loaded !");
		myLog.displayFrame(false);
	}
	
	private void loadLimits()
	{
		FileConfiguration config = this.getConfig();
		
		if(!config.contains("prices"))
			return;
		
		for(Entry<String, Object> listShops : config.getConfigurationSection("prices").getValues(false).entrySet())
		{
			MemorySection data = (MemorySection) listShops.getValue();
			
			prixMinA.put(listShops.getKey(), data.getDouble("prixMinA"));
			prixMaxA.put(listShops.getKey(), data.getDouble("prixMaxA"));
			prixMinV.put(listShops.getKey(), data.getDouble("prixMinV"));
			prixMaxV.put(listShops.getKey(), data.getDouble("prixMaxV"));
		}
	}
	
	@Override
	public void onDisable()
	{
		myLog.startFrame();
		
		getServer().getScheduler().cancelTasks(this);
		
		for(SSItem item : showItem)
			item.removeItem();
		
		data.saveShop();
		
		itemsByDrop.clear();
		showItem.clear();
		listshop.clear();
		prixMinA.clear();
		prixMaxA.clear();
		prixMinV.clear();
		prixMaxV.clear();
		
		myLog.addInFrame("ShowShop disabled !");
		myLog.displayFrame(false);
	}
	
	public void onConfigurationDefault(FileConfiguration config)
	{
		config.set("config.type", "yml");
	}
	
	public void onLangDefault(Properties p)
	{
		
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if(!label.equalsIgnoreCase("ss"))
			return false;
		
		if(args.length == 0)
		{
			sender.sendMessage(ChatColor.YELLOW + "|---------------------------------------------------|");
			sender.sendMessage(ChatColor.YELLOW + "| " + ChatColor.WHITE + "ShowShop v3.0 by Jivaa");
			sender.sendMessage(ChatColor.YELLOW + "|---------------------------------------------------|");
			sender.sendMessage(ChatColor.YELLOW + "|---------------------------------------------------|");
			sender.sendMessage(ChatColor.YELLOW + "| " + ChatColor.WHITE + "Plugin de magasin. Interaction via le chat.");
			sender.sendMessage(ChatColor.YELLOW + "|---------------------------------------------------|");
			return true;
		}
		
		if(!sender.hasPermission("ss.admin") || !sender.isOp())
		{
			sender.sendMessage(ChatColor.DARK_RED + prefix + "Vous n avez pas la permission d'executer les commandes de ShowShop.");
			return false;
		}
		
		if(args[0].equalsIgnoreCase("load"))
		{
			for(SSItem item : showItem)
				item.removeItem();
			
			itemsByDrop.clear();
			showItem.clear();
			listshop.clear();
			prixMinA.clear();
			prixMaxA.clear();
			prixMinV.clear();
			prixMaxV.clear();
			
			data.loadShop();
			loadLimits();
			sender.sendMessage(ChatColor.YELLOW + "[ShowShop] " + ChatColor.WHITE + "Base de donnees chargee");
		}
		else if(args[0].equalsIgnoreCase("save"))
		{
			data.saveShop();
			sender.sendMessage(ChatColor.YELLOW + "[ShowShop] " + ChatColor.WHITE + "Base de donnees sauvegardee");
		}
		else if(args[0].equalsIgnoreCase("reload"))
		{
			data.saveShop();
			
			for(SSItem item : showItem)
				item.removeItem();
			
			itemsByDrop.clear();
			showItem.clear();
			listshop.clear();
			prixMinA.clear();
			prixMaxA.clear();
			prixMinV.clear();
			prixMaxV.clear();
			
			data.loadShop();
			loadLimits();
			sender.sendMessage(ChatColor.YELLOW + "[ShowShop] " + ChatColor.WHITE + "Base de donnees rechargee");
		}
		else if(args[0].equalsIgnoreCase("help"))
		{
			sender.sendMessage(ChatColor.YELLOW + "|---------------------------------------------------|");
			sender.sendMessage(ChatColor.YELLOW + "| " + ChatColor.WHITE + "ShowShop");
			sender.sendMessage(ChatColor.YELLOW + "|---------------------------------------------------|");
			sender.sendMessage(ChatColor.YELLOW + "|---------------------------------------------------|");
			sender.sendMessage(ChatColor.YELLOW + "| " + ChatColor.WHITE + "Liste des commandes");
			sender.sendMessage(ChatColor.YELLOW + "|---------------------------------------------------|");
			sender.sendMessage(ChatColor.YELLOW + "| " + ChatColor.WHITE + "/ss help : Affiche cette aide.");
			sender.sendMessage(ChatColor.YELLOW + "| " + ChatColor.WHITE + "/ss load : Charge la liste des magasins a partir du fichier.");
			sender.sendMessage(ChatColor.YELLOW + "| " + ChatColor.WHITE + "/ss save : Sauvegarde la liste des magasins dans le fichier.");
			sender.sendMessage(ChatColor.YELLOW + "| " + ChatColor.WHITE + "/ss reload : Equivalent de /ss save et de /ss load a la suite.");
			sender.sendMessage(ChatColor.YELLOW + "| " + ChatColor.WHITE + "/ss info <id> : Donne des informations sur les prix de l objet.");
			sender.sendMessage(ChatColor.YELLOW + "| " + ChatColor.WHITE + "/ss debug : Commande de debug (peut changer en fonction ");
			sender.sendMessage(ChatColor.YELLOW + "| " + ChatColor.WHITE + "des versions).");
			sender.sendMessage(ChatColor.YELLOW + "|---------------------------------------------------|");
		}
		else if(args[0].equalsIgnoreCase("info"))
		{
			sender.sendMessage(ChatColor.YELLOW + "|---------------------------------------------------|");
			sender.sendMessage(ChatColor.YELLOW + "| " + ChatColor.WHITE + "ShowShop");
			sender.sendMessage(ChatColor.YELLOW + "|---------------------------------------------------|");
			sender.sendMessage(ChatColor.YELLOW + "|---------------------------------------------------|");
			sender.sendMessage(ChatColor.YELLOW + "| " + ChatColor.WHITE + "Informations sur cet objet");
			sender.sendMessage(ChatColor.YELLOW + "|---------------------------------------------------|");
			
			/* -- ACHAT -- */
			
			// Prix Minimum
			if(prixMinA.get(args[1]) != null)
				sender.sendMessage(ChatColor.YELLOW + "| " + ChatColor.WHITE + "Le prix minimum d'achat est de " + prixMinA.get(args[1]) + " "+ this.economy.currencyNamePlural() + ".");
			else
				sender.sendMessage(ChatColor.YELLOW + "| " + ChatColor.WHITE + "Il n'y a pas de prix minimum pour l'achat.");
			
			// Prix Maximum
			if(prixMaxA.get(args[1]) != null)
				sender.sendMessage(ChatColor.YELLOW + "| " + ChatColor.WHITE + "Le prix maximum d'achat est de " + prixMaxA.get(args[1]) + " "+ this.economy.currencyNamePlural() + ".");
			else
				sender.sendMessage(ChatColor.YELLOW + "| " + ChatColor.WHITE + "Il n'y a pas de prix maximum pour l'achat.");
			
			/* -- VENTE -- */
			
			// Prix Minimum
			if(prixMinV.get(args[1]) != null)
				sender.sendMessage(ChatColor.YELLOW + "| " + ChatColor.WHITE + "Le prix minimum de revente est de " + prixMinV.get(args[1]) + " "+ this.economy.currencyNamePlural() + ".");
			else
				sender.sendMessage(ChatColor.YELLOW + "| " + ChatColor.WHITE + "Il n'y a pas de prix minimum pour la revente.");
			
			// Prix Maximum
			if(prixMaxV.get(args[1]) != null)
				sender.sendMessage(ChatColor.YELLOW + "| " + ChatColor.WHITE + "Le prix maximum de revente est de " + prixMaxV.get(args[1]) + " "+ this.economy.currencyNamePlural() + ".");
			else
				sender.sendMessage(ChatColor.YELLOW + "| " + ChatColor.WHITE + "Il n'y a pas de prix maximum pour la revente.");
			
			sender.sendMessage(ChatColor.YELLOW + "|---------------------------------------------------|");
		}
		else if(args[0].equalsIgnoreCase("debug"))
		{
			sender.sendMessage(ChatColor.YELLOW + "|---------------------------------------------------|");
			sender.sendMessage(ChatColor.YELLOW + "| " + ChatColor.WHITE + "ShowShop");
			sender.sendMessage(ChatColor.YELLOW + "|---------------------------------------------------|");
			sender.sendMessage(ChatColor.YELLOW + "|---------------------------------------------------|");
			sender.sendMessage(ChatColor.YELLOW + "| " + ChatColor.WHITE + "Commande de debug");
			sender.sendMessage(ChatColor.YELLOW + "|---------------------------------------------------|");
			sender.sendMessage(ChatColor.YELLOW + "| " + ChatColor.WHITE + "Rien à ajouter ...");
			sender.sendMessage(ChatColor.YELLOW + "|---------------------------------------------------|");
		}
		else
			return false;
		
		return true;
	}
	
	public boolean isNumeric(String string)
	{
		try { Double.parseDouble(string); }
		catch(Exception e) { return false; }
		
		return true;
	}
	
	public boolean isMaterial(String mat)
	{
		try { Material.getMaterial(mat.trim()); }
		catch(Exception e) { return false; }
		
		return true;
	}
	
	public String toTextNormal(String name)
	{
        name = name.toLowerCase();
        String[] split = name.split(Character.toString('_'));
        StringBuilder total = new StringBuilder(3);
        for (String s : split) total.append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).append(' ');
        
        return total.toString().trim();
	}
	
	public boolean isShowItem(Item item)
	{
		return itemsByDrop.containsKey(item.getEntityId());
	}
	
	public static ShowShop getInstance() 
	{
		return instance;
	}
	
	// Economy
	public boolean setupEconomy()
	{
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		this.economy = rsp.getProvider();
		return economy != null;
	}
	
	public Economy getEconomy()
	{
		return this.economy;
	}
	
	public boolean isEconomyEnabled()
	{
		return (this.economy != null);
	}
}
