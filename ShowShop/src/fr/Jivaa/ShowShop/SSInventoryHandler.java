package fr.Jivaa.ShowShop;

import java.util.ArrayList;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public abstract class SSInventoryHandler
{
	public static int removeFromInventory(Inventory inventory, ItemStack type, int amount)
	{
		ArrayList<ItemStack> remove = new ArrayList<ItemStack>();
		int removed = 0;
		
		for(ItemStack is_actuel : inventory)
		{
			if(amount == 0)
					break;
			
			if(is_actuel != null)
			{
				if(is_actuel.getType().equals(type.getType()) && is_actuel.getData().equals(type.getData()))
				{
					if(is_actuel.getAmount() <= amount)
					{
						remove.add(is_actuel);
						amount -= is_actuel.getAmount();
						removed += is_actuel.getAmount();
					}
					else
					{
						is_actuel.setAmount(is_actuel.getAmount() - amount);
						removed += amount;
						amount = 0;
					}
				}
			}
		}
		
		for(ItemStack is : remove)
			inventory.removeItem(is);
		
		return removed;
	}
}
