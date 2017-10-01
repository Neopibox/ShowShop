package fr.Jivaa.ShowShop.shops;

import org.bukkit.Location;

public class ShopFinite extends Shop
{
	public ShopFinite(Location loc, String nameOwner, String id, Double prixA, Double prixV, int nbobjet, int taillelot)
	{
		super(loc, nameOwner, id, prixA, prixV, nbobjet, taillelot);
	}
	
	@Override
	public void addItemToShop(int i)
	{
		this.setNbObjet(this.nbobjet + i);
	}
	
	@Override
	public void removeItemToShop(int i)
	{
		if(this.canRemove(i))
		{
			this.nbobjet -= i;
		}
	}
	
	@Override
	public boolean canRemove(int i)
	{
		if(this.nbobjet >= i)
			return true;
		
		return false;
	}
}
