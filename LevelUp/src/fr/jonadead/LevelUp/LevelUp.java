package fr.jonadead.LevelUp;

import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import ru.tehkode.permissions.PermissionManager;

public class LevelUp extends JavaPlugin{
	
	@SuppressWarnings("unused")
	private LevelUpListener listener;
	public PermissionManager pManager;
	
	@Override
	public void onDisable() {
		
		System.out.println("[LevelUp] Unloaded !");
		
	}
	
	@Override
	public void onEnable () {
		
		System.out.println("[LevelUp] Loaded !");
		
		PluginManager pm = getServer().getPluginManager();
		
		pm.registerEvents(new LevelUpListener(this), this);
	}
	
	public boolean hasPermission(Player p, String perm){
		if(!p.isOp() && !(pManager != null && pManager.has(p, perm))){
			return false;
		}else{
			return true;
		}
	}


}
