package fr.jonadead.LevelUp;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
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
	private boolean LevelIsSet;
	private FileConfiguration config;
	
	
	public LevelUpListener(LevelUp plugin){
		this.plugin = plugin;
		config = plugin.getConfig();
		checkconfig();
	}


	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerLevelChange(PlayerLevelChangeEvent e){
		Player p = e.getPlayer();
		
	    if(e.getOldLevel() < e.getNewLevel()){
	    	if(!levelchangebyplugin){
	    		p.sendMessage(config.getString("LevelUp"));
	    	}
	    	else levelchangebyplugin = false;
	    }

	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityDeath(EntityDeathEvent e){
        if(e.getEntity() instanceof Player){
        	Player p = (Player) e.getEntity();
        	p.sendMessage(config.getString("PlayerDeath"));
        }  
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent e){
		
		Player p = e.getPlayer();
		String message = e.getMessage();
		
		String[] params = message.split(" ");
		
		if(params[0].equalsIgnoreCase("/reload")){
			e.setCancelled(true);
			log.info("[PLAYER_COMMAND] " + p.getName() + ": " + message);
			
			if(params.length == 2){
				
				if(params[1].equalsIgnoreCase("levelup")){
					
					if(!plugin.hasPermission(p, "LevelUp.reload")){
						p.sendMessage(config.getString("Errors.noperms"));
						return;
					}
					plugin.reloadConfig();
					p.sendMessage(ChatColor.GREEN + "LevelUp reload !");
					return;
				}
			}
		}
		
		if(params[0].equalsIgnoreCase("/level")){
			e.setCancelled(true);
			log.info("[PLAYER_COMMAND] " + p.getName() + ": " + message);
			
			if(params.length == 2){
				
				if(params[1].equalsIgnoreCase("see")){
				
					if(!plugin.hasPermission(p, "LevelUp.level.see")){
						p.sendMessage(config.getString("Errors.noperms"));
						return;
					}
				
					p.sendMessage(config.getString("Level.see.self").replace("{LEVEL}", "" + p.getLevel()));
					return;
				}
				
				else if(params[1].equalsIgnoreCase("up")){
					
					if(!plugin.hasPermission(p, "LevelUp.level.up")){
						p.sendMessage(config.getString("Errors.noperms"));
						return;
					}
					
					setlevel(p.getLevel() + 1, p);
					p.sendMessage(config.getString("Level.up.self"));
					return;
				}
				
				else if(params[1].equalsIgnoreCase("down")){
					if(!plugin.hasPermission(p, "LevelUp.level.down")){
						p.sendMessage(config.getString("Errors.noperms"));
						return;
					}
					
					setlevel(p.getLevel() - 1, p);
					
					if(!LevelIsSet){
						p.sendMessage(ChatColor.RED + "Nombre de level invalide !");
						return;
					}
					
					p.sendMessage(config.getString("Level.down.self"));
					return;
				}
				
				else if(params[1].equalsIgnoreCase("xp")){
					
					if(!plugin.hasPermission(p, "LevelUp.level.xp")){
						p.sendMessage(config.getString("Errors.noperms"));
						return;
					}
					
					p.sendMessage(config.getString("Level.xp.self").replace("{XP}", "" + p.getExp()));
					return;
				}
				
			}
			
			if(params.length == 3){
				
				if(params[1].equalsIgnoreCase("set")){
					
					if(!plugin.hasPermission(p, "LevelUp.level.set")){
						p.sendMessage(config.getString("Errors.noperms"));
						return;
					}

					setlevel(params[2], p, p);
					
					if(!LevelIsSet){
						p.sendMessage(ChatColor.RED + "Nombre de level invalide !");
						return;
					}
					
					p.sendMessage(config.getString("Level.set.self").replace("{LEVEL}", "" + p.getLevel()));
					return;
				}
				
				else if(params[1].equalsIgnoreCase("give")){
					
					if(!plugin.hasPermission(p, "LevelUp.level.give")){
						p.sendMessage(config.getString("Errors.noperms"));
						return;
					}
				
					setlevel(params[2], p, p);
					
					if(!LevelIsSet){
						p.sendMessage(ChatColor.RED + "Nombre de level invalide !");
						return;
					}
					
					p.sendMessage(config.getString("Level.give.self").replace("{LEVEL}", "" + p.getLevel()));
					return;
				}
				
				else if(params[1].equalsIgnoreCase("see")){
					
					if(!plugin.hasPermission(p, "LevelUp.level.see.other")){
						p.sendMessage(config.getString("Errors.noperms"));
						return;
					}
					
					if(Bukkit.getOfflinePlayer(params[2]) != null){
						OfflinePlayer player = Bukkit.getOfflinePlayer(params[2]);
						
						if(player.getPlayer() != null){
							 int playerlevel = player.getPlayer().getLevel();
							 p.sendMessage(config.getString("Level.see.others").replace("{PLAYER}", "" + player.getName()).replace("{LEVEL}", "" + playerlevel));
						 }
						 else{
						 	 p.sendMessage(config.getString("Errors.notconnectedplayer").replace("{PLAYER}", "" + player.getName()));
						 }
					}
					return;
				}
				
				else if(params[1].equalsIgnoreCase("up")){
					if(!plugin.hasPermission(p, "LevelUp.level.up.other")){
						p.sendMessage(config.getString("Errors.noperms"));
						return;
					}
				
				
					if(Bukkit.getOfflinePlayer(params[2]) != null){
						OfflinePlayer player = Bukkit.getOfflinePlayer(params[2]);
						
							
						if(player.getPlayer() != null){
							setlevel(player.getPlayer().getLevel() + 1, p);
							player.getPlayer().sendMessage(config.getString("Level.up.others.receiver").replace("{PLAYER}", "" + p.getName()));
							p.sendMessage(config.getString("Level.up.others.sender").replace("{PLAYER}", "" + player.getName()));
						}else{
							 p.sendMessage(config.getString("Errors.notconnectedplayer").replace("{PLAYER}", "" + player.getName()));
						}
						
					}
					return;
				}
				
				else if(params[1].equalsIgnoreCase("down")){
					if(!plugin.hasPermission(p, "LevelUp.level.down.other")){
						p.sendMessage(config.getString("Errors.noperms"));
						return;
					}
				
					if(Bukkit.getOfflinePlayer(params[2]) != null){
						OfflinePlayer player = Bukkit.getOfflinePlayer(params[2]);
						
						if(player.getPlayer() != null){
							setlevel(player.getPlayer().getLevel() - 1, p);
							if(!LevelIsSet){
								p.sendMessage(ChatColor.RED + "Nombre de level invalide !");
								return;
							}
							player.getPlayer().sendMessage(config.getString("Level.down.others.receiver").replace("{PLAYER}", "" + p.getName()));
							p.sendMessage(config.getString("Level.down.others.sender").replace("{PLAYER}", "" + player.getName()));
						}else{
							 p.sendMessage(config.getString("Errors.notconnectedplayer").replace("{PLAYER}", "" + player.getName()));
						}
						
					}
					return;
				}
				
				else if(params[1].equalsIgnoreCase("xp")){
					if(!plugin.hasPermission(p, "LevelUp.level.xp.other")){
						p.sendMessage(config.getString("Errors.noperms"));
						return;
					}
				
				
					if(Bukkit.getOfflinePlayer(params[2]) != null){
						OfflinePlayer player = Bukkit.getOfflinePlayer(params[2]);
						
						 if(player.getPlayer() != null){
							 p.sendMessage(config.getString("Level.xp.others").replace("{PLAYER}", "" + player.getName()).replace("{XP}", "" + player.getPlayer().getExp()));
						 }else{
							 p.sendMessage(config.getString("Errors.notconnectedplayer").replace("{PLAYER}", "" + player.getName()));
						}
					}
					return;
				}
			}
		
			if(params.length == 4){
				if(params[1].equalsIgnoreCase("set")){
					
					if(!plugin.hasPermission(p, "LevelUp.level.set.other")){
						p.sendMessage(config.getString("Errors.noperms"));
						return;
					}
					
					if (Bukkit.getOfflinePlayer(params[2]) != null){
						
						OfflinePlayer player = Bukkit.getOfflinePlayer(params[2]);
												
						if(player.getPlayer() != null){
							
							setlevel(params[3], player, p);
							
							if(!LevelIsSet){
								p.sendMessage(ChatColor.RED + "Nombre de level invalide !");
								return;
							}

							
							player.getPlayer().sendMessage(config.getString("Level.set.others.receiver").replace("{PLAYER}", "" + p.getName()).replace("{LEVEL}", "" + player.getPlayer().getLevel()));
							p.sendMessage(config.getString("Level.set.others.sender").replace("{PLAYER}", "" + p.getName()).replace("{LEVEL}", "" + player.getPlayer().getLevel()));
						}else{
							 p.sendMessage(config.getString("Errors.notconnectedplayer").replace("{PLAYER}", "" + player.getName()));
						}
					}
					return;
				}
				
				else if(params[1].equalsIgnoreCase("give")){
					
					if(!plugin.hasPermission(p, "LevelUp.level.give.other")){
						p.sendMessage(config.getString("Errors.noperms"));
						return;
					}
				
				
					if (Bukkit.getOfflinePlayer(params[2]) != null){
						OfflinePlayer player = Bukkit.getOfflinePlayer(params[2]);
						
						if(player.getPlayer() != null){
							
							setlevel(params[3], player, p);
							if(!LevelIsSet){
								p.sendMessage(ChatColor.RED + "Nombre de level invalide !");
								return;
							}
							
							player.getPlayer().sendMessage(config.getString("Level.give.others.receiver").replace("{PLAYER}", "" + p.getName()).replace("{LEVEL}", "" + player.getPlayer().getLevel()));
							p.sendMessage(config.getString("Level.set.others.sender").replace("{PLAYER}", "" + p.getName()).replace("{LEVEL}", "" + player.getPlayer().getLevel()));
						}else{
							 p.sendMessage(config.getString("Errors.notconnectedplayer").replace("{PLAYER}", "" + player.getName()));
						}
						
					}
					return;
				}
			}
			p.sendMessage(config.getString("Errors.invalidcommand").replace("{COMMAND}", "/help LevelUp"));
		}
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
			sender.sendMessage(config.getString("Errors.notconnectedplayer").replace("{PLAYER}", "" + player.getName()));
		}
	}
	
	public void setlevel(int level, Player p){
		if(level < 0 || level > 32767){
			LevelIsSet = false;
			return;
		}else LevelIsSet = true;
		levelchangebyplugin = true;
		p.setLevel(level);
	}
	
	private void checkconfig() {
		
		if(config.getString("LevelUp") == null){
			config.set("LevelUp", ChatColor.GOLD + "Level Up !");
		}
		
		if(config.getString("PlayerDeath") == null){
			config.set("PlayerDeath", ChatColor.GOLD + "Vous êtes mort ! Vos levels ont étés reset !");
		}
		
		if(config.getString("Level.see.self") == null){
			config.set("Level.see.self", ChatColor.GREEN + "Vous avez : " + ChatColor.WHITE + "{LEVEL}" + ChatColor.GREEN + " Level.");
		}
		
		if(config.getString("Level.up.self") == null){
			config.set("Level.up.self", ChatColor.GREEN + "Vous avez gagné un level !");
		}
		
		if(config.getString("Level.down.self") == null){
			config.set("Level.down.self", ChatColor.GREEN + "Vous avez perdu un level !");
		}
		
		if(config.getString("Level.xp.self") == null){
			config.set("Level.xp.self", ChatColor.GREEN + "Vous avez " + ChatColor.WHITE + "{XP}" + ChatColor.GREEN + " Xp.");
		}
		
		if(config.getString("Level.set.self") == null){
			config.set("Level.set.self", ChatColor.GREEN + "Vous êtes maintenant au level " + ChatColor.WHITE + "{LEVEL}" + ChatColor.GREEN + ".");
		}
		
		if(config.getString("Level.give.self") == null){
			config.set("Level.give.self", ChatColor.GREEN + "Vous êtes maintenant au level " + ChatColor.WHITE + "{LEVEL}" + ChatColor.GREEN + ".");
		}
		
		if(config.getString("Level.see.others") == null){
			config.set("Level.see.others", ChatColor.DARK_RED + "{PLAYER}" + ChatColor.GREEN + " a : " + ChatColor.WHITE + "{LEVEL}" + ChatColor.GREEN +" Level");
		}
		
		if(config.getString("Level.up.others.sender") == null){
			config.set("Level.up.others.sender", ChatColor.DARK_RED + "{PLAYER}" + ChatColor.GREEN + " a gagné un level !");
		}
		
		if(config.getString("Level.up.others.receiver") == null){
			config.set("Level.up.others.receiver",  ChatColor.DARK_RED + "{PLAYER}" + ChatColor.GREEN + " vous a fait gagner un level !");
		}
		
		if(config.getString("Level.down.others.sender") == null){
			config.set("Level.down.others.sender", ChatColor.DARK_RED + "{PLAYER}" + ChatColor.GREEN + " a perdu un level !");
		}
		
		if(config.getString("Level.down.others.receiver") == null){
			config.set("Level.down.others.receiver", ChatColor.DARK_RED + "{PLAYER}" + ChatColor.GREEN + " vous a fait perdre un level !");
		}
		
		if(config.getString("Level.xp.others") == null){
			config.set("Level.xp.others", ChatColor.DARK_RED + "{PLAYER}" +  ChatColor.GREEN + " a " + ChatColor.WHITE + "{XP}" +  ChatColor.GREEN +" Xp.");
		}
		
		if(config.getString("Level.set.others.sender") == null){
			config.set("Level.set.others.sender", ChatColor.DARK_RED + "{PLAYER}" + ChatColor.GREEN + " est maintenant au level "+ ChatColor.WHITE + "{LEVEL}" + ChatColor.GREEN + ".");
		}
		
		if(config.getString("Level.set.others.receiver") == null){
			config.set("Level.set.others.receiver", ChatColor.DARK_RED + "{PLAYER}" + ChatColor.GREEN + " vous a mis au level " + ChatColor.WHITE + "{LEVEL}" + ChatColor.GREEN + ".");
		}
		
		if(config.getString("Level.give.others.sender") == null){
			config.set("Level.give.others.sender", ChatColor.DARK_RED + "{PLAYER}" + ChatColor.GREEN + " est maintenant au level "+ ChatColor.WHITE + "{LEVEL}" + ChatColor.GREEN + ".");
		}
		
		if(config.getString("Level.give.others.receiver") == null){
			config.set("Level.give.others.receiver", ChatColor.DARK_RED + "{PLAYER}" + ChatColor.GREEN + " vous a mis au level " + ChatColor.WHITE + "{LEVEL}" + ChatColor.GREEN + ".");
		}
		
		if(config.getString("Errors.noperms") == null){
			config.set("Errors.noperms", ChatColor.RED + "Vous n'avez pas les permissions pour cette commande !");
		}
		
		if(config.getString("Errors.notconnectedplayer") == null){
			config.set("Errors.notconnectedplayer", ChatColor.DARK_RED + "{PLAYER}" + ChatColor.RED + " n'est pas connecté ou n'existe pas.");
		}
		
		if(config.getString("Errors.invalidcommand") == null){
			config.set("Errors.invalidcommand", ChatColor.RED + "Commande Invalide ! Tapez {COMMAND} pour voir la liste des commandes.");
		}
		
		plugin.saveConfig();
	}
}