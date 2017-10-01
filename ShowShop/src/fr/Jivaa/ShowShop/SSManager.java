package fr.Jivaa.ShowShop;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.material.Sign;

import fr.Jivaa.ShowShop.shops.Shop;

public class SSManager
{
	public static Block getBlockByWallSign(Block WallSign)
	{
		Block Return = null; // pour éviter une erreur
		
		if(WallSign.getTypeId() == Material.WALL_SIGN.getId())
		{
			Sign s = (Sign) WallSign.getState().getData();
			Return = WallSign.getRelative(s.getAttachedFace());
		}
		
		return Return;
	}
	
	public static boolean isShop(Location location)
	{
		for(int i = 0; i < ShowShop.getInstance().listshop.size(); i++)
		{
			if(ShowShop.getInstance().listshop.get(i).getLoc().equals(location))
				return true;
		}
		
		return false;
	}
	
	public static Shop getShop(Location loc)
	{	
		for(int i = 0; i < ShowShop.getInstance().listshop.size(); i++)
		{
			if(ShowShop.getInstance().listshop.get(i).getLoc().equals(loc))
				return ShowShop.getInstance().listshop.get(i);
		}
		
		return null;
	}
}
