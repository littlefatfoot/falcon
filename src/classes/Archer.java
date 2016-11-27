package classes;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import core.Core;
import utils.ArmorType;

public class Archer extends Kit implements Listener {

	public Archer() {
		super("Archer", ArmorType.LEATHER);
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onHit(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Arrow) {
			Arrow arrow = (Arrow) event.getDamager();
			if (arrow.getShooter() instanceof Player) {
				Player player = (Player) arrow.getShooter();
				if (Core.getInstance().getUserManager().getUser(player)
						.hasKit()
						&& Core.getInstance().getUserManager().getUser(player)
								.getKit().equals(this)) {
					event.setDamage(event.getDamage() * 2);
					if (event.getEntity() instanceof Player) {
						Player hit = (Player) event.getEntity();
						Core.getInstance().getUserManager().getUser(hit)
								.addCooldown("archer_tag", 20 * 10);
					}
				}
			}
		}
	}

	@Override
	public void onApply(Player player) {
		player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,
				Integer.MAX_VALUE, 2));
		player.addPotionEffect(new PotionEffect(
				PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1));
	}

	@Override
	public void onRemove(Player player) {
		player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
		player.removePotionEffect(PotionEffectType.SPEED);
	}

}
