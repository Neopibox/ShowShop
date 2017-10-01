package fr.Jivaa.ShowShop.shops;

import org.bukkit.Location;

import fr.Jivaa.ShowShop.SSItem;

public abstract class Shop
{
	public Shop(Location loc, String nameOwner, String id, Double prixA, Double prixV, int nbobjet, int taillelot)
	{
		this.loc = loc;
		this.nameOwner = nameOwner;
		this.prixA = prixA;
		this.prixV = prixV;
		this.nbobjet = nbobjet;
		this.taillelot = taillelot;
		
		this.item = new SSItem(loc, id);
	}
	
	// Accesseurs
	public Location getLoc()
	{
		return this.loc;
	}
	
	public void setLoc(Location loc)
	{
		this.loc = loc;
	}
	
	public String getNameOwner()
	{
		return nameOwner;
	}
	
	public Double getPrixA() 
	{
		return prixA;
	}
	
	public void setPrixA(double prixA)
	{
		this.prixA = prixA;
	}
	
	public Double getPrixV()
	{
		return prixV;
	}

	public void setPrixV(Double prixV)
	{
		this.prixV = prixV;
	}
	
	public int getNbObjet()
	{
		return nbobjet;
	}
	
	public void setNbObjet(int nbobjet)
	{
		this.nbobjet = nbobjet;
	}
	
	public int getTaillelot() 
	{
		return taillelot;
	}
	
	public void setTaillelot(int taillelot)
	{
		this.taillelot = taillelot;
	}
	
	public void setItem(SSItem item)
	{
		this.item = item;
	}
	
	public SSItem getItem()
	{
		return this.item;
	}
	
	// Is
	public boolean IsOwner(String name)
	{
		return this.nameOwner.equals(name);
	}
	
	// Add / remove
	public abstract void addItemToShop(int i);
	public abstract void removeItemToShop(int i);
	public abstract boolean canRemove(int i);
	
	// Attributs
	protected Location loc;
	protected SSItem item;
	protected String nameOwner;
	protected Double prixA;
	protected Double prixV;
	protected int nbobjet;
	protected int taillelot;
}
