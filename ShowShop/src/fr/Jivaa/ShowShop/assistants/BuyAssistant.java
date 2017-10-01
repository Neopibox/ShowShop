package fr.Jivaa.ShowShop.assistants;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.Jivaa.ShowShop.SSInventoryHandler;
import fr.Jivaa.ShowShop.SSItem;
import fr.Jivaa.ShowShop.ShowShop;
import fr.Jivaa.ShowShop.shops.Shop;

public class BuyAssistant extends Assistant
{
	private AssistantPage choix;
	private AssistantPage achat;
	private AssistantPage vente;
	private Economy economy;
	
	public BuyAssistant(Player p, final Shop magasin)
	{
		super(p);
		
		economy = ShowShop.getInstance().getEconomy();
		
		achat = new AssistantPage(this)
		{
			public AssistantAction onPageInput(String text)
			{
				if(!ShowShop.getInstance().isNumeric(text))
					return null;
				
				int nombre = Integer.parseInt(text);
				
				// Verification
				if(nombre <= 0 || magasin.getPrixA() == -1)
				{
					sendMessage(formatLine("Pas d achat possible\n"));
					sendMessage(getSeparator());
					
					return AssistantAction.CANCEL;
				}
				
				if(magasin.getNbObjet() == 0)
				{
					sendMessage(formatLine("Ce magasin est en rupture de stock !\n"));
					sendMessage(getSeparator());
					
					return AssistantAction.CANCEL;
				}
				
				if(nombre % magasin.getTaillelot() != 0)
				{
					sendMessage(formatLine("Veuillez entrer un nombre divisible par " + ChatColor.YELLOW + magasin.getTaillelot() + ChatColor.WHITE + "\n"));
					sendMessage(getSeparator());
					
					return AssistantAction.CANCEL;
				}
				
				if(!magasin.canRemove(nombre))
					nombre = magasin.getNbObjet();
				
				// ItemStacker le nombre
				ItemStack is = SSItem.get(magasin.getItem().getId());
				is.setAmount(nombre);
				
				if(economy.has(getAssistant().getPlayer().getName(), magasin.getPrixA() * nombre) == false)
				{
					sendMessage(formatLine("Vous n avez plus assez d argent\n"));
					sendMessage(getSeparator());
					
					return AssistantAction.CANCEL;
				}
				
				// Transfert
				magasin.removeItemToShop(nombre);
				getPlayer().getInventory().addItem(is);
				
				economy.depositPlayer(magasin.getNameOwner(), magasin.getPrixA() * (nombre / magasin.getTaillelot()));;
				economy.withdrawPlayer(getAssistant().getPlayer().getName(), magasin.getPrixA() * (nombre / magasin.getTaillelot()));
				
				sendMessage(formatLine("Vous avez achete(e) " +  ChatColor.YELLOW + nombre + " " + magasin.getItem().getItemName() +  ChatColor.WHITE + " a " + ChatColor.YELLOW + magasin.getNameOwner() + "\n"));
				
				return AssistantAction.FINISH;
			}
		};
		achat.setTitle("Achat");
		achat.setText("Combien de " + ChatColor.YELLOW + magasin.getItem().getItemName() +  ChatColor.WHITE + " voulez-vous acheter ?");
		
		vente = new AssistantPage(this)
		{
			public AssistantAction onPageInput(String text)
			{
				if(!ShowShop.getInstance().isNumeric(text))
					return null;
				
				int nombre = Integer.parseInt(text);
				
				if(nombre <= 0 || magasin.getPrixV() == -1)
				{
					sendMessage(formatLine("Pas de vente\n"));
					sendMessage(getSeparator());
					
					return AssistantAction.CANCEL;
				}
				
				if(nombre % magasin.getTaillelot() != 0)
				{
					sendMessage(formatLine("Veuillez entrer un nombre divisible par " + ChatColor.YELLOW + magasin.getTaillelot() + ChatColor.WHITE + "\n"));
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
				
				if(economy.has(magasin.getNameOwner(), magasin.getPrixV() * nombre) == false)
				{
					sendMessage(formatLine("Le createur de ce magasin n a plus assez d argent\n"));
					sendMessage(getSeparator());
					
					return AssistantAction.CANCEL;
				}
				
				// Transfert
				magasin.addItemToShop(nombre);
				SSInventoryHandler.removeFromInventory(getPlayer().getInventory(), is, nombre);
				
				economy.depositPlayer(getAssistant().getPlayer().getName(), magasin.getPrixV() * (nombre / magasin.getTaillelot()));
				economy.withdrawPlayer(magasin.getNameOwner(), magasin.getPrixV() * (nombre/ magasin.getTaillelot()));
				
				sendMessage(formatLine("Vous avez vendu " + ChatColor.YELLOW + nombre + " " + magasin.getItem().getItemName() + ChatColor.WHITE + " a " +  ChatColor.YELLOW + magasin.getNameOwner() + "\n"));
				
				return AssistantAction.FINISH;
			}
		};
		vente.setTitle("Vente");
		vente.setText("Combien de " + ChatColor.YELLOW + magasin.getItem().getItemName() + ChatColor.WHITE + " voulez-vous vendre ?");
		
		choix = new AssistantPage(this)
		{
			public AssistantAction onPageInput(String text)
			{
				if(text.equalsIgnoreCase("vente") || text.equalsIgnoreCase("vendre") || text.equalsIgnoreCase("vendu") || text.equalsIgnoreCase("vendue") || text.equalsIgnoreCase("vendus") || text.equalsIgnoreCase("vendues"))
					getAssistant().addPage(vente);
				else if(text.equalsIgnoreCase("achat") || text.equalsIgnoreCase("acheter") || text.equalsIgnoreCase("achete") || text.equalsIgnoreCase("achetee") || text.equalsIgnoreCase("achetes") || text.equalsIgnoreCase("achetees"))
					getAssistant().addPage(achat);
				else
					return AssistantAction.CANCEL;
				
				return AssistantAction.CONTINUE;
			}
		};
		choix.setTitle("Achat / Vente");
		choix.setText("Voulez-vous acheter ou vendre ?\n");
		
		// Ajout des pages !
		this.addPage(choix);
		
		this.setTitle("ShowShop");
		this.setAssistantStartLocation(magasin.getLoc());
	}
}
