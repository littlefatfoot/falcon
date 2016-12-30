package classes;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import core.Core;
import user.User;
import utils.ArmorType;

public class Bard extends Kit implements Listener {

	public Bard() {
		super("Bard", ArmorType.GOLD);
		startBardHoldCheck();
	}

	private void startBardHoldCheck() {
		Bukkit.getServer().getScheduler()
				.scheduleSyncRepeatingTask(Core.getInstance(), new Runnable() {

					@Override
					public void run() {
						bardHoldCheck();
					}

				}, 20 * 4, 20 * 4);
	}

	private void bardHoldCheck() {
		for (User user : Core.getInstance().getUserManager().getUsers()) {
			if (user.hasKit() && user.getKit().equals(this)) {
				Material m = user.getPlayer().getItemInHand().getType();
				PotionEffectType type = PotionEffectType.HARM;
				int amplifier = 0;
				if (m.equals(Material.IRON_INGOT)) {
					type = PotionEffectType.DAMAGE_RESISTANCE;
					amplifier = 0;
				}
				if (m.equals(Material.BLAZE_POWDER)) {
					type = PotionEffectType.INCREASE_DAMAGE;
					amplifier = 0;
				}
				if (m.equals(Material.SUGAR)) {
					type = PotionEffectType.SPEED;
					amplifier = 1;
				}
				if (m.equals(Material.GHAST_TEAR)) {
					type = PotionEffectType.REGENERATION;
					amplifier = 0;
				}
				if (m.equals(Material.FEATHER)) {
					type = PotionEffectType.JUMP;
					amplifier = 1;
				}
				if (user.hasFaction()) {
					for (Player player : user.getFaction().getOnlinePlayers()) {
						if (!player.equals(user.getPlayer())) {
							if (player.getLocation().distance(
									user.getPlayer().getLocation()) < 8) {
								if (!type.equals(PotionEffectType.HARM)) {
									player.addPotionEffect(new PotionEffect(
											type, 20 * 5, amplifier));
								}
							}
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if (!event.getAction().equals(Action.RIGHT_CLICK_AIR)
				&& !event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
			return;
		User user = Core.getInstance().getUserManager()
				.getUser(event.getPlayer());
		if (user.hasKit() && user.getKit().equals(this)) {
			Material m = user.getPlayer().getItemInHand().getType();
			PotionEffectType type = PotionEffectType.HARM;
			int amplifier = 0;
			if (m.equals(Material.IRON_INGOT)) {
				type = PotionEffectType.DAMAGE_RESISTANCE;
				amplifier = 1;
			}
			if (m.equals(Material.BLAZE_POWDER)) {
				type = PotionEffectType.INCREASE_DAMAGE;
				amplifier = 1;
			}
			if (m.equals(Material.SUGAR)) {
				type = PotionEffectType.SPEED;
				amplifier = 2;
			}
			if (m.equals(Material.FEATHER)) {
				type = PotionEffectType.JUMP;
				amplifier = 2;
			}
			if (!type.equals(PotionEffectType.HARM)) {

				if (event.getPlayer().getItemInHand().getAmount() == 1) {
					event.getPlayer()
							.setItemInHand(new ItemStack(Material.AIR));
					event.getPlayer().updateInventory();
				} else {
					event.getPlayer()
							.getItemInHand()
							.setAmount(
									event.getPlayer().getItemInHand()
											.getAmount() - 1);
					event.getPlayer().updateInventory();
				}

				if (user.hasFaction()) {
					for (Player player : user.getFaction().getOnlinePlayers()) {
						if (!player.equals(user.getPlayer())) {
							if (player.getLocation().distance(
									user.getPlayer().getLocation()) < 8) {
								player.removePotionEffect(type);
								player.addPotionEffect(new PotionEffect(type,
										20 * 5, amplifier));
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void onApply(Player player) {
		player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,
				Integer.MAX_VALUE, 1));
		player.addPotionEffect(new PotionEffect(
				PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0));
		player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION,
				Integer.MAX_VALUE, 0));
	}

	@Override
	public void onRemove(Player player) {
		player.removePotionEffect(PotionEffectType.SPEED);
		player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
		player.removePotionEffect(PotionEffectType.REGENERATION);
	}

}
