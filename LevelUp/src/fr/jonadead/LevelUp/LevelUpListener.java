package fr.jonadead.LevelUp;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;

public class LevelUpListener implements Listener{
	
	private LevelUp plugin;
	Logger log = Logger.getLogger("Minecraft");
	private boolean levelchangebyplugin = false;
	
	public LevelUpListener(LevelUp plugin){
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerLevelChange(PlayerLevelChangeEvent e){
		Player p = e.getPlayer();
		
	    if(e.getOldLevel() < e.getNewLevel()){
	    	if(!levelchangebyplugin){
	    		p.sendMessage(ChatColor.GOLD + "Level Up !");
	    	}
	    	else levelchangebyplugin = false;
	    }

	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityDeath(EntityDeathEvent e){
        if(e.getEntity() instanceof Player){
        	Player p = (Player) e.getEntity();
        	p.sendMessage(ChatColor.GOLD + "Vous êtes mort ! Vos levels ont étés reset !");
        }  
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent e){
		
		Player p = e.getPlayer();
		String message = e.getMessage();
		
		String[] params = message.split(" ");
		
		if(params[0].equalsIgnoreCase("/level")){
			e.setCancelled(true);
			log.info("[PLAYER_COMMAND] " + p.getName() + ": " + message);
			
			if(params.length == 2){
				
				if(params[1].equalsIgnoreCase("see")){
				
					if(!plugin.hasPermission(p, "LevelUp.level.see")){
						p.sendMessage(ChatColor.RED + "Vous n'avez pas les permissions pour cette commande !");
						return;
					}
				
					p.sendMessage(ChatColor.GREEN + "Vous avez: " + ChatColor.WHITE + p.getLevel() + " Level" + (p.getLevel() > 1 ? "s":""));
					return;
				}
				
				else if(params[1].equalsIgnoreCase("up")){
					
					if(!plugin.hasPermission(p, "LevelUp.level.up")){
						p.sendMessage(ChatColor.RED + "Vous n'avez pas les permissions pour cette commande !");
						return;
					}
					
					setlevel(p.getLevel() + 1, p);
					p.sendMessage(ChatColor.GREEN + "Vous avez gagné un level !");
					return;
				}
				
				else if(params[1].equalsIgnoreCase("down")){
					if(!plugin.hasPermission(p, "LevelUp.level.down")){
						p.sendMessage(ChatColor.RED + "Vous n'avez pas les permissions pour cette commande !");
						return;
					}
					
					setlevel(p.getLevel() - 1, p);
					
					if(p.getLevel()<0){
						p.sendMessage(ChatColor.RED + "Nombre de level invalide !");
						return;
					}
					
					p.sendMessage(ChatColor.GREEN + "Vous avez perdu un level !");
					return;
				}
				
				else if(params[1].equalsIgnoreCase("xp")){
					
					if(!plugin.hasPermission(p, "LevelUp.level.xp")){
						p.sendMessage(ChatColor.RED + "Vous n'avez pas les permissions pour cette commande !");
						return;
					}
					
					p.sendMessage(ChatColor.GREEN + "Vous avez " + ChatColor.WHITE + p.getExp() + " Xp" + (p.getExp() > 1 ? "s":""));
					return;
				}
				
			}
			
			if(params.length == 3){
				
				if(params[1].equalsIgnoreCase("set")){
					
					if(!plugin.hasPermission(p, "LevelUp.level.set")){
						p.sendMessage(ChatColor.RED + "Vous n'avez pas les permissions pour cette commande !");
						return;
					}

					setlevel(params[2], p, p);
					
					if(p.getLevel()<0){
						p.sendMessage(ChatColor.RED + "Nombre de level invalide !");
						return;
					}
					
					p.sendMessage(ChatColor.GREEN + "Vous êtes au level " + p.getLevel());
					return;
				}
				
				else if(params[1].equalsIgnoreCase("give")){
					
					if(!plugin.hasPermission(p, "LevelUp.level.give")){
						p.sendMessage(ChatColor.RED + "Vous n'avez pas les permissions pour cette commande !");
						return;
					}
				
					setlevel(params[2], p, p);
					
					if(p.getLevel() < 0){
						p.sendMessage(ChatColor.RED + "Nombre de level invalide !");
						return;
					}
					
					p.sendMessage(ChatColor.GREEN + "Vous êtes au level "+ p.getLevel());
					return;
				}
				
				else if(params[1].equalsIgnoreCase("see")){
					
					if(!plugin.hasPermission(p, "LevelUp.level.see.other")){
						p.sendMessage(ChatColor.RED + "Vous n'avez pas les permissions pour cette commande !");
						return;
					}
					
					if(Bukkit.getOfflinePlayer(params[2]) != null){
						OfflinePlayer player = Bukkit.getOfflinePlayer(params[2]);
						
						if(player.getPlayer() != null){
							 int playerlevel = player.getPlayer().getLevel();
							 p.sendMessage(ChatColor.DARK_RED + player.getName() + ChatColor.GREEN + " a : " + ChatColor.WHITE + playerlevel + " Level" + (playerlevel > 1 ? "s":""));
						 }
						 else{
						 	 p.sendMessage(ChatColor.DARK_RED + player.getName() + ChatColor.RED + " n'est pas connecté ou n'existe pas.");
						 }
					}
					return;
				}
				
				else if(params[1].equalsIgnoreCase("up")){
					if(!plugin.hasPermission(p, "LevelUp.level.up.other")){
						p.sendMessage(ChatColor.RED + "Vous n'avez pas les permissions pour cette commande !");
						return;
					}
				
				
					if(Bukkit.getOfflinePlayer(params[2]) != null){
						OfflinePlayer player = Bukkit.getOfflinePlayer(params[2]);
						setlevel(((Player) player).getLevel() + 1, p);
							
						((Player) player).sendMessage(ChatColor.DARK_RED + p.getName() + ChatColor.GREEN + " vous a fait gagner un level !");
						p.sendMessage(ChatColor.DARK_RED + player.getName() + ChatColor.GREEN + " a gagné un level !");
					}
					return;
				}
				
				else if(params[1].equalsIgnoreCase("down")){
					if(!plugin.hasPermission(p, "LevelUp.level.down.other")){
						p.sendMessage(ChatColor.RED + "Vous n'avez pas les permissions pour cette commande !");
						return;
					}
				
					if(Bukkit.getOfflinePlayer(params[2]) != null){
						OfflinePlayer player = Bukkit.getOfflinePlayer(params[2]);
					
						setlevel(((Player) player).getLevel() - 1, p);
						
						if(((Player) player).getLevel()<0){
							p.sendMessage(ChatColor.RED + "Nombre de level invalide !");
							return;
						}
						
						((Player) player).sendMessage(ChatColor.DARK_RED + p.getName() + ChatColor.GREEN + " vous a fait perdre un level !");
						p.sendMessage(ChatColor.DARK_RED + player.getName() + ChatColor.GREEN + " a perdu un level !");
					}
					return;
				}
				
				else if(params[1].equalsIgnoreCase("xp")){
					if(!plugin.hasPermission(p, "LevelUp.level.xp.other")){
						p.sendMessage(ChatColor.RED + "Vous n'avez pas les permissions pour cette commande !");
						return;
					}
				
				
					if(Bukkit.getOfflinePlayer(params[2]) != null){
						OfflinePlayer player = Bukkit.getOfflinePlayer(params[2]);
						
						 if(player.getPlayer() != null){
							 p.sendMessage(ChatColor.DARK_RED + player.getName() +" a " + ChatColor.WHITE + p.getExp() + " Xp" + (p.getExp() > 1 ? "s":""));
						 }else{
							 p.sendMessage(ChatColor.DARK_RED + player.getName() + ChatColor.RED + " n'est pas connecté ou n'existe pas.");
						}
					}
					return;
				}
			}
		
			if(params.length == 4){
				if(params[1].equalsIgnoreCase("set")){
					
					if(!plugin.hasPermission(p, "LevelUp.level.set.other")){
						p.sendMessage(ChatColor.RED + "Vous n'avez pas les permissions pour cette commande !");
						return;
					}
					
					if (Bukkit.getOfflinePlayer(params[2]) != null){
						
						OfflinePlayer player = Bukkit.getOfflinePlayer(params[2]);
						setlevel(params[3], player, p);
							
						if(((Player) player).getLevel() < 0){
							p.sendMessage(ChatColor.RED + "Nombre de level invalide !");
							return;
						}
							
						((Player) player).sendMessage(ChatColor.DARK_RED + p.getName() + ChatColor.GREEN + " vous a mis au level " + ChatColor.WHITE + ((Player) player).getLevel());
						p.sendMessage(ChatColor.DARK_RED + player.getName() + ChatColor.GREEN + " est maintenant au level "+ ChatColor.WHITE + ((Player) player).getLevel());
					}
					return;
				}
				
				else if(params[1].equalsIgnoreCase("give")){
					
					if(!plugin.hasPermission(p, "LevelUp.level.give.other")){
						p.sendMessage(ChatColor.RED + "Vous n'avez pas les permissions pour cette commande !");
						return;
					}
				
				
					if (Bukkit.getOfflinePlayer(params[2]) != null){
						OfflinePlayer player = Bukkit.getOfflinePlayer(params[2]);
						setlevel(params[3], player, p);
						
						if(((Player) player).getLevel() < 0){
							p.sendMessage(ChatColor.RED + "Nombre de level invalide !");
							return;
						}
						
						((Player) player).sendMessage(ChatColor.DARK_RED + p.getName() + ChatColor.GREEN + " vous a mis au level " + ChatColor.WHITE + ((Player) player).getLevel());
						p.sendMessage(ChatColor.DARK_RED + player.getName() + ChatColor.GREEN + " est maintenant au level "+ ChatColor.WHITE + ((Player) player).getLevel());
					}
					return;
				}
			}
		}
		p.sendMessage(ChatColor.RED + "Commande Invalide ! Tapez /help LevelUp pour voir la liste des commandes.");
	}
	
	public void setlevel(String level, OfflinePlayer player, Player sender){
		int levelint;
		try{
			levelint = Integer.valueOf(level);
		}
		catch (Exception exception){
			levelint = -1;
		}
		if(player.getPlayer() != null){
			setlevel(levelint, (Player) player);
		}
		else{
			sender.sendMessage(ChatColor.DARK_RED + player.getName() + ChatColor.RED + " n'est pas connecté ou n'existe pas.");
		}
	}
	
	public void setlevel(int level, Player p){
		if(level<0) return;
		levelchangebyplugin = true;
		p.setLevel(level);
	}
}