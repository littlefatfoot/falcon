package core;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import classes.KitManager;
import faction.FactionCommand;
import faction.FactionManager;
import user.AdminCommands;
import user.UserManager;
import utils.Chat.C;

public class Core extends JavaPlugin {

	private static Core instance;

	private String name = "FalconHCF";
	private int kitWarmupTicks = 20 * 8;
	private boolean chatMuted = false;

	private UserManager um;
	private KitManager km;
	private FactionManager fm;

	public Core() {

	}

	@SuppressWarnings("deprecation")
	public boolean isOnline(String name) {
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			if (player.getName().equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("deprecation")
	public Player getPlayer(String name) {
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			if (player.getName().equalsIgnoreCase(name)) {
				return player;
			}
		}
		return null;

	}

	public boolean getChatMuted(){
		return chatMuted;
	}
	
	public void toggleChatMute(){
		chatMuted = !chatMuted;
		Bukkit.broadcastMessage(C.SECONDARY + "Chat has been " + C.ERROR_PRIMARY + (chatMuted ? "muted" : "unmuted") + C.SECONDARY + "!");
	}
	
	public String getServerName() {
		return name;
	}

	public int getKitWarmupTicks() {
		return kitWarmupTicks;
	}

	public static Core getInstance() {
		return instance;
	}

	@Override
	public void onEnable() {
		instance = this;

		um = new UserManager();
		km = new KitManager();
		fm = new FactionManager();

		PluginManager pm = Bukkit.getServer().getPluginManager();
		pm.registerEvents(um, this);
		pm.registerEvents(km, this);

		getCommand("f").setExecutor(new FactionCommand());
		getCommand("a").setExecutor(new AdminCommands());
	}

	public UserManager getUserManager() {
		return um;
	}

	public KitManager getKitManager() {
		return km;
	}

	public FactionManager getFactionManager() {
		return fm;
	}

}
