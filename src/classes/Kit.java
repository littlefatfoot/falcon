package classes;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

import core.Core;
import utils.ArmorType;
import utils.Chat.C;

public class Kit {

	private String name;
	private ArmorType armorType;

	public Kit(String name, ArmorType armorType) {
		this.name = name;
		this.armorType = armorType;
	}

	public String getName() {
		return name;
	}

	public ArmorType getArmorType() {
		return this.armorType;
	}

	public void startApply(Player player) {
		if (Core.getInstance().getKitWarmupTicks() > 19) {
			player.sendMessage(C.SECONDARY + "Warmup of " + C.PRIMARY
					+ (int) (Core.getInstance().getKitWarmupTicks() / 20) + "s"
					+ C.SECONDARY + " for class " + C.PRIMARY + name
					+ C.SECONDARY + "!");
			Core.getInstance()
					.getUserManager()
					.getUser(player)
					.addCooldown("kit_warmup_" + name,
							Core.getInstance().getKitWarmupTicks());
		} else {
			onApply(player);
		}
	}

	public void onApply(Player player) {
		Core.getInstance().getUserManager().getUser(player).setKit(this);
		player.playSound(player.getLocation(), Sound.HORSE_SADDLE, 1, 1);
		player.sendMessage(C.SECONDARY + "Applied class " + C.PRIMARY + name
				+ C.SECONDARY + "!");
	}

	public void onRemove(Player player) {

	}

}
