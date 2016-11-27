package utils;

import org.bukkit.entity.Player;

import core.Core;

public enum ArmorType {

	NONE("___", ""), CHAIN("CHAINMAIL", "Rouge"), LEATHER("LEATHER", "Archer"), IRON(
			"IRON", "Miner"), DIAMOND("DIAMOND", ""), GOLD("GOLD", "Bard");

	private String kit;
	private String sC;

	ArmorType(String shouldContain, String kit) {
		sC = shouldContain;
		this.kit = kit;
	}

	public boolean isApplied(Player player, boolean tryToApply) {
		if (player.getInventory().getHelmet() != null
				&& player.getInventory().getChestplate() != null
				&& player.getInventory().getLeggings() != null
				&& player.getInventory().getBoots() != null) {
			if (player.getInventory().getHelmet().getType().toString()
					.toUpperCase().contains(sC)
					&& player.getInventory().getChestplate().getType()
							.toString().toUpperCase().contains(sC)
					&& player.getInventory().getLeggings().getType().toString()
							.toUpperCase().contains(sC)
					&& player.getInventory().getBoots().getType().toString()
							.toUpperCase().contains(sC)) {
				if (kit != "" && tryToApply) {
					if (!Core.getInstance().getUserManager().getUser(player)
							.hasKit()) {
						if (!Core.getInstance().getUserManager()
								.getUser(player)
								.hasCooldown("kit_warmup_" + kit)) {
							Core.getInstance().getKitManager().getKit(kit)
									.startApply(player);
						}
					}
				}
				return true;
			}
		}
		if (Core.getInstance().getUserManager().getUser(player).hasKit()
				&& tryToApply) {
			Core.getInstance().getUserManager().getUser(player).getKit()
					.onRemove(player);
			Core.getInstance().getUserManager().getUser(player).removeKit();
		}
		return false;
	}

}
