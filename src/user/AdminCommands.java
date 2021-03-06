package user;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import utils.Chat.C;
import core.Core;
import faction.Faction;

public class AdminCommands implements CommandExecutor {

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		
		if(arg2.equalsIgnoreCase("a")){
			if(arg0 instanceof Player){
				Player player = (Player) arg0;
				User user = Core.getInstance().getUserManager().getUser(player);
				if(user.getRank().getId() >= Rank.ADMIN.getId()){
					if(arg3.length == 0){
						player.sendMessage(C.PRIMARY +""+ChatColor.UNDERLINE+ "Admin commands:");
						player.sendMessage("");
						player.sendMessage(C.PRIMARY + "/a setrank (name) (rank)" + C.SECONDARY + " - Set the rank of a player.");
						player.sendMessage(C.PRIMARY + "/a freeze (name)" + C.SECONDARY + " - Freeze a player.");
						player.sendMessage(C.PRIMARY + "/a vanish" + C.SECONDARY + " - Freeze a player.");
						player.sendMessage(C.PRIMARY + "/a rtp" + C.SECONDARY + " - Teleport to a random player.");
						player.sendMessage(C.PRIMARY + "/a dtrfreeze (faction)" + C.SECONDARY + " - Freeze faction DTR.");
						player.sendMessage(C.PRIMARY + "/a setdtr (faction) (amount)" + C.SECONDARY + " - Set faction DTR.");
						player.sendMessage(C.PRIMARY + "/a mutechat" + C.SECONDARY + " - Mute server chat.");
					}else{
						if(arg3[0].equalsIgnoreCase("setrank")){
							if(arg3.length >= 3){
								if(Core.getInstance().isOnline(arg3[1])){
									for(Rank rank : Rank.values()){
										if(ChatColor.stripColor(rank.getPrefix()).equalsIgnoreCase(arg3[2])){
											player.sendMessage(C.SECONDARY + "Set the rank of " + C.PRIMARY + Core.getInstance().getPlayer(arg3[1]).getName() + C.SECONDARY + " to " + C.PRIMARY + rank.getPrefix() + C.SECONDARY + "!");
											Core.getInstance().getUserManager().getUser(Core.getInstance().getPlayer(arg3[1])).setRank(rank, true);
										}
									}
								}else{
									player.sendMessage(C.SECONDARY + "Player not online.");
								}
							}else{
								player.sendMessage(C.SECONDARY + "Invalid syntax, try " + C.ERROR_PRIMARY + "/a setrank (name) (rank)" + C.SECONDARY + "!");
							}
							return true;
						}
						if(arg3[0].equalsIgnoreCase("mutechat")){
							Core.getInstance().toggleChatMute();
							return true;
						}
						
						if(arg3[0].equalsIgnoreCase("vanish")){
							user.toggleVanish();
							if(user.isVanished()){
								player.sendMessage(C.SECONDARY + "You have been vanished.");
								for(Player o : Bukkit.getServer().getOnlinePlayers()){
									if(Core.getInstance().getUserManager().getUser(o).getRank().getId() < Rank.JRMOD.getId()){
										o.hidePlayer(player);
									}
								}
							}else{
								player.sendMessage(C.SECONDARY + "You are no longer vanished.");
								for(Player o : Bukkit.getServer().getOnlinePlayers()){
									o.showPlayer(player);
								}
							}
							return true;
						}
						
						if(arg3[0].equalsIgnoreCase("rtp")){
							Random random = new Random();
							int r = random.nextInt(Bukkit.getServer().getOnlinePlayers().length);
							player.teleport(Bukkit.getServer().getOnlinePlayers()[r].getLocation());
							player.sendMessage(C.SECONDARY + "You have teleported to " + C.PRIMARY + Bukkit.getServer().getOnlinePlayers()[r].getName());
							return true;
						}
						
						if(arg3[0].equalsIgnoreCase("setdtr")){
							if(arg3.length >= 3){
								if(Core.getInstance().getFactionManager().isFaction(arg3[1])){
									Faction faction = Core.getInstance().getFactionManager().getFaction(arg3[1]);
									try{
										double d = Double.parseDouble(arg3[2]);
										faction.setDtr(d);
										faction.sendFactionMessage(C.SECONDARY + "Your faction DTR was set to " + C.PRIMARY + faction.getDtr() + C.SECONDARY + " by " + C.PRIMARY + player.getName() + C.SECONDARY + "!");
									}catch(Exception ex){
										player.sendMessage(C.SECONDARY + "Invalid number given.");
									}
								}else{
									player.sendMessage(C.SECONDARY + "No faction found called \"" + C.ERROR_PRIMARY + arg3[1] + "\"" + C.SECONDARY + "!");
								}
							}else{
								player.sendMessage(C.SECONDARY + "Invalid syntax, try " + C.ERROR_PRIMARY + "/a setdtr (faction) (amount)" + C.SECONDARY + "!");
							}
							return true;
						}
						
						if(arg3[0].equalsIgnoreCase("dtrfreeze")){
							if(arg3.length >= 2){
								if(Core.getInstance().getFactionManager().isFaction(arg3[1])){
									Faction faction = Core.getInstance().getFactionManager().getFaction(arg3[1]);
									faction.setFrozen(!faction.isFrozen());
									faction.sendFactionMessage(C.SECONDARY + "Your faction DTR was " + (faction.isFrozen() ? "":"un") + "frozen by " + C.PRIMARY + player.getName() + C.SECONDARY + "!");
								}else{
									player.sendMessage(C.SECONDARY + "No faction found called \"" + C.ERROR_PRIMARY + arg3[1] + "\"" + C.SECONDARY + "!");
								}
							}else{
								player.sendMessage(C.SECONDARY + "Invalid syntax, try " + C.ERROR_PRIMARY + "/a dtrfreeze (faction)" + C.SECONDARY + "!");
							}
							return true;
						}
						
						if(arg3[0].equalsIgnoreCase("freeze")){
							if(arg3.length >= 2){
								String name = arg3[1];
								if(Core.getInstance().isOnline(name)){
									if(!Core.getInstance().getUserManager().getUser(Core.getInstance().getPlayer(name)).isFrozen()){
										player.sendMessage(C.SECONDARY + "You have frozen " + C.PRIMARY + Core.getInstance().getPlayer(name).getName() + C.SECONDARY + "!");
										Core.getInstance().getUserManager().getUser(Core.getInstance().getPlayer(name)).setFrozen(true);
									}else{
										player.sendMessage(C.SECONDARY + "You have unfrozen " + C.PRIMARY + Core.getInstance().getPlayer(name).getName() + C.SECONDARY + "!");
										Core.getInstance().getUserManager().getUser(Core.getInstance().getPlayer(name)).setFrozen(false);
									}
								}
							}else{
								player.sendMessage(C.SECONDARY + "Invalid syntax, try " + C.ERROR_PRIMARY + "/a togglefreeze (name)" + C.SECONDARY + "!");
							}
						}
					}
				}else{
					player.sendMessage(C.SECONDARY + "This command is restricted to " + Rank.ADMIN.getPrefix() + C.SECONDARY + " and up.");
				}
			}else{
				arg0.sendMessage("Only usable by players.");
			}
		}
		
		return false;
	}

}
