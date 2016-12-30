package classes;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import core.Core;
import user.User;
import utils.ArmorType;
import utils.Chat.C;

public class Rouge extends Kit implements Listener {

	public Rouge() {
		super("Rouge", ArmorType.CHAIN);
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
			PotionEffectType type = PotionEffectType.SPEED;
			int amplifier = 2;
			if (m.equals(Material.QUARTZ)) {
				if(user.hasCooldown("rouge_attack_cooldown")){
					user.getPlayer().sendMessage(C.SECONDARY + "You are still on cooldown for " + C.ERROR_PRIMARY + (int)(user.getCooldown("rouge_attack_cooldown")/20) + "s" + C.SECONDARY + "!");
					return;
				}

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
				event.getPlayer().removePotionEffect(PotionEffectType.SPEED);
				event.getPlayer().removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
				event.getPlayer().addPotionEffect(new PotionEffect(type,
						20 * 5, amplifier));
				event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE,
						20 * 5, 1));
				event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,
						20 * 5, 1));
				user.addCooldown("rouge_attack_cooldown", 20*55);
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Core.getInstance(), new Runnable(){

					@Override
					public void run() {
						onApply(event.getPlayer());
					}
					
				}, 20*5 + 1);
			}
		}
	}

	@Override
	public void onApply(Player player) {
		player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,
				Integer.MAX_VALUE, 1));
		player.addPotionEffect(new PotionEffect(
				PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0));
	}

	@Override
	public void onRemove(Player player) {
		player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
		player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
		player.removePotionEffect(PotionEffectType.SPEED);
	}

}
