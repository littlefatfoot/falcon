package utils;

import org.bukkit.ChatColor;

public class Chat {

	public enum C {

		PRIMARY(ChatColor.GOLD), SECONDARY(ChatColor.GRAY), ERROR_PRIMARY(
				ChatColor.RED), ONLINE(ChatColor.GREEN), OFFLINE(ChatColor.RED);

		private ChatColor color;

		C(ChatColor color) {
			this.color = color;
		}

		public String toString() {
			return color + "";
		}

	}

}
