package user;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import classes.Kit;
import core.Core;
import faction.Faction;
import utils.Chat.C;

public class User {

	private String player;

	private Kit kit;
	private String tabName;

	private Faction faction;

	private int cooldownRunnable;

	private boolean vanish = false;
	
	private Rank rank = Rank.DEFAULT;
	
	private boolean frozen;

	private HashMap<String, Location> teleportLocations = new HashMap<String, Location>();

	private HashMap<String, Integer> cooldowns = new HashMap<String, Integer>();

	public User(Player player) {
		this.player = player.getName();
		if(player.isOp())
			rank = Rank.ADMIN;
		setUp(player);
		// Load faction
		hello();
	}

	public User(String player) {
		this.player = player;
		setUp(Core.getInstance().getPlayer(player));
		// Load faction
		hello();
	}
	
	private void setUp(Player player){
		startRemovingCooldown();
		updateTabName();
	}
	
	private void updateTabName(){
		if(vanish){
			this.tabName = ChatColor.GRAY +""+ChatColor.ITALIC + player;
		}else{
			if(this.rank != null && !this.rank.equals(Rank.DEFAULT)){
				this.tabName = ChatColor.getLastColors(rank.getPrefix()) +""+ player;
			}else{
				this.tabName = ChatColor.WHITE + player;
			}
		}
		getPlayer().setPlayerListName(tabName);
	}
	
	public void setFrozen(boolean frozen){
		if(frozen){
			getPlayer().sendMessage("");
			getPlayer().sendMessage("");
			getPlayer().sendMessage(C.SECONDARY + "You have been " + C.PRIMARY + "frozen" + C.SECONDARY + "!");
			getPlayer().sendMessage("");
			getPlayer().sendMessage("");
		}else{
			getPlayer().sendMessage("");
			getPlayer().sendMessage("");
			getPlayer().sendMessage(C.SECONDARY + "You have been " + C.PRIMARY + "unfrozen" + C.SECONDARY + "!");
			getPlayer().sendMessage("");
			getPlayer().sendMessage("");
		}
		this.frozen = frozen;
	}
	
	public boolean isFrozen(){
		return frozen;
	}
	
	public boolean isVanished(){
		return vanish;
	}
	
	public void toggleVanish(){
		vanish = !vanish;
		this.updateTabName();
	}

	public void setKit(Kit kit) {
		this.kit = kit;
	}

	public void removeKit() {
		this.kit = null;
	}

	public boolean hasKit() {
		return kit != null;
	}

	public boolean hasFaction() {
		return faction != null;
	}

	public void leaveFaction() {
		faction = null;
	}

	public void setFaction(Faction faction) {
		this.faction = faction;
	}

	public Faction getFaction() {
		return faction;
	}

	public Rank getRank() {
		return rank;
	}

	public void teleport(Location location, String locationID, int ticks) {
		for (String s : this.getActiveCooldowns()) {
			if (s.startsWith("teleport_to_")) {
				deleteCooldown(s);
			}
		}
		this.teleportLocations.put(locationID, location);
		this.addCooldown("teleport_to_" + locationID, ticks);
	}

	public void quit() {
		Bukkit.getServer().getScheduler().cancelTask(this.cooldownRunnable);
	}

	public void setRank(Rank rank, boolean alert) {
		this.rank = rank;
		if (alert)
			Core.getInstance()
					.getPlayer(player)
					.sendMessage(
							C.SECONDARY + "Your rank has been changed to "
									+ C.PRIMARY + rank.getPrefix()
									+ C.SECONDARY + "!");
		updateTabName();
	}

	public Kit getKit() {
		return kit;
	}

	public void deleteCooldown(String cooldown) {
		if (cooldowns.containsKey(cooldown)) {
			cooldowns.remove(cooldown);
		}
	}

	public ArrayList<String> getActiveCooldowns() {
		ArrayList<String> cds = new ArrayList<String>();
		for (String s : cooldowns.keySet()) {
			cds.add(s);
		}
		return cds;
	}

	private void startRemovingCooldown() {
		cooldownRunnable = Bukkit.getServer().getScheduler()
				.scheduleSyncRepeatingTask(Core.getInstance(), new Runnable() {

					@Override
					public void run() {
						if (cooldowns.keySet() != null) {
							for (String s : cooldowns.keySet()) {
								if (subtractCooldown(s, 2)) {
									if (s.startsWith("kit_warmup_")) {
										s = s.replace("kit_warmup_", "");
										Kit k = Core.getInstance()
												.getKitManager().getKit(s);
										if (k.getArmorType().isApplied(
												Bukkit.getServer().getPlayer(
														player), false)) {
											kit = k;
											kit.onApply(Bukkit.getServer()
													.getPlayer(player));
										}
									}
									if (s.startsWith("teleport_to_")) {
										s = s.replace("teleport_to_", "");
										Core.getInstance()
												.getPlayer(player)
												.teleport(
														teleportLocations
																.get(s));
										Core.getInstance()
												.getPlayer(player)
												.playSound(
														Core.getInstance()
																.getPlayer(
																		player)
																.getLocation(),
														Sound.ENDERMAN_TELEPORT,
														1, 1);
									}
								}
							}
						}
					}

				}, 2, 2);
	}

	/**
	 * Subtract ticks from cool down
	 * 
	 * @return true if cool down is no longer active
	 */
	public boolean subtractCooldown(String string, int amount) {
		if (cooldowns.containsKey(string)) {
			cooldowns.put(string, cooldowns.get(string) - amount);
			if (cooldowns.get(string) < 1) {
				cooldowns.remove(string);
				return true;
			}
		}
		return false;
	}

	public void addCooldown(String string, int time) {
		cooldowns.put(string, time);
	}

	public int getCooldown(String string) {
		if (cooldowns.containsKey(string)) {
			return cooldowns.get(string);
		}
		return 0;
	}

	public boolean hasCooldown(String string) {
		if (cooldowns.containsKey(string)) {
			return true;
		}
		return false;
	}

	private void hello() {
		Player p = Bukkit.getPlayer(player);
		p.sendMessage("");
		p.sendMessage(C.SECONDARY + "You joined " + C.PRIMARY
				+ Core.getInstance().getServerName() + C.SECONDARY + ".");
		p.sendMessage("");
	}

	public Player getPlayer() {
		return Bukkit.getPlayer(player);
	}

}
