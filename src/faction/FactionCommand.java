package faction;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import core.Core;
import user.User;
import utils.Chat.C;

public class FactionCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {

		if (arg0 instanceof Player) {
			Player player = (Player) arg0;
			User user = Core.getInstance().getUserManager().getUser(player);
			if (arg2.equalsIgnoreCase("f")) {
				int l = arg3.length;
				if (l == 0) {
					sendFactionHelp(player);
					return true;
				}
				String a1 = arg3[0];
				if (a1.equalsIgnoreCase("who")) {
					if (l >= 2) {
						if (Core.getInstance().getFactionManager()
								.canLocateFaction(arg3[1])) {
							displayFactionWho(player, Core.getInstance()
									.getFactionManager().locateFaction(arg3[1]));
						} else {
							player.sendMessage(C.SECONDARY
									+ "Faction not found.");
						}
					} else if (l == 1) {
						if (user.hasFaction()) {
							displayFactionWho(player, user.getFaction());
						} else {
							player.sendMessage(C.SECONDARY
									+ "You are not in a faction.");
						}
					}
					return true;
				}
				if (a1.equalsIgnoreCase("create")) {
					if (!user.hasFaction()) {
						if (l >= 2) {
							if (!Core.getInstance().getFactionManager()
									.isFaction(arg3[1])) {
								if (!arg3[1].equalsIgnoreCase("wilderness")
										&& !arg3[1]
												.equalsIgnoreCase("safezone")
										&& !arg3[1].equalsIgnoreCase("warzone")) {
									Location d = new Location(Bukkit
											.getWorlds().get(0), 0, 0, 0);
									Faction f = new Faction(arg3[1],
											player.getName(), player
													.getUniqueId().toString(),
											1, d, false);
									Core.getInstance().getFactionManager()
											.addFaction(f);
									user.setFaction(f);
									player.sendMessage(C.SECONDARY
											+ "Created faction " + C.PRIMARY
											+ arg3[1] + C.SECONDARY + "!");
								} else {
									player.sendMessage(C.SECONDARY
											+ "That name is not allowed.");
								}
							} else {
								player.sendMessage(C.SECONDARY
										+ "There is already a faction called "
										+ C.ERROR_PRIMARY
										+ "\""
										+ Core.getInstance()
												.getFactionManager()
												.getFaction(arg3[1]).getName()
										+ "\"" + C.SECONDARY + "!");
							}
						} else {
							player.sendMessage(C.SECONDARY
									+ "Invalid syntax, try: " + C.ERROR_PRIMARY
									+ "/f create (name)" + C.SECONDARY + "!");
						}
					} else {
						player.sendMessage(C.SECONDARY
								+ "You are already in a faction. Type "
								+ C.PRIMARY + "/f leave" + C.SECONDARY
								+ " if you wish to create your own.");
					}
					return true;
				}
				if (a1.equalsIgnoreCase("map")) {
					HashMap<Faction, Character> factions = new HashMap<Faction, Character>();
					char c = 'a';
					player.sendMessage(C.PRIMARY + " " + ChatColor.UNDERLINE
							+ "Faction map:");
					player.sendMessage("");
					for (int i = -5; i <= 5; i++) {
						String s = "";
						for (int y = -10; y <= 10; y++) {
							Location location = player.getLocation();
							location.setX(location.getX() + (i * 16));
							location.setZ(location.getZ() + (y * 16));
							FChunk chunk = new FChunk(location);
							FChunk current = new FChunk(player.getLocation());
							if(y <= -8 && i <= -3){
								s+= " ";
							}else{
							if (!(chunk.getX() == current.getX() && chunk
									.getZ() == current.getZ())) {

								if (!chunk.isClaimed()) {
									s += ChatColor.GRAY + "-";
								} else {
									if (user.hasFaction()) {
										if (chunk.whoOwns().equals(
												user.getFaction())) {
											if (factions.containsKey(user
													.getFaction())) {
												s += ChatColor.GREEN
														+ ""
														+ factions.get(user
																.getFaction());
											} else {
												factions.put(user.getFaction(),
														c);
												s += ChatColor.GREEN
														+ ""
														+ factions.get(user
																.getFaction());
												c++;
											}
										} else {
											Faction cF = chunk.whoOwns();
											if (factions.containsKey(cF)) {
												s += ChatColor.RED + ""
														+ factions.get(cF);
											} else {
												factions.put(cF, c);
												s += ChatColor.RED + ""
														+ factions.get(cF);
												c++;
											}
										}
									} else {
										Faction cF = chunk.whoOwns();
										if (factions.containsKey(cF)) {
											s += ChatColor.RED + ""
													+ factions.get(cF);
										} else {
											factions.put(cF, c);
											s += ChatColor.RED + ""
													+ factions.get(cF);
											c++;
										}
									}
								}
							} else {
								s += ChatColor.YELLOW + "+";
							}
							}
						}
						player.sendMessage(s);
					}
					String m = "";
					for (Faction faction : factions.keySet()) {
						m += C.SECONDARY + faction.getName() + " = "
								+ factions.get(faction) + ", ";
					}
					String f = "";
					for (int i = 0; i < m.length() - 2; i++) {
						f += m.charAt(i);
					}
					if(!"".equals(f))
						f = f + ".";
					player.sendMessage(f);
				}
				if (a1.equalsIgnoreCase("join")) {
					if (!user.hasFaction()) {
						if (l >= 2) {
							if (Core.getInstance().getFactionManager()
									.canLocateFaction(arg3[1])) {
								Faction faction = Core.getInstance()
										.getFactionManager()
										.locateFaction(arg3[1]);
								if (faction.isInvited(player)) {
									faction.addMember(player);
									user.setFaction(faction);
									faction.sendFactionMessage(C.PRIMARY + ""
											+ player.getName() + C.SECONDARY
											+ " has joined the faction.");
								} else {
									player.sendMessage(C.SECONDARY
											+ "The faction " + C.ERROR_PRIMARY
											+ faction.getName() + C.SECONDARY
											+ " has not invited you.");
								}
							} else {
								player.sendMessage(C.SECONDARY
										+ "Faction not found.");
							}
						} else {
							player.sendMessage(C.SECONDARY
									+ "Invalid syntax, try: " + C.ERROR_PRIMARY
									+ "/f invite (name)" + C.SECONDARY + "!");
						}
					} else {
						player.sendMessage(C.SECONDARY
								+ "You are already in a faction.");
					}
				}
				if (a1.equalsIgnoreCase("kick")) {
					if (user.hasFaction()) {
						if (user.getFaction().isMod(player)) {
							if (l >= 2) {
								String name = arg3[1];
								HashMap<String, String> players = user
										.getFaction().getPlayers();
								for (String st : players.keySet()) {
									String n = user.getFaction().getPlayers()
											.get(st);
									if (n.equalsIgnoreCase(name)) {
										if (user.getFaction().isMod(
												UUID.fromString(st))) {
											if (user.getFaction().isOwner(
													player)) {
												user.getFaction()
														.sendFactionMessage(
																C.PRIMARY
																		+ player.getName()
																		+ C.SECONDARY
																		+ " has kicked "
																		+ C.PRIMARY
																		+ n
																		+ C.SECONDARY
																		+ " from the faction.");
												user.getFaction().removeMember(
														UUID.fromString(st));
											}
										} else {
											user.getFaction()
													.sendFactionMessage(
															C.PRIMARY
																	+ player.getName()
																	+ C.SECONDARY
																	+ " has kicked "
																	+ C.PRIMARY
																	+ n
																	+ C.SECONDARY
																	+ " from the faction.");
											user.getFaction().removeMember(
													UUID.fromString(st));
										}
									}
								}
							} else {
								player.sendMessage(C.SECONDARY
										+ "Invalid syntax, try: "
										+ C.ERROR_PRIMARY + "/f kick (name)"
										+ C.SECONDARY + "!");
							}
						} else {
							player.sendMessage(C.SECONDARY
									+ "Only the faction " + C.ERROR_PRIMARY
									+ "Moderators" + C.SECONDARY
									+ " can kick users.");
						}
					} else {
						player.sendMessage(C.SECONDARY
								+ "You are not in a faction.");
					}
				}
				if (a1.equalsIgnoreCase("mod")) {
					if (user.hasFaction()) {
						if (user.getFaction().getOwnerUUID()
								.equals(player.getUniqueId().toString())) {
							if (l >= 2) {
								String name = arg3[1];
								HashMap<String, String> players = user
										.getFaction().getPlayers();
								for (String st : players.keySet()) {
									String n = user.getFaction().getPlayers()
											.get(st);
									if (n.equalsIgnoreCase(name)) {
										if (!user.getFaction().isMod(
												UUID.fromString(st))) {
											user.getFaction().mod(player, name);
										} else {
											player.sendMessage(C.SECONDARY
													+ "This user is already faction moderator.");
										}
									}
								}
							} else {
								player.sendMessage(C.SECONDARY
										+ "Invalid syntax, try: "
										+ C.ERROR_PRIMARY + "/f mod (name)"
										+ C.SECONDARY + "!");
							}
						} else {
							player.sendMessage(C.SECONDARY
									+ "Only the faction " + C.ERROR_PRIMARY
									+ "Leader" + C.SECONDARY
									+ " can promote users.");
						}
					} else {
						player.sendMessage(C.SECONDARY
								+ "You are not in a faction.");
					}
				}
				if (a1.equalsIgnoreCase("inv") || a1.equalsIgnoreCase("invite")) {
					if (user.hasFaction()) {
						if (user.getFaction().isMod(player)) {
							if (l >= 2) {
								if (Core.getInstance().isOnline(arg3[1])) {
									Player invited = Core.getInstance()
											.getPlayer(arg3[1]);
									if (!Core.getInstance().getUserManager()
											.getUser(invited).hasFaction()) {
										user.getFaction().invite(invited);
										user.getFaction().sendFactionMessage(
												C.PRIMARY + player.getName()
														+ C.SECONDARY
														+ " has invited "
														+ C.PRIMARY
														+ invited.getName()
														+ C.SECONDARY
														+ " to your faction.");
										invited.sendMessage("");
										invited.sendMessage(C.SECONDARY
												+ "You have been invited to "
												+ C.PRIMARY
												+ user.getFaction().getName()
												+ C.SECONDARY + "!");
										invited.sendMessage(C.SECONDARY
												+ "Type " + C.PRIMARY
												+ "/f join "
												+ user.getFaction().getName()
												+ C.SECONDARY + " to accept.");
										invited.sendMessage("");
									} else {
										player.sendMessage(C.ERROR_PRIMARY
												+ invited.getName()
												+ C.SECONDARY
												+ " already is in a faction.");
									}
								} else {
									player.sendMessage(C.SECONDARY
											+ "The player " + C.ERROR_PRIMARY
											+ "\"" + arg3[1] + "\""
											+ C.SECONDARY + " is not online.");
								}
							} else {
								player.sendMessage(C.SECONDARY
										+ "Invalid syntax, try: "
										+ C.ERROR_PRIMARY + "/f invite (name)"
										+ C.SECONDARY + "!");
							}
						} else {
							player.sendMessage(C.SECONDARY
									+ "Only the faction " + C.ERROR_PRIMARY
									+ "Moderators" + C.SECONDARY
									+ " can invite users.");
						}
					} else {
						player.sendMessage(C.SECONDARY
								+ "You are not in a faction.");
					}
				}
				if (a1.equalsIgnoreCase("claim")) {
					if (user.hasFaction()) {
						if (user.getFaction().isMod(player)) {
							player.sendMessage(user.getFaction().tryToClaim(
									new FChunk(player.getLocation()),
									player.getName(), true));
						} else {
							player.sendMessage(C.SECONDARY
									+ "Only the faction " + C.ERROR_PRIMARY
									+ "Moderators" + C.SECONDARY
									+ " can claim land.");
						}
					} else {
						player.sendMessage(C.SECONDARY
								+ "You are not in a faction.");
					}
					return true;
				}
				if (a1.equalsIgnoreCase("sethome")) {
					if (user.hasFaction()) {
						if (user.getFaction().getOwnerUUID()
								.equals(player.getUniqueId().toString())) {
							if (Core.getInstance().getFactionManager()
									.isInFactionTerritory(player)) {
								if (Core.getInstance().getFactionManager()
										.getFactionTerritory(player)
										.equals(user.getFaction())) {
									user.getFaction().setFactionHome(
											player.getLocation());
									user.getFaction()
											.sendFactionMessage(
													C.PRIMARY
															+ player.getName()
															+ C.SECONDARY
															+ " has set the faction home to "
															+ C.PRIMARY
															+ "("
															+ player.getLocation()
																	.getBlockX()
															+ ", "
															+ player.getLocation()
																	.getBlockY()
															+ ", "
															+ player.getLocation()
																	.getBlockZ()
															+ ")" + C.SECONDARY
															+ "!");
								} else {
									player.sendMessage(C.SECONDARY
											+ "You are not in your faction's territory.");
								}
							} else {
								player.sendMessage(C.SECONDARY
										+ "You are not in your faction's territory.");
							}
						} else {
							player.sendMessage(C.SECONDARY
									+ "Only the faction " + C.ERROR_PRIMARY
									+ "Moderators" + C.SECONDARY
									+ " can claim land.");
						}
					} else {
						player.sendMessage(C.SECONDARY
								+ "You are not in a faction.");
					}
					return true;
				}
				if (a1.equalsIgnoreCase("home")) {
					if (user.hasFaction()) {
						if (user.getFaction().hasFactionHome()) {
							if (!user.hasCooldown("pvptimer")) {
								if (!Core.getInstance().getFactionManager()
										.isInFactionTerritory(player)
										|| Core.getInstance()
												.getFactionManager()
												.getFactionTerritory(player)
												.equals(user.getFaction())) {
									player.sendMessage(C.SECONDARY
											+ "Teleporting to " + C.PRIMARY
											+ "faction home " + C.SECONDARY
											+ "in " + C.PRIMARY + "10s"
											+ C.SECONDARY + "!");
									user.teleport(user.getFaction()
											.getFactionHome(), "f_home",
											20 * 10);
								} else {
									player.sendMessage(C.SECONDARY
											+ "You cannot use this command in other faction territory. If you are stuck, use "
											+ C.ERROR_PRIMARY + "/f stuck"
											+ C.SECONDARY + "!");
								}
							} else {
								player.sendMessage(C.SECONDARY
										+ "You cannot teleport in combat.");
							}
						} else {
							player.sendMessage(C.SECONDARY
									+ "Your faction does not have a home set.");
						}
					} else {
						player.sendMessage(C.SECONDARY
								+ "You are not in a faction.");
					}
					return true;
				}
				if(a1.equalsIgnoreCase("leader")){
					if(arg3.length >= 2){
						user.getFaction().mod(player, arg3[1]);
					}else{
						player.sendMessage(C.SECONDARY + "Invalid syntax, try " + C.ERROR_PRIMARY + "/f leader (name)" + C.SECONDARY + "!");
					}
				}
				if (a1.equalsIgnoreCase("stuck")) {
					if (user.hasFaction()) {
						if (user.getFaction().hasFactionHome()) {
							if (!user.hasCooldown("pvptimer")) {
								player.sendMessage(C.SECONDARY
										+ "Teleporting to " + C.PRIMARY
										+ "faction home " + C.SECONDARY + "in "
										+ C.PRIMARY + "60s" + C.SECONDARY + "!");
								user.teleport(user.getFaction()
										.getFactionHome(), "f_home", 20 * 60);
							} else {
								player.sendMessage(C.SECONDARY
										+ "You cannot teleport in combat.");
							}
						} else {
							player.sendMessage(C.SECONDARY
									+ "Your faction does not have a home set.");
						}
					} else {
						player.sendMessage(C.SECONDARY
								+ "You are not in a faction.");
					}
					return true;
				}
				if (a1.equalsIgnoreCase("leave")) {
					if (user.hasFaction()) {
						if (!user.getFaction().getOwnerUUID()
								.equals(player.getUniqueId().toString())) {
							user.getFaction().sendFactionMessage(
									C.PRIMARY + player.getName() + " "
											+ C.SECONDARY
											+ "has left the faction!");
							user.getFaction().removeMember(player);
						} else {
							player.sendMessage(C.SECONDARY
									+ "You cannot leave a faction that you own. Do "
									+ C.ERROR_PRIMARY
									+ "/f leader (name)"
									+ C.SECONDARY
									+ " to promote another member to leader or "
									+ C.ERROR_PRIMARY + "/f disband"
									+ C.SECONDARY + " to disband your faction.");
						}
					} else {
						player.sendMessage(C.SECONDARY
								+ "You are not in a faction.");
					}
					return true;
				}
				if (a1.equalsIgnoreCase("unclaim")) {
					if (user.hasFaction()) {
						if (user.getFaction().isMod(player)) {
							FChunk chunk = new FChunk(player.getLocation());
							if(chunk.isClaimed() && chunk.whoOwns().equals(user.getFaction())){
								user.getFaction().sendFactionMessage(C.PRIMARY + player.getName() + C.SECONDARY + " has unclaimed territory at " + C.PRIMARY + "(" + chunk.getX() + ", " + chunk.getZ() + ")" + C.SECONDARY + "!");
								user.getFaction().unclaim(chunk);
							}
						} else {
							player.sendMessage(C.SECONDARY
									+ "Only the faction " + C.ERROR_PRIMARY
									+ "Moderators" + C.SECONDARY
									+ " can promote users.");
						}
					} else {
						player.sendMessage(C.SECONDARY
								+ "You are not in a faction.");
					}
					return true;
				}
				
				if (a1.equalsIgnoreCase("unclaimall")) {
					if (user.hasFaction()) {
						if (user.getFaction().isMod(player)) {
							FChunk chunk = new FChunk(player.getLocation());
							if(chunk.isClaimed() && chunk.whoOwns().equals(user.getFaction())){
								user.getFaction().sendFactionMessage(C.PRIMARY + player.getName() + C.SECONDARY + " has unclaimed all faction territory!");
								user.getFaction().unclaimAll();
							}
						} else {
							player.sendMessage(C.SECONDARY
									+ "Only the faction " + C.ERROR_PRIMARY
									+ "Moderators" + C.SECONDARY
									+ " can promote users.");
						}
					} else {
						player.sendMessage(C.SECONDARY
								+ "You are not in a faction.");
					}
					return true;
				}
				if (a1.equalsIgnoreCase("disband")) {
					if (user.hasFaction()) {
						if (user.getFaction().getOwnerUUID()
								.equals(player.getUniqueId().toString())) {
							user.getFaction().disband();
						} else {
							player.sendMessage(C.SECONDARY
									+ "Only the faction " + C.ERROR_PRIMARY
									+ "Leader" + C.SECONDARY
									+ " can disband the faction.");
						}
					} else {
						player.sendMessage(C.SECONDARY
								+ "You are not in a faction.");
					}
					return true;
				}
				String s = "";
				for(int i = 0; i < arg3.length; i++)
					s+=arg3[i] + " ";
				player.sendMessage(C.SECONDARY + "The command " + C.ERROR_PRIMARY  + "/f " + s + C.SECONDARY + "not found.");
			}
		} else {
			arg0.sendMessage("This command is for players.");
		}

		return false;
	}

	private void displayFactionWho(Player player, Faction faction) {
		player.sendMessage(C.PRIMARY + "" + ChatColor.UNDERLINE
				+ faction.getName() + ":");
		player.sendMessage("");
		player.sendMessage(C.SECONDARY + "Players: "
				+ faction.getFormattedMembers());
		player.sendMessage(C.SECONDARY + "DTR: " + C.PRIMARY + faction.getDtr());
		player.sendMessage(C.SECONDARY + "DTR reset: " + C.PRIMARY
				+ faction.getDTRReset());
		player.sendMessage(C.SECONDARY + "Faction home: " + C.PRIMARY
				+ faction.getFactionHomeString());
		player.sendMessage(C.SECONDARY + "Land: " + C.PRIMARY
				+ faction.getLand().size() + "/" + faction.getMaxLand());
	}

	private void sendFactionHelp(Player player) {
		User user = Core.getInstance().getUserManager().getUser(player);
		C needFaction = C.ERROR_PRIMARY;
		C needMod = C.ERROR_PRIMARY;
		C needOwner = C.ERROR_PRIMARY;
		if (user.hasFaction()) {
			needFaction = C.PRIMARY;
			Faction faction = user.getFaction();
			if (faction.isOwner(player)) {
				needMod = C.PRIMARY;
				needOwner = C.PRIMARY;
			}
			if (faction.isMod(player)) {
				needMod = C.PRIMARY;
			}
		}
		player.sendMessage(C.PRIMARY + "" + ChatColor.UNDERLINE
				+ "Faction help:");
		player.sendMessage("");
		player.sendMessage(C.PRIMARY + "/f who [faction] " + C.SECONDARY
				+ "- Shows faction information and members.");
		player.sendMessage(C.PRIMARY + "/f create (name) " + C.SECONDARY
				+ "- Create a faction.");
		player.sendMessage(C.PRIMARY + "/f join (name) " + C.SECONDARY
				+ "- Join a faction.");
		player.sendMessage(needFaction + "/f home " + C.SECONDARY
				+ "- Teleport to faction home.");
		player.sendMessage(needFaction + "/f leave " + C.SECONDARY
				+ "- Leave your faction.");
		player.sendMessage(needMod + "/f invite (player) " + C.SECONDARY
				+ "- Invite a member to your faction.");
		player.sendMessage(needMod + "/f claim " + C.SECONDARY
				+ "- Claim a chunk of land for your faction.");
		player.sendMessage(needMod + "/f sethome " + C.SECONDARY
				+ "- Set the home of your faction.");
		player.sendMessage(needMod + "/f unclaim " + C.SECONDARY
				+ "- Unclaim some faction land.");
		player.sendMessage(needMod + "/f unclaimall " + C.SECONDARY
				+ "- Unclaim all faction land.");
		player.sendMessage(needOwner + "/f mod (player) " + C.SECONDARY
				+ "- Promote a faction member to moderator.");
		player.sendMessage(needOwner + "/f leader (player) " + C.SECONDARY
				+ "- Promote a faction member to owner.");
		player.sendMessage(needOwner + "/f disband " + C.SECONDARY
				+ "- Disband your faction.");
	}

}
