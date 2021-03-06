package com.samleighton.xquiset.sethomes.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.samleighton.xquiset.sethomes.Home;
import com.samleighton.xquiset.sethomes.SetHomes;
import com.samleighton.xquiset.sethomes.utils.ChatUtils;

import java.util.HashMap;
import java.util.Map;

public class SetHome implements CommandExecutor{
	private final SetHomes pl;
	private int maxHomes;
	
	public SetHome(SetHomes plugin) {
		pl = plugin;
		maxHomes = pl.config.getInt("max-homes");
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		//Make sure the sender of the command is a player
		if (!(sender instanceof Player)) {
			//Sends message to sender of command that they're not a player
			sender.sendMessage(ChatColor.DARK_RED + "This command is for players only!");
			return false;
		}
		//Checks if the command sent is /sethome
		if (cmd.getName().equalsIgnoreCase("sethome")) {
			//Since we know sender is a player we can Cast sender as such
			Player p = (Player) sender;
			String uuid = p.getUniqueId().toString();
			Location home = p.getLocation();
			
			if(pl.getBlacklistedWorlds().contains(home.getWorld().getName()) && !p.hasPermission("homes.config_bypass")) {
				ChatUtils.sendError(p, "This world does not allow the usage of homes!");
				return true;
			}
			
			//Create a home at the players location
			Home playersHome = new Home(home);
			
			//No home name provided
			if(args.length < 1) {
				//Save the home
				pl.saveUnknownHome(uuid, playersHome);

				ChatUtils.sendSuccess(p, "You have set a default home!");
				return true;
			//They have provided a home name and possibly description too
			} else {
				if(p.hasPermission("homes.sethome")) {

					//Check if players amount of homes vs the config max homes allowed
					if(pl.hasNamedHomes(uuid)){
						if((pl.getPlayersNamedHomes(uuid).size() >= maxHomes && maxHomes != 0) && !p.hasPermission("homes.config_bypass")){
							ChatUtils.sendInfo(p, pl.config.getString("max-homes-msg"));
							return true;
						}
						//Check if the player already has a home with the name they gave us
						if(pl.getPlayersNamedHomes(uuid).containsKey(args[0])) {
							ChatUtils.sendError(p, "You already have a home with that name, try a different one!");
							return true;
						}
					}
					
					//Set the home name to the given name
					playersHome.setHomeName(args[0]);
					
					//Build the description as a combination of all other arguments passed
					String desc = "";
					for (int i = 1; i <= args.length - 1; i++) {
						desc += args[i] + " ";
					}
					
					if(!desc.equals("")) {
						playersHome.setDesc(desc.substring(0, desc.length() - 1));
					}

					//Save the new home
					pl.saveNamedHome(uuid, playersHome);

					p.sendMessage(ChatColor.DARK_GREEN + "Your home \'" + playersHome.getHomeName() + "\' has been set!");
					return true;
				}
				//Send player message because they didn't have the proper permissions
				ChatUtils.permissionError(p);
				return true;
			}
		}
		return false;
	}
}
