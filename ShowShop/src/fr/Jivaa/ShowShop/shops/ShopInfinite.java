package fr.Jivaa.ShowShop.shops;

import org.bukkit.Location;

public class ShopInfinite extends Shop
{
	public ShopInfinite(Location loc, String nameOwner, String id, Double prixA, Double prixV, int nbobjet, int taillelot) 
	{
		super(loc, nameOwner, id, prixA, prixV, nbobjet, taillelot);
	}
	
	@Override
	public void addItemToShop(int i)
	{
		
	}
	
	@Override
	public void removeItemToShop(int i) 
	{
		
	}

	@Override
	public boolean canRemove(int i)
	{
		return true;
	}
}
