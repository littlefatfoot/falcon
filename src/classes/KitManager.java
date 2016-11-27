package classes;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import user.User;
import utils.ArmorType;
import core.Core;

public class KitManager implements Listener {

	private ArrayList<Kit> kits = new ArrayList<Kit>();

	public KitManager() {
		registerKits();
	}
	
	private void startKitCheck(){
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Core.getInstance(), new Runnable(){

			@Override
			public void run() {
				for(User user : Core.getInstance().getUserManager().getUsers()){
					if (!user.hasKit()) {
						ArmorType.GOLD.isApplied(user.getPlayer(), true);
						ArmorType.DIAMOND.isApplied(user.getPlayer(),
								true);
						ArmorType.CHAIN.isApplied(user.getPlayer(), true);
						ArmorType.LEATHER.isApplied(user.getPlayer(),
								true);
						ArmorType.IRON.isApplied(user.getPlayer(), true);
					} else {
						Core.getInstance().getUserManager()
								.getUser(user.getPlayer()).getKit()
								.getArmorType()
								.isApplied(user.getPlayer(), true);
					}
				}
			}
			
		}, 40, 40);
	}

	@EventHandler
	public void onClick(InventoryClickEvent event) {
		if (event.getWhoClicked() instanceof Player) {
			Player player = (Player) event.getWhoClicked();
			Bukkit.getServer()
					.getScheduler()
					.scheduleSyncDelayedTask(Core.getInstance(),
							new Runnable() {

								@Override
								public void run() {
									if (!Core.getInstance().getUserManager()
											.getUser(player).hasKit()) {
										ArmorType.GOLD.isApplied(player, true);
										ArmorType.DIAMOND.isApplied(player,
												true);
										ArmorType.CHAIN.isApplied(player, true);
										ArmorType.LEATHER.isApplied(player,
												true);
										ArmorType.IRON.isApplied(player, true);
									} else {
										Core.getInstance().getUserManager()
												.getUser(player).getKit()
												.getArmorType()
												.isApplied(player, true);
									}
								}

							}, 1);
		}
	}

	private void registerKits() {
		Miner miner = new Miner();
		kits.add(miner);
		Bard bard = new Bard();
		kits.add(bard);
		Archer archer = new Archer();
		kits.add(archer);

		Bukkit.getServer().getPluginManager()
				.registerEvents(miner, Core.getInstance());
		Bukkit.getServer().getPluginManager()
				.registerEvents(bard, Core.getInstance());
		Bukkit.getServer().getPluginManager()
				.registerEvents(archer, Core.getInstance());
	}

	public Kit getKit(String name) {
		for (Kit kit : kits) {
			if (kit.getName().equalsIgnoreCase(name)) {
				return kit;
			}
		}
		return null;
	}

	public boolean isKit(String name) {
		for (Kit kit : kits) {
			if (kit.getName().equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}

}
