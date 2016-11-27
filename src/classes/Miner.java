package classes;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import core.Core;
import user.User;
import utils.ArmorType;

public class Miner extends Kit implements Listener {

	public Miner() {
		super("Miner", ArmorType.IRON);
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		User user = Core.getInstance().getUserManager()
				.getUser(event.getPlayer());
		if (user.hasKit() && user.getKit().equals(this)) {
			if (!event.getPlayer().hasPotionEffect(
					PotionEffectType.INVISIBILITY)) {
				if (event.getPlayer().getLocation().getY() < 25) {
					event.getPlayer().addPotionEffect(
							new PotionEffect(PotionEffectType.INVISIBILITY,
									Integer.MAX_VALUE, 0));
				}
			} else {
				if (event.getPlayer().getLocation().getY() > 27) {
					event.getPlayer().removePotionEffect(
							PotionEffectType.INVISIBILITY);
				}
			}
		}
	}

	@Override
	public void onApply(Player player) {
		player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,
				Integer.MAX_VALUE, 0));
		player.addPotionEffect(new PotionEffect(
				PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0));
		player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING,
				Integer.MAX_VALUE, 1));
	}

	@Override
	public void onRemove(Player player) {
		player.removePotionEffect(PotionEffectType.SPEED);
		player.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
		player.removePotionEffect(PotionEffectType.FAST_DIGGING);
		player.removePotionEffect(PotionEffectType.INVISIBILITY);
	}

}
