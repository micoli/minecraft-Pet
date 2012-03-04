package org.micoli.minecraft.petmanager.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Creeper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.micoli.minecraft.petmanager.PetManager;
import org.micoli.minecraft.utils.ServerLogger;

public class QDListener implements Listener {
	PetManager plugin;

	public QDListener(PetManager plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		PetManager.EntityDie(event.getEntity());
	}

	void onEntityDamage(EntityDamageEvent event) {
		ServerLogger.log(event.getEntity().toString());
		if (event.getEntity() instanceof Creeper) {
			PetManager.EntityDie(event.getEntity());
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Action action = event.getAction();
		if (action == Action.RIGHT_CLICK_AIR
				&& event.getPlayer().getInventory().getItemInHand().getType() == Material.ENDER_PEARL) {
			plugin.setTarget(event.getPlayer());
			event.setCancelled(true);
		}
		if (action == Action.RIGHT_CLICK_AIR
				&& event.getPlayer().getInventory().getItemInHand().getType() == Material.SADDLE) {
			plugin.mountTarget(event.getPlayer());
			event.setCancelled(true);
		}

	}

	/*@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		plugin.playerMove(event.getPlayer());
	}*/
}