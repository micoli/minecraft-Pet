package org.micoli.minecraft.petmanager.managers;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.micoli.minecraft.petmanager.PetManager;
import org.micoli.minecraft.utils.ChatFormater;
import org.micoli.minecraft.utils.ServerLogger;

// TODO: Auto-generated Javadoc
/**
 * The Class QDCommandManager.
 */
public final class QDCommandManager implements CommandExecutor {
	
	/** The plugin. */
	private PetManager plugin;

	/**
	 * Instantiates a new qD command manager.
	 *
	 * @param plugin the plugin
	 */
	public QDCommandManager(PetManager plugin) {
		this.plugin = plugin;
	}

	/* (non-Javadoc)
	 * @see org.bukkit.command.CommandExecutor#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		try {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				if (command.getName().equalsIgnoreCase(
						PetManager.getCommandString())) {
					if (args.length > 0) {
						ServerLogger.log("Command " + args[0]);
						if (args[0].equalsIgnoreCase("invoke")) {
							plugin.invokePet(player,
									(args.length == 2 ? args[1] : "LIST")
											.toUpperCase());
						} else if (args[0].equalsIgnoreCase("attack")) {
							plugin.setTarget(player);
						} else if (args[0].equalsIgnoreCase("heal")) {
							plugin.healPet(player);
						} else {
							player.sendMessage(ChatFormater
									.format("{ChatColor.RED} command unknown"));
						}
					} else {
						player.sendMessage(ChatFormater
								.format("{ChatColor.RED} Need more arguments"));
					}
					return true;
				}
			} else {
				ServerLogger.log("[petmanager] requires you to be a Player");
			}
			return false;
		} catch (Exception ex) {
			ServerLogger.log("[petmanager] Command failure: %s %s",
					ex.toString(), ex.getMessage());
		}

		return false;
	}
}