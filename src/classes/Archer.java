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
			int amplifier = 3;
			if (m.equals(Material.SUGAR)) {
				if(user.hasCooldown("archer_speed_cooldown")){
					user.getPlayer().sendMessage(C.SECONDARY + "You are still on cooldown for " + C.ERROR_PRIMARY + (int)(user.getCooldown("archer_speed_cooldown")/20) + "s" + C.SECONDARY + "!");
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
				event.getPlayer().addPotionEffect(new PotionEffect(type,
						20 * 8, amplifier));
				user.addCooldown("archer_speed_cooldown", 20*30);
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Core.getInstance(), new Runnable(){

					@Override
					public void run() {
						onApply(event.getPlayer());
					}
					
				}, 20*8 + 1);
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
