package faction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import core.Core;
import utils.Chat.C;

public class Faction {

	private static int TIME_TO_RESET_SECONDS = 60 * 60;

	private static String FACTION_OWNER_PREFIX = "**";
	private static String FACTION_MODERATOR_PREFIX = "*";
	
	private static int MAX_LAND_AMOUNT = 12;
	
	private boolean raidable = false;
	
	private ArrayList<String> invitedUsers = new ArrayList<String>();

	private ArrayList<FChunk> land = new ArrayList<FChunk>();

	private String name;
	private String owner, ownerUUID;

	private Location fHome;
	private boolean hasFHome = false;

	private ArrayList<String> mods = new ArrayList<String>();
	private ArrayList<String> members = new ArrayList<String>();

	private HashMap<String, String> players = new HashMap<String, String>();

	private double dtr;
	private double maxDtr;

	private boolean frozen = true;

	private int timeToReset = TIME_TO_RESET_SECONDS;
	
	private boolean warzone = false, spawn = false;

	public Faction(String name, String owner, String ownerUUID, int dtr,
			Location fHome, boolean hasFHome) {
		this.name = name;
		this.owner = owner;
		this.ownerUUID = ownerUUID;
		this.dtr = dtr;
		players.put(ownerUUID, owner);
		fillFromFile();
		this.maxDtr = players.size() * 1;
		if (this.maxDtr > 7)
			this.maxDtr = 7;
		if (hasFHome) {
			this.hasFHome = hasFHome;
			this.fHome = fHome;
		}
	}

	public void setWarzone(){
		this.warzone = true;
	}
	
	public void setSpawn(){
		this.spawn = true;
	}
	
	public ArrayList<FChunk> getLand() {
		return land;
	}

	public void invite(Player player){
		this.invitedUsers.add(player.getName());
	}
	
	public boolean isInvited(Player player){
		if(invitedUsers.contains(player.getName())){
			return true;
		}
		return false;
	}
	
	public void unInvite(String name){
		for(String s : invitedUsers){
			if(s.equalsIgnoreCase(name)){
				invitedUsers.remove(s);
			}
		}
	}
	
	public int getMaxLand() {
		return MAX_LAND_AMOUNT;
	}

	public boolean isWarzone(){
		return this.warzone;
	}
	
	public boolean isSpawn(){
		return this.spawn;
	}
	
	public void setFactionHome(Location location) {
		this.fHome = location;
		this.hasFHome = true;
	}

	public void unclaim(FChunk chunk){
		FChunk fc = null;
		for(FChunk c : land){
			if(c.getX() == chunk.getX() && c.getZ() == chunk.getZ()){
				fc = c;
				break;
			}
		}
		if(fc != null)
			this.land.remove(fc);
	}
	
	public void unclaimAll(){
		land.clear();
	}
	
	public String tryToClaim(FChunk chunk, String name, boolean alertOnSuccess) {
		if (chunk.isClaimed()) {
			return C.SECONDARY + "This land is already claimed.";
		}
		if(!chunk.isNearOtherFaction(this)){
		if (land.size() == 0) {
			land.add(chunk);
			if (alertOnSuccess)
				this.sendFactionMessage(C.PRIMARY + name + C.SECONDARY
						+ " claimed chunk at " + C.PRIMARY + "(" + chunk.getX()
						+ ", " + chunk.getZ() + ")" + C.SECONDARY + "!");
			return C.SECONDARY + "Claimed chunk at " + C.PRIMARY + "("
					+ chunk.getX() + ", " + chunk.getZ() + ")" + C.SECONDARY
					+ "!";
		} else {
			if (land.size() < MAX_LAND_AMOUNT) {
				boolean n = false;
				for (FChunk c : land) {
					if (c.isNextTo(chunk)) {
						n = true;
					}
				}
				if (n) {
					land.add(chunk);
					if (alertOnSuccess)
						this.sendFactionMessage(C.PRIMARY + name + C.SECONDARY
								+ " claimed chunk at " + C.PRIMARY + "("
								+ chunk.getX() + ", " + chunk.getZ() + ")"
								+ C.SECONDARY + "!");
					return C.SECONDARY + "Claimed chunk at " + C.PRIMARY + "("
							+ chunk.getX() + ", " + chunk.getZ() + ")"
							+ C.SECONDARY + "!";
				} else {
					return C.SECONDARY
							+ "This chunk is not next to any of your current land!";
				}
			} else {
				return C.SECONDARY + "Your faction already has the maximum "
						+ C.ERROR_PRIMARY + MAX_LAND_AMOUNT + "/"
						+ MAX_LAND_AMOUNT + C.SECONDARY + " land!";
			}
		}
		}else{
			return C.SECONDARY + "This territory is near another faction's territory. Try claiming in a more open area.";
		}
	}

	public void addMember(Player player) {
		if (!players.containsKey(player.getUniqueId().toString())) {
			players.put(player.getUniqueId().toString(), player.getName());
		}
		if (!members.contains(player.getUniqueId().toString())) {
			members.add(player.getUniqueId().toString());
		}
		this.maxDtr = players.size() * 1;
		this.dtr++;
		if(this.maxDtr > 7)
			this.maxDtr = 7;
		if(this.dtr > this.maxDtr)
			this.dtr = this.maxDtr;
	}

	public void removeMember(Player player) {
		if (Core.getInstance().getUserManager().getUser(player).hasFaction()) {
			if (Core.getInstance().getUserManager().getUser(player)
					.getFaction().equals(this)) {
				Core.getInstance().getUserManager().getUser(player)
						.leaveFaction();
			}
		}
		if (players.containsKey(player.getUniqueId().toString())) {
			players.remove(player.getUniqueId().toString());
		}
		if (members.contains(player.getUniqueId().toString())) {
			members.remove(player.getUniqueId().toString());
		}
		if (mods.contains(player.getUniqueId().toString())) {
			mods.remove(player.getUniqueId().toString());
		}
		this.maxDtr = players.size() * 1;
		if(this.dtr > this.maxDtr)
			dtr = this.maxDtr;
	}
	
	public void removeMember(UUID uuid) {
		if(Bukkit.getServer().getPlayer(uuid).isOnline()){
			removeMember(Bukkit.getServer().getPlayer(uuid));
			return;
		}
		if (players.containsKey(uuid.toString())) {
			players.remove(uuid.toString());
		}
		if (members.contains(uuid.toString())) {
			members.remove(uuid.toString());
		}
		if (mods.contains(uuid.toString())) {
			mods.remove(uuid.toString());
		}
		this.maxDtr = players.size() * 1;
		if(this.dtr > this.maxDtr)
			dtr = this.maxDtr;
	}

	public void secondOffTime() {
		if (!frozen) {
			timeToReset--;
			if (timeToReset == 0) {
				sendFactionMessage(C.SECONDARY + "Faction DTR has frozen.");
				setFrozen(true);
			}
		}
	}

	public boolean hasFactionHome() {
		return hasFHome;
	}

	public Location getFactionHome() {
		return this.fHome;
	}
	


	public String getFactionHomeString() {
		if (hasFHome && fHome != null) {
			int x = (int) fHome.getX();
			int y = (int) fHome.getY();
			int z = (int) fHome.getZ();
			return "(" + x + ", " + y + ", " + z + ")";
		} else {
			return "not set";
		}
	}

	public void factionDeath() {
		if(!raidable){
		timeToReset = TIME_TO_RESET_SECONDS;
		dtr--;
		setFrozen(false);
		if (dtr <= 0) {
			dtr = 0;
			sendFactionMessage(C.ERROR_PRIMARY + "WARNING " + C.SECONDARY
					+ "Your faction is now raidable!");
			raidable = true;
		}
		}
	}

	public int getTimeToReset() {
		return timeToReset;
	}

	public void setFrozen(boolean frozen) {
		this.frozen = frozen;
		if (frozen == true) {
			dtr = maxDtr;
			timeToReset = TIME_TO_RESET_SECONDS;
		}
	}

	public boolean isFrozen() {
		return frozen;
	}

	private void fillFromFile() {

	}

	public double getDtr() {
		return dtr;
	}

	public void setDtr(double d) {
		dtr = d;
		if(dtr > this.maxDtr)
			dtr = this.maxDtr;
		if(dtr < this.maxDtr)
			this.setFrozen(false);
	}

	public boolean isRaidable() {
		return raidable;
	}
	
	public void setRaidable(boolean raidable){
		this.raidable = raidable;
	}

	public String getName() {
		return name;
	}

	public String getOwner() {
		return owner;
	}

	public String getOwnerUUID() {
		return ownerUUID;
	}

	public boolean isMod(Player player) {
		return this.mods.contains(player.getUniqueId().toString()) || this.owner.equalsIgnoreCase(player.getName());
	}
	
	public boolean isMod(UUID uuid) {
		return this.mods.contains(uuid.toString()) || this.ownerUUID.equalsIgnoreCase(uuid.toString());
	}

	@SuppressWarnings("deprecation")
	public ArrayList<Player> getOnlinePlayers() {
		ArrayList<Player> re = new ArrayList<>();
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			if (players.containsKey(player.getUniqueId().toString())) {
				re.add(player);
			}
		}
		return re;
	}

	public void sendFactionMessage(String message) {
		for (Player player : getOnlinePlayers()) {
			player.sendMessage(message);
		}
	}

	public void owner(Player sender, String player) {
		if (!ownerUUID.equals(sender.getUniqueId().toString())) {
			sender.sendMessage(C.SECONDARY + "You must be faction rank "
					+ C.ERROR_PRIMARY + "Leader" + C.SECONDARY
					+ " to promote users.");
			return;
		}
		if (!Core.getInstance().isOnline(player)) {
			sender.sendMessage(C.SECONDARY + "The player " + C.ERROR_PRIMARY
					+ "\"" + player + "\"" + C.SECONDARY + " is not online.");
		}
		Player q = Core.getInstance().getPlayer(player);
		String p = q.getUniqueId().toString();
		if (players.containsKey(p)) {
			this.mods.add(sender.getUniqueId().toString());
			this.owner = q.getName();
			this.ownerUUID = p;
			if (this.mods.contains(p))
				this.mods.remove(p);
			if (this.members.contains(p))
				this.members.remove(p);
			sender.sendMessage(C.SECONDARY + "Set faction rank of " + C.PRIMARY
					+ q.getName() + C.SECONDARY + " to " + C.PRIMARY + "Leader"
					+ C.SECONDARY + ".");
			this.sendFactionMessage(C.PRIMARY + q.getName() + C.SECONDARY
					+ " has been promoted to faction " + C.PRIMARY + "Leader"
					+ C.SECONDARY + ".");
		} else {
			sender.sendMessage(C.SECONDARY + "The player " + C.ERROR_PRIMARY
					+ "\"" + player + "\"" + C.SECONDARY
					+ " is not in your faction.");
		}
	}

	public HashMap<String, String> getPlayers() {
		return players;
	}

	public String getDTRReset() {
		if (!this.frozen) {
			int minutes = (int) Math.floor((timeToReset / 60));
			double total = timeToReset / 60;
			double actualSeconds = total - minutes;
			int roundedSeconds = (int) (actualSeconds * 60);
			return minutes + "m" + roundedSeconds + "s";
		} else {
			return "frozen";
		}
	}

	public String getFormattedMembers() {
		String r = "";
		for (String s : this.getPlayers().keySet()) {
			String name = this.getPlayers().get(s);
			if (this.ownerUUID == s)
				name = FACTION_OWNER_PREFIX + name;
			if (this.mods.contains(s))
				name = FACTION_MODERATOR_PREFIX + name;
			if (Core.getInstance().isOnline(this.getPlayers().get(s)))
				name = C.ONLINE + name;
			else
				name = C.OFFLINE + name;
			r += name + C.SECONDARY + ", ";
		}
		String f = "";
		for (int i = 0; i < r.length(); i++) {
			if (i != r.length() - 1 && i != r.length() - 2) {
				f += r.charAt(i);
			}
		}
		f = f + C.SECONDARY + ".";
		return f;
	}

	public void disband() {
		this.sendFactionMessage(C.SECONDARY + "Your faction has been deleted.");
		for (Player pl : this.getOnlinePlayers()) {
			this.removeMember(pl);
		}
		Core.getInstance().getFactionManager().removeFaction(this);
	}

	public void mod(Player sender, String player) {
		for(String id : players.keySet()){
			if(players.get(id).equalsIgnoreCase(player)){
				this.sendFactionMessage(C.SECONDARY + "Member " + C.PRIMARY + player + C.SECONDARY + " has become a faction moderator.");
				mods.add(id);
				break;
			}
		}
	}

	public boolean isOwner(Player player) {
		if (player.getUniqueId().toString().equals(ownerUUID)) {
			return true;
		}
		return false;
	}

}
