package com.samleighton.xquiset.sethomes.commands;

import com.samleighton.xquiset.sethomes.SetHomes;
import com.samleighton.xquiset.sethomes.utils.ChatUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ListHomes implements CommandExecutor{

	private final SetHomes pl;
	
	public ListHomes(SetHomes plugin) {
		this.pl = plugin;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		//Make sure the sender of the command is a player
		if (!(sender instanceof Player)) {
			//Sends message to sender of command that they're not a player
			ChatUtils.notPlayerError(sender);
			return false;
		}
		
		if (cmd.getName().equalsIgnoreCase("homes")) {
			Player p = (Player) sender;
			
			if(args.length == 1) {
				if(p.hasPermission("homes.gethomes")) {
					//Create a offline player for the name they passed
					@SuppressWarnings("deprecated")
					OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
					//Check to make sure the player has actually joined the server
					if (offlinePlayer.hasPlayedBefore()) {
						UUID uuid = offlinePlayer.getUniqueId();
						listHomes(uuid, p);
					} else {
						ChatUtils.sendError(p, "That user has never player here before!");
						return true;
					}
					return true;
				} else {
					//Send player message because they didn't have the proper permissions
					ChatUtils.permissionError(p);
					return true;
				}
			} else if(args.length == 0){
				//List the homes for the player who sent the command
				listHomes(p);
				return true;
			} else {
				//Tell the player if they've sent to many arguments with the command
				ChatUtils.tooManyArgs(p);
				return false;
			}
		}
		return false;
	}
	
	private void listHomes(Player p) {
		String uuid = p.getUniqueId().toString();
		String filler = StringUtils.repeat("-", 53);

		p.sendMessage(ChatColor.BOLD + "Your Currently Set Homes");
		p.sendMessage(filler);
		//Tell the player if they have a default home set or not
		if(pl.hasUnknownHomes(uuid)) {
			//Gets the name of the world the home has been set in
			String world = pl.getPlayersUnnamedHome(uuid).getWorld().getName();
			p.sendMessage(ChatColor.GOLD + "Default Home - World: " + world);
		}
		
		//Check to make sure the player has homes
		if(pl.hasNamedHomes(uuid)) {
			//Print the home with its description to the player
			for(String id : pl.getPlayersNamedHomes(uuid).keySet()) {
				//Gets the name of the world the home has been set in
				String world = pl.getPlayersNamedHomes(uuid).get(id).getWorld();
				//Gets the description for the home
				String desc = pl.getPlayersNamedHomes(uuid).get(id).getDesc();
				if(desc != null) {
					p.sendMessage(ChatColor.DARK_GREEN + "Name: " + id + " - World: " + world + " - Desc: " + desc);
				} else {
					p.sendMessage(ChatColor.DARK_GREEN + "Name: " + id + " - World: " + world);
				}
			}
		}
		p.sendMessage(filler);
	}
	
	//List homes for one player to another
	private void listHomes(UUID playerUUID, Player sender) {
		String uuid = playerUUID.toString();
		String filler = StringUtils.repeat("-", 53);
		
		sender.sendMessage(ChatColor.BOLD + "Homes currently set for the player - " + Bukkit.getOfflinePlayer(playerUUID).getName());
		sender.sendMessage(filler);
		
		//Tell the player if they have a default home set or not
		if(pl.hasUnknownHomes(uuid)) {
			//Gets the name of the world the home has been set in
			String world = pl.getPlayersUnnamedHome(uuid).getWorld().getName();
			sender.sendMessage(ChatColor.GOLD + "Default Home - World: " + world);
		}
		
		//Check to make sure the player has homes
		if(pl.hasNamedHomes(uuid)) {
			//Print the home with its description to the player
			for(String id : pl.getPlayersNamedHomes(uuid).keySet()) {
				//Gets the name of the world the home has been set in
				String world = pl.getPlayersNamedHomes(uuid).get(id).getWorld();
				//Gets the description for the home
				String desc = pl.getPlayersNamedHomes(uuid).get(id).getDesc();
				if(desc != null) {
					sender.sendMessage(ChatColor.DARK_GREEN + "Name: " + id + " - World: " + world + " - Desc: " + desc);
				} else {
					sender.sendMessage(ChatColor.DARK_GREEN + "Name: " + id + " - World: " + world);
				}
			}
		}
		sender.sendMessage(filler);
	}

}
