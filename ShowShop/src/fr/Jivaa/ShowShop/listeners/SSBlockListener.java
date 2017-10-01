package fr.Jivaa.ShowShop.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import fr.Jivaa.ShowShop.SSItem;
import fr.Jivaa.ShowShop.SSManager;
import fr.Jivaa.ShowShop.ShowShop;
import fr.Jivaa.ShowShop.shops.Shop;
import fr.Jivaa.ShowShop.shops.ShopFinite;
import fr.Jivaa.ShowShop.shops.ShopInfinite;

public class SSBlockListener implements Listener
{
	private ShowShop plugin;
	
	public SSBlockListener(ShowShop plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onSignChange(SignChangeEvent event)
	{
		Player player = event.getPlayer();
		String lines[] = event.getLines();
		Block bloc = event.getBlock();
		Block isPiston = SSManager.getBlockByWallSign(bloc);
		
		// Gestion d'erreurs
		if(isPiston == null)
			return;
		
		if(lines[0].isEmpty()  || lines[1].isEmpty() || lines[2].isEmpty() || lines[3].isEmpty())
			return;
		
		if(isPiston.getType() != Material.PISTON_BASE && isPiston.getType() != Material.PISTON_EXTENSION && isPiston.getType() != Material.PISTON_MOVING_PIECE)
			return;

		// Gestion des permissions
		if(!plugin.getPermissionHandler().has(player, "vendeur", false) && !plugin.getPermissionHandler().has(player, "admin", true) && !player.isOp())
			return;
		
		int taille = 0;
		
		if(lines[0].contains("#"))
		{
			String s[] = lines[0].split("#");
			taille = Integer.parseInt(s[0]);
		}
		else if(ShowShop.getInstance().isNumeric(lines[0]))
		{
			taille = Integer.parseInt(lines[0]);
		}
		
		if(taille <= 0)	// Vérification que l'on ne achète / vend pas par un nombre négatif (logique)
			return;
		
		if(!ShowShop.getInstance().isNumeric(lines[2]) || !ShowShop.getInstance().isNumeric(lines[3]))
			return;
		
		Double prixA = Double.parseDouble(lines[2]);
		Double prixV = Double.parseDouble(lines[3]);
		
		Double prixMinA = plugin.prixMinA.get(lines[1]);
		Double prixMaxA = plugin.prixMaxA.get(lines[1]);
		
		Double prixMinV = plugin.prixMinV.get(lines[1]);
		Double prixMaxV = plugin.prixMaxV.get(lines[1]);
		
		if(prixMinA != null && prixMaxA != null && prixMinV != null && prixMaxV != null)
		{
			if(((prixA > prixMaxA || prixA < prixMinA) && prixA != -1) || ((prixV > prixMaxV || prixV < prixMinV) && prixV != -1))
			{
				plugin.sendMessage(player, "Les prix d achat et/ou de vente sont en dehors des limites fixees !");
				plugin.sendMessage(player, "Les limites d'achat sont : " + prixMinA + " " + ShowShop.getInstance().getEconomy().currencyNamePlural() + " pour le prix min et " + prixMaxA + " " + ShowShop.getInstance().getEconomy().currencyNamePlural() + " pour le prix max.");
				plugin.sendMessage(player, "Les limites de revente sont : " + prixMinV + " " + ShowShop.getInstance().getEconomy().currencyNamePlural() + " pour le prix min et " + prixMaxV + " " + ShowShop.getInstance().getEconomy().currencyNamePlural() + " pour le prix max.");
				event.getBlock().breakNaturally();
				return;
			}
		}
		
		Location loc = isPiston.getLocation();
		Shop shop = null;
		
		if(lines[0].contains("#") && plugin.getPermissionHandler().has(player, "admin", true))
			shop = new ShopInfinite(loc, player.getName(), lines[1], prixA, prixV, -1, taille);
		else
			shop = new ShopFinite(loc, player.getName(), lines[1], prixA, prixV, 0, taille);
		
		plugin.listshop.add(shop);
		
		plugin.sendMessage(player, "Vous avez cree un magasin.");
		
		// Texte AsSIGNement
		event.setLine(0, ChatColor.AQUA      + "Par " + shop.getTaillelot());
		event.setLine(1, ChatColor.GOLD      + plugin.toTextNormal(shop.getItem().getItemName()));
		
		if(shop.getPrixA() != -1)
			event.setLine(2, ChatColor.GREEN     + "A : " + shop.getPrixA());
		else
			event.setLine(2, ChatColor.GREEN     + "Pas d'achat");
		
		if(shop.getPrixA() != -1)
			event.setLine(3, ChatColor.DARK_RED  + "V : " + shop.getPrixV());
		else
			event.setLine(3, ChatColor.DARK_RED  + "Pas de revente");
		
		// Don't forget to save :)
		plugin.data.saveShop();
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlocBreak(BlockBreakEvent event)
	{
		Block bloc = event.getBlock();
		Player p = event.getPlayer();
		Location loc = bloc.getLocation();
		
		// Gestion d'erreurs
		if(bloc.getType() == Material.PISTON_BASE)
		{
			Vector top = new Vector(0, 1, 0);
			loc = event.getBlock().getLocation().add(top);
		}
		else if(bloc.getType() == Material.PISTON_EXTENSION)
		{
			Vector bottom = new Vector(0, 1, 0);
			loc = event.getBlock().getLocation().add(bottom);
		}
		
		if((SSManager.isShop(bloc.getLocation()) == true || SSManager.isShop(loc) == true)) // Protection de l'étalage
		{
			plugin.sendMessage(p, "Vous ne pouvez pas casser le piston d'un magasin !");
			event.setCancelled(true);
			return;
		}
		
		// Traitement de l'event
		try
		{
			Block shopBloc = SSManager.getBlockByWallSign(bloc);
			Shop shop = SSManager.getShop(shopBloc.getLocation());
			
			if(shop.IsOwner(p.getName()) || plugin.getPermissionHandler().has(p, "admin", true))
			{	
				// Enlever l'item
				SSItem item = shop.getItem();
				plugin.showItem.remove(item);
				plugin.itemsByDrop.remove(item.getItem().getEntityId());
				item.removeItem();
				
				plugin.listshop.remove(shop);
				plugin.data.saveShop();  // Don't forget to save :)
				
				Vector v = shop.getLoc().toVector();
				v.add(new Vector(0.5D, 2D, 0.5D));
				
				if(shop.getNbObjet() != 0)
				{
					ItemStack is = SSItem.get(shop.getItem().getId());
					is.setAmount(shop.getNbObjet());
					p.getInventory().addItem(is);
				}
				
				plugin.sendMessage(p, "Vous avez supprime votre magasin.");
			}
			else
			{
				plugin.sendMessage(p, "Vous n etes pas le createur de ce magasin !");
				event.setCancelled(true);
				return;
			}
		}
		catch (Exception e) {return;}
	}
}
