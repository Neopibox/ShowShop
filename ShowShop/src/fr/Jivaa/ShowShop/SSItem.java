package fr.Jivaa.ShowShop;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class SSItem
{
	private Item item;
	private Location loc;
	private String itemName;
	private String id;
	private boolean isChunckLoaded;

	public SSItem(Location loc, String id)
	{
		this.loc = loc;
		this.id = id;
		this.itemName = SSItem.get(id).getType().name();
		
		this.respawnItem();
		
		ShowShop.getInstance().showItem.add(this);
		ShowShop.getInstance().itemsByDrop.put(this.item.getEntityId(), this);
	}
	
	public void respawnItem()
	{
		checkForDupedItem();
		
		// Spawn Item Location
		Vector vec = loc.toVector();
		
		if(loc.getBlock().getType() == Material.PISTON_BASE || loc.getBlock().getType() == Material.PISTON_STICKY_BASE)
			vec.add(new Vector(0.5D, 2D, 0.5D));
		else if(loc.getBlock().getType() == Material.PISTON_EXTENSION)
			vec.add(new Vector(0.5D, 1D, 0.5D));
	    
	    loc = vec.toLocation(loc.getWorld());
	    
	    // Spawn Item !
		ItemStack is = get(id);
		is.setAmount(1);
		
		setItem(loc.getWorld().dropItem(loc, is));
		
		// Config Item
		item.setVelocity(new Vector(0.0D, 0.1D, 0.0D));
		
		ShowShop.getInstance().itemsByDrop.remove(this);
		ShowShop.getInstance().itemsByDrop.put(this.item.getEntityId(), this);
	}
	
	public void removeItem()
	{
		checkForDupedItem();
		item.remove();
	}
	
	public void checkForDupedItem()
	{
		Chunk c = loc.getBlock().getChunk();
		
		for(Entity e : c.getEntities())
			if(e.getLocation().getBlock().equals(loc.getBlock()) && (e instanceof Item) && e.getEntityId() == item.getEntityId())
				e.remove();
	}
	
	public static ItemStack get(final String id)
	{
		int itemid = 0;
		short metaData = 0;
		
		if(id.matches("^\\d+[:+',;.]\\d+$"))
		{
			itemid = Integer.parseInt(id.split("[:+',;.]")[0]);
			metaData = Short.parseShort(id.split("[:+',;.]")[1]);
		}
		else
			itemid = Integer.parseInt(id);
		
		Material mat = Material.getMaterial(itemid);
		
		ItemStack retval = new ItemStack(mat);
		retval.setAmount(1);
		retval.setDurability(metaData);
		
		return retval;
	}
    
	// Accesseurs
	public Item getItem()
	{
		return item;
	}
	
	public void setItem(Item item)
	{
		this.item = item;
	}
	
	public Location getLoc()
	{
		return loc;
	}
	
	public void setLoc(Location loc)
	{
		this.loc = loc;
	}
	
	public String getItemName()
	{
		return itemName;
	}
	
	public void setItemName(String itemName) 
	{
		this.itemName = itemName;
	}
	
	public String getId()
	{
		return id;
	}
	
	public void setId(String id) 
	{
		this.id = id;
	}
	
	public boolean isChunckLoaded()
	{
		return isChunckLoaded;
	}
	
	public void setChunckLoaded(boolean isChunckLoaded)
	{
		this.isChunckLoaded = isChunckLoaded;
	}
}
