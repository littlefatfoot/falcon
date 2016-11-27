package user;

import org.bukkit.ChatColor;

public enum Rank {

	OWNER(ChatColor.RED + "[OWNER]", 9), ADMIN(ChatColor.RED + "[ADMIN]",8),SRMOD(
			ChatColor.YELLOW + "[SR.MOD]", 7), MODERATOR(
			ChatColor.YELLOW + "[MOD]", 6), JRMOD(
					ChatColor.YELLOW + "[JR.MOD]", 5), YOUTUBE(ChatColor.LIGHT_PURPLE
			+ "[YT]", 4), TWITCH(ChatColor.LIGHT_PURPLE + "[TW]", 4), PRO(
			ChatColor.GOLD + "[PRO*]", 3), MVP(ChatColor.AQUA + "[MVP*]", 2), VIP(
			ChatColor.GREEN + "[VIP*]", 1), PRO_TEMP(ChatColor.GOLD + "[PRO]",
			3), MVP_TEMP(ChatColor.AQUA + "[MVP]", 2), VIP_TEMP(ChatColor.GREEN
			+ "[VIP]", 1), DEFAULT("", 0);

	private String prefix;
	private int id;

	Rank(String prefix, int id) {
		this.prefix = prefix;
		this.id = id;
	}

	public String getPrefix() {
		return prefix;
	}

	public int getId() {
		return id;
	}

}