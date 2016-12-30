package user;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import utils.ArmorType;
import utils.Chat.C;
import core.Core;
import faction.FChunk;
import faction.Faction;

public class UserManager implements Listener {

	private static String CHAT_FORMAT = "<rank> <player>&7: <message>";

	private static int DEFAULT_CHAT_COOLDOWN = 3*20;

	private ArrayList<User> users = new ArrayList<User>();

	public UserManager() {

	}

	public User getUser(Player player) {
		for (User user : users) {
			if (user.getPlayer().equals(player)) {
				return user;
			}
		}
		return null;
	}

	public ArrayList<User> getUsers() {
		return users;
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent event){
		if(shouldCancelEvent(event.getPlayer(), event.getBlock().getLocation()))
			event.setCancelled(true);
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onLaunch(ProjectileLaunchEvent event){
		if(event.getEntity() instanceof EnderPearl){
			EnderPearl pearl = (EnderPearl) event.getEntity();
			if(pearl.getShooter() instanceof Player){
				Player player = (Player) pearl.getShooter();
				User user = Core.getInstance().getUserManager().getUser(player);
				user.addCooldown("enderpearl_cooldown", 20*15);
			}
		}
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent event){
		if(event.getWhoClicked() instanceof Player){
		if(Core.getInstance().getUserManager().getUser((Player)event.getWhoClicked()).isFrozen()){
			event.setCancelled(true);
		}
		}
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent event){
		if(Core.getInstance().getUserManager().getUser(event.getPlayer()).isFrozen()){
			if(event.getTo().getX() != event.getFrom().getX() || event.getTo().getY() != event.getFrom().getY() ||
					event.getTo().getZ() != event.getFrom().getZ()){
				event.getPlayer().teleport(event.getFrom());
			}
		}
	}
	
	@EventHandler
	public void onPickup(PlayerPickupItemEvent event){
		if(Core.getInstance().getUserManager().getUser(event.getPlayer()).isFrozen()){
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onFrozenInteract(PlayerInteractEvent event){
		if(Core.getInstance().getUserManager().getUser(event.getPlayer()).isFrozen()){
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onDamaged(EntityDamageEvent event){
		if(event.getEntity() instanceof Player){
		if(Core.getInstance().getUserManager().getUser((Player) event.getEntity()).isFrozen()){
			event.setCancelled(true);
		}
		}
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent event){
		
		User user = Core.getInstance().getUserManager().getUser(event.getPlayer());
		
		FChunk fChunk = new FChunk(event.getClickedBlock().getLocation());
		
		if(fChunk.isClaimed() && Core.getInstance().getUserManager().getUser(event.getPlayer()).hasFaction() &&
				fChunk.whoOwns().equals(Core.getInstance().getUserManager().getUser(event.getPlayer()).getFaction())){
		if(event.getClickedBlock().getState() instanceof Sign){
			Sign s = (Sign) event.getClickedBlock().getState();
			if(s.getLine(0).equalsIgnoreCase("[elevator]")){
				if(s.getLine(1).equalsIgnoreCase("down")){
					s.setLine(0, ChatColor.DARK_GREEN + "[Elevator]");
					s.setLine(1, "Down");
					s.update();
				}
				if(s.getLine(1).equalsIgnoreCase("up")){
					s.setLine(0, ChatColor.DARK_GREEN + "[Elevator]");
					s.setLine(1, "Up");
					s.update();
				}
			}
		}
		}
		
		if(fChunk.isClaimed() && Core.getInstance().getUserManager().getUser(event.getPlayer()).hasFaction() &&
				fChunk.whoOwns().equals(Core.getInstance().getUserManager().getUser(event.getPlayer()).getFaction())){
			if(!Core.getInstance().getUserManager().getUser(event.getPlayer()).hasCooldown("pvptimer")){
			if(event.getClickedBlock().getState() instanceof Sign){
				Sign s = (Sign) event.getClickedBlock().getState();
				if(s.getLine(0).equals(ChatColor.DARK_GREEN + "[Elevator]")){
					if(s.getLine(1).equals("Down")){
						for(int i = event.getClickedBlock().getLocation().getBlockY(); i >= 0; i--){
							Location loc = event.getClickedBlock().getLocation().clone();
							loc.setY(i);
							if(loc.getBlock().getState() instanceof Sign){
								Sign r = (Sign) loc.getBlock().getState();
								if(s.getLine(0).equals(ChatColor.DARK_GREEN + "[Elevator]")){
									if(r.getLine(1).equals("Up")){
										event.getPlayer().teleport(loc);
									}
								}
							}
						}
					}
					if(s.getLine(1).equals("Up")){
						for(int i = event.getClickedBlock().getLocation().getBlockY(); i <= 256; i++){
							Location loc = event.getClickedBlock().getLocation().clone();
							loc.setY(i);
							if(loc.getBlock().getState() instanceof Sign){
								Sign r = (Sign) loc.getBlock().getState();
								if(s.getLine(0).equals(ChatColor.DARK_GREEN + "[Elevator]")){
									if(r.getLine(1).equals("Down")){
										event.getPlayer().teleport(loc);
									}
								}
							}
						}
					}
				}
			}
			}
		}
		
		List<Material> blocked = Arrays.asList(Material.CHEST, Material.WOOD_BUTTON, Material.STONE_BUTTON, Material.LEVER,
				Material.TRAPPED_CHEST, Material.BREWING_STAND, Material.HOPPER, Material.REDSTONE_COMPARATOR, Material.DIODE,
				Material.TRAP_DOOR, Material.TRIPWIRE_HOOK, Material.TRIPWIRE, Material.STONE_PLATE, Material.WOOD_PLATE,
				Material.GOLD_PLATE, Material.IRON_PLATE, Material.WOODEN_DOOR, Material.FENCE_GATE, Material.DROPPER, Material.DISPENSER, Material.FURNACE,
				Material.CAULDRON);
		if(blocked.contains(event.getClickedBlock().getType())){
			if(this.shouldCancelEvent(event.getPlayer(), event.getClickedBlock().getLocation())){
				event.setCancelled(true);
			}
		}
		
		if(event.getPlayer().getItemInHand().getType().equals(Material.ENDER_PEARL)){
			if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
				event.setCancelled(true);
				event.getPlayer().updateInventory();
				return;
			}
			if(user.getActiveCooldowns().contains("enderpearl_cooldown")){
				event.getPlayer().sendMessage(C.SECONDARY + "You are on pearl cooldown for another " + C.PRIMARY + (int)(user.getCooldown("enderpearl_cooldown")/20) + "s" + C.SECONDARY + "!");
				event.getPlayer().updateInventory();
				event.setCancelled(true);
				return;
			}
		}
	}
	
	@EventHandler
	public void onPlace(BlockPlaceEvent event){
		if(this.shouldCancelEvent(event.getPlayer(), event.getBlock().getLocation())){
			event.setCancelled(true);
		}
	}
	
	
	private boolean shouldCancelEvent(Player player, Location location){
		if(Core.getInstance().getUserManager().getUser(player).isFrozen())
			return true;
		FChunk chunk = new FChunk(location);
		User user = Core.getInstance().getUserManager().getUser(player);
		if(chunk.isClaimed()){
			if(!chunk.whoOwns().equals(user.getFaction()) && !chunk.whoOwns().isRaidable()){
				return true;
			}
		}
		return false;
	}
	

	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			Player player = (Player) e.getEntity();
			User user = this.getUser(player);
			for (String s : user.getActiveCooldowns()) {
				if (s.startsWith("teleport_to_")) {
					player.sendMessage(C.SECONDARY
							+ "Your teleportation has been cancelled due to "
							+ C.ERROR_PRIMARY + "damage" + C.SECONDARY + "!");
					user.deleteCooldown(s);
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		if (!Core.getInstance().getUserManager().getUser(event.getPlayer())
				.hasCooldown("chat_cooldown")) {
			if(!Core.getInstance().getChatMuted() || Core.getInstance().getUserManager().getUser(event.getPlayer()).getRank().getId() >= Rank.MODERATOR.getId()){
			if(Core.getInstance().getUserManager().getUser(event.getPlayer()).getRank().getId() < Rank.JRMOD.getId())
				Core.getInstance().getUserManager().getUser(event.getPlayer())
					.addCooldown("chat_cooldown", DEFAULT_CHAT_COOLDOWN);
			String s = CHAT_FORMAT;
			s = ChatColor.translateAlternateColorCodes('&', s);
			s = s.replaceAll("<rank>", Core.getInstance().getUserManager()
					.getUser(event.getPlayer()).getRank().getPrefix());
			s = s.replaceAll("<player>", event.getPlayer().getName());
			s = s.replaceAll("<message>", event.getMessage());
			for (Player player : Bukkit.getServer().getOnlinePlayers()) {
				player.sendMessage(s);
			}
			}else{
				
				event.getPlayer().sendMessage(C.SECONDARY + "Your rank " + Core.getInstance().getUserManager().getUser(event.getPlayer()).getRank().getPrefix() + C.SECONDARY + " does not have permission to chat while chat is muted.");
			}
			event.setCancelled(true);
		}else{
			event.getPlayer().sendMessage(C.SECONDARY + "You must wait "+ C.ERROR_PRIMARY + (int)(DEFAULT_CHAT_COOLDOWN/20) + "s" + C.SECONDARY + " between messages.");
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onDamageWithArcherTag(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player
				&& event.getDamager() instanceof Player) {
			Player hit = (Player) event.getEntity();
			if (this.getUser(hit).hasCooldown("archer_tag")) {
				if (!event.isCancelled()) {
					event.setDamage(event.getDamage() * 1.8);
				}
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void damageOtherPlayer(EntityDamageByEntityEvent event) {
		if(event.getEntity() instanceof Player){
			if(event.getDamager() instanceof Player){
				event.setCancelled(!hit((Player)event.getDamager(), (Player)event.getEntity()));
			}else if(event.getDamager() instanceof Projectile){
				Projectile proj = (Projectile) event.getDamager();
				if(proj.getShooter() instanceof Player){
					event.setCancelled(!hit((Player)proj.getShooter(), (Player)event.getEntity()));
				}
			}
		}
	}

	private boolean hit(Player player, Player hit){
		User playerU = Core.getInstance().getUserManager().getUser(player);
		User hitU = Core.getInstance().getUserManager().getUser(hit);
		if((!playerU.hasFaction() || !hitU.hasFaction()) || !hitU.getFaction().equals(playerU.getFaction())){
			playerU.addCooldown("pvptimer", 20*25);
			hitU.addCooldown("pvptimer", 20*25);
			return true;
		}else{
			player.sendMessage(C.SECONDARY + "You cannot harm " +C.PRIMARY + hit.getName() + C.SECONDARY + "!");
			return false;
		}
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		if(event.getEntity().getKiller() != null)
		event.setDeathMessage(ChatColor.YELLOW + event.getDeathMessage());
		
		Player player = event.getEntity();
		player.getInventory().clear();
		if (Core.getInstance().getUserManager().getUser(player).hasFaction()) {
			Core.getInstance().getUserManager().getUser(player).getFaction()
					.factionDeath();
		}
		player.kickPlayer(ChatColor.RED + "Death banned for 1 hour!");
		// TODO do more here, rank time death bans
	}

	@EventHandler
	public void onTeleportMove(PlayerMoveEvent event) {
		User user = Core.getInstance().getUserManager()
				.getUser(event.getPlayer());
		if (event.getTo().distance(event.getFrom()) > 0) {
			for (String s : user.getActiveCooldowns()) {
				if (s.startsWith("teleport_to_")) {
					event.getPlayer()
							.sendMessage(
									C.SECONDARY
											+ "Your teleportation has been cancelled due to "
											+ C.ERROR_PRIMARY + "movement"
											+ C.SECONDARY + "!");
					user.deleteCooldown(s);
				}
			}
		}
	}

	@EventHandler
	public void onChangeClaims(PlayerMoveEvent event){
		FChunk fC = new FChunk(event.getFrom());
		FChunk tC = new FChunk(event.getTo());
		if(fC.getX() != tC.getX() || fC.getZ() != tC.getZ()){
			if(tC.isClaimed()){
				if(!tC.whoOwns().equals(fC.whoOwns())){
					event.getPlayer().sendMessage(C.SECONDARY + "Entering " + C.PRIMARY + tC.whoOwns().getName() + C.SECONDARY + "!");
				}
			}else{
				if(fC.isClaimed()){
					event.getPlayer().sendMessage(C.SECONDARY + "Entering " + C.PRIMARY + "wilderness" + C.SECONDARY + "!");
				}
			}
		}
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		User u = new User(event.getPlayer());
		users.add(u);
		for (Faction faction : Core.getInstance().getFactionManager()
				.getFactions()) {
			if (faction.getOnlinePlayers().contains(event.getPlayer())) {
				u.setFaction(faction);
			}
		}
		event.setJoinMessage("");

		ArmorType.GOLD.isApplied(event.getPlayer(), true);
		ArmorType.LEATHER.isApplied(event.getPlayer(), true);
		ArmorType.DIAMOND.isApplied(event.getPlayer(), true);
		ArmorType.IRON.isApplied(event.getPlayer(), true);
		ArmorType.CHAIN.isApplied(event.getPlayer(), true);
		
		for(User user : users){
			if(user.isVanished()){
				if(u.getRank().getId() < Rank.JRMOD.getId()){
					event.getPlayer().hidePlayer(user.getPlayer());
				}
			}else{
				if(!event.getPlayer().canSee(user.getPlayer())){
					event.getPlayer().showPlayer(user.getPlayer());
				}
			}
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		leave(Core.getInstance().getUserManager().getUser(event.getPlayer()));
		event.setQuitMessage("");
	}

	@EventHandler
	public void onKick(PlayerKickEvent event) {
		leave(Core.getInstance().getUserManager().getUser(event.getPlayer()));
		event.setLeaveMessage("");
	}

	private void leave(User player) {
		if (player.hasKit()) {
			player.getKit().onRemove(player.getPlayer());
		}
		users.remove(player);
	}

}
