package faction;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import core.Core;

public class FactionManager {

	private ArrayList<Faction> factions = new ArrayList<Faction>();

	public FactionManager() {
		this.startFactionFreezeTimer();
	}

	public void addFaction(Faction faction) {
		if (!factions.contains(faction))
			factions.add(faction);
	}

	public ArrayList<Faction> getFactions() {
		return factions;
	}

	public Faction getFactionTerritory(Player player) {
		FChunk c = new FChunk(player.getLocation());
		return c.whoOwns();
	}

	public boolean isInFactionTerritory(Player player) {
		FChunk c = new FChunk(player.getLocation());
		return c.isClaimed();
	}

	public void removeFaction(Faction faction) {
		if (factions.contains(faction))
			factions.remove(faction);
	}

	public boolean isFaction(String string) {
		for (Faction faction : factions) {
			if (faction.getName().equalsIgnoreCase(string)) {
				return true;
			}
		}
		return false;
	}

	public Faction getFaction(String string) {
		for (Faction faction : factions) {
			if (faction.getName().equalsIgnoreCase(string)) {
				return faction;
			}
		}
		return null;
	}

	private void startFactionFreezeTimer() {
		Bukkit.getServer().getScheduler()
				.scheduleSyncRepeatingTask(Core.getInstance(), new Runnable() {

					@Override
					public void run() {
						for (Faction faction : factions) {
							if (!faction.isFrozen()) {
								faction.secondOffTime();
							}
						}
					}

				}, 20, 20);
	}

	public boolean canLocateFaction(String string) {
		for (Faction faction : factions) {
			if (faction.getName().equalsIgnoreCase(string)) {
				return true;
			}
		}
		for (Faction faction : factions) {
			HashMap<String, String> players = faction.getPlayers();
			for (String s : players.keySet()) {
				if (players.get(s).equalsIgnoreCase(string)) {
					return true;
				}
			}
		}
		return false;
	}

	public Faction locateFaction(String string) {
		for (Faction faction : factions) {
			if (faction.getName().equalsIgnoreCase(string)) {
				return faction;
			}
		}
		for (Faction faction : factions) {
			HashMap<String, String> players = faction.getPlayers();
			for (String s : players.keySet()) {
				if (players.get(s).equalsIgnoreCase(string)) {
					return faction;
				}
			}
		}
		return null;
	}
}
