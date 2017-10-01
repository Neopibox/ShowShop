package fr.Jivaa.ShowShop.assistants;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.Jivaa.ShowShop.SSInventoryHandler;
import fr.Jivaa.ShowShop.SSItem;
import fr.Jivaa.ShowShop.ShowShop;
import fr.Jivaa.ShowShop.shops.Shop;

public class ReFillAssistant extends Assistant
{
	private AssistantPage choix;
	private AssistantPage ajout;
	private AssistantPage retrait;
	
	public ReFillAssistant(Player p, final Shop magasin)
	{
		super(p);
		
		ajout = new AssistantPage(this)
		{
			public AssistantAction onPageInput(String text)
			{
				if(!ShowShop.getInstance().isNumeric(text))
					return null;
				
				int nombre = Integer.parseInt(text);
				
				if(nombre <= 0)
				{
					sendMessage(formatLine("Pas d ajout\n"));
					sendMessage(getSeparator());
					
					return AssistantAction.CANCEL;
				}
				
				// Montant
				int amount = 0;
				ItemStack is = SSItem.get(magasin.getItem().getId());
				
				for(ItemStack is1 : getPlayer().getInventory())
				{
					if(is1 != null)
						if(is1.getAmount() != 0)
							if(is1.getType().equals(is.getType()) && is1.getData().equals(is.getData()))
								amount += is1.getAmount();
				}
				
				if(amount < nombre)
					nombre = amount;
				
				magasin.addItemToShop(nombre);
				SSInventoryHandler.removeFromInventory(getPlayer().getInventory(), is, nombre);
				
				sendMessage(formatLine("Vous avez ajoute(e) " + ChatColor.YELLOW + nombre + " " + magasin.getItem().getItemName() + ChatColor.WHITE + " a votre magasin\n"));
				
				return AssistantAction.FINISH;
			}
		};
		ajout.setTitle("Ajout");
		ajout.setText("Combien de " + ChatColor.YELLOW + magasin.getItem().getItemName() + ChatColor.WHITE + " voulez-vous ajouter ?");
		
		retrait = new AssistantPage(this)
		{
			public AssistantAction onPageInput(String text)
			{
				if(!ShowShop.getInstance().isNumeric(text))
					return null;
				
				int nombre = Integer.parseInt(text);
				
				if(nombre <= 0)
				{
					sendMessage(formatLine("Pas de retrait\n"));
					sendMessage(getSeparator());
					
					return AssistantAction.CANCEL;
				}
				
				if(magasin.getNbObjet() == 0)
				{
					sendMessage(formatLine("Ce magasin est en rupture de stock !\n"));
					sendMessage(getSeparator());
					
					return AssistantAction.CANCEL;
				}
				
				if(nombre > magasin.getNbObjet())
					nombre = magasin.getNbObjet();
				
				// ItemStacker le nombre
				ItemStack is = SSItem.get(magasin.getItem().getId());
				is.setAmount(nombre);
				
				// Transfert
				magasin.removeItemToShop(nombre);
				getPlayer().getInventory().addItem(is);
				
				sendMessage(formatLine("Vous avez retire(e) " + ChatColor.YELLOW + nombre + " " + magasin.getItem().getItemName() + ChatColor.WHITE + " de votre magasin\n"));
				
				return AssistantAction.FINISH;
			}
		};
		retrait.setTitle("Retrait");
		retrait.setText("Combien de " + ChatColor.YELLOW + magasin.getItem().getItemName() +  ChatColor.WHITE + " voulez-vous retirer ?");
		
		choix = new AssistantPage(this)
		{
			public AssistantAction onPageInput(String text)
			{
				if(text.equalsIgnoreCase("ajout") || text.equalsIgnoreCase("ajouter") || text.equalsIgnoreCase("ajoute")|| text.equalsIgnoreCase("ajoutee") || text.equalsIgnoreCase("ajoute") || text.equalsIgnoreCase("ajoutees"))
					getAssistant().addPage(ajout);
				else if(text.equalsIgnoreCase("retrait") || text.equalsIgnoreCase("retirer") || text.equalsIgnoreCase("retire") || text.equalsIgnoreCase("retiree") || text.equalsIgnoreCase("retires") || text.equalsIgnoreCase("retirees"))
					getAssistant().addPage(retrait);
				else
					return AssistantAction.CANCEL;
				
				return AssistantAction.CONTINUE;
			}
		};
		choix.setTitle("Ajout / Retrait");
		choix.setText("Voulez-vous ajouter ou retirer ?\n");
		
		// Ajout des pages !
		this.addPage(choix);
		
		this.setTitle("ShowShop");
		this.setAssistantStartLocation(magasin.getLoc());
	}
}
