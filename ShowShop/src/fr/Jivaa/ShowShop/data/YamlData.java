package fr.Jivaa.ShowShop.data;

import java.io.File;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;

import fr.Jivaa.ShowShop.ShowShop;
import fr.Jivaa.ShowShop.shops.Shop;
import fr.Jivaa.ShowShop.shops.ShopFinite;
import fr.Jivaa.ShowShop.shops.ShopInfinite;

public class YamlData implements IData
{
	private File file;
	private ShowShop plugin;
	
	public YamlData()
	{
		plugin = ShowShop.getInstance();
		
		try
		{
			file = new File("./plugins/ShowShop/shops.yml");
			
			if(!file.exists())
				file.createNewFile();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void loadShop()
	{
		YamlConfiguration yml;
		yml = YamlConfiguration.loadConfiguration(file);
		
		if(yml.getConfigurationSection("shops") == null)
			return;
		
		for(Entry<String, Object> listShops : yml.getConfigurationSection("shops").getValues(false).entrySet())
		{
			MemorySection data = (MemorySection) listShops.getValue();
			World w = plugin.getServer().getWorld(data.getString("world"));
			Location loc = new Location(w, data.getInt("location.X"), data.getInt("location.Y"), data.getInt("location.Z"));
			String name = data.getString("name");
			String itemId = data.getString("itemId");
			Double prixA = data.getDouble("prixA");
			Double prixV = data.getDouble("prixV");
			int nbObject = data.getInt("NbObject");
			int tailleLot = data.getInt("TailleLot");
			
			Shop shop = null;
			
			if(data.getBoolean("limite") == false)
				shop = new ShopInfinite(loc, name, itemId, prixA, prixV, nbObject, tailleLot);
			else
				shop = new ShopFinite(loc, name, itemId, prixA, prixV, nbObject, tailleLot);
			
			plugin.listshop.add(shop);
		}
	}

	public void saveShop()
	{
		YamlConfiguration yml = new YamlConfiguration();
		
		try
		{
			file.delete();
			file.createNewFile();
		}
		catch (Exception e1) { e1.printStackTrace(); }
		
		for(Shop shop : plugin.listshop)
		{
			Location loc = shop.getLoc();
			World w = loc.getWorld();
			
			String chemin = "shops." + plugin.listshop.indexOf(shop) + ".";
			
			if(shop instanceof ShopInfinite)
				yml.set(chemin + "illimite", false);
			else
				yml.set(chemin + "limite", true);
			
			yml.set(chemin + "world", w.getName());
			yml.set(chemin + "location.X", loc.getBlockX());
			yml.set(chemin + "location.Y", loc.getBlockY());
			yml.set(chemin + "location.Z", loc.getBlockZ());
			yml.set(chemin + "name", shop.getNameOwner());
			yml.set(chemin + "itemId", shop.getItem().getId());
			yml.set(chemin + "prixA", shop.getPrixA());
			yml.set(chemin + "prixV", shop.getPrixV());
			yml.set(chemin + "NbObject", shop.getNbObjet());
			yml.set(chemin + "TailleLot", shop.getTaillelot());
		}
		
		try
		{
			yml.save(file);
		}
		catch (Exception e) { e.printStackTrace(); }
	}
}