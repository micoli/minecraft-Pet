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

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving QD events.
 * The class that is interested in processing a QD
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addQDListener<code> method. When
 * the QD event occurs, that object's appropriate
 * method is invoked.
 *
 * @see QDEvent
 */
public class QDListener implements Listener {
	
	/** The plugin. */
	PetManager plugin;

	/**
	 * Instantiates a new qD listener.
	 *
	 * @param plugin the plugin
	 */
	public QDListener(PetManager plugin) {
		this.plugin = plugin;
	}

	/**
	 * On entity death.
	 *
	 * @param event the event
	 */
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		PetManager.EntityDie(event.getEntity());
	}

	/**
	 * On entity damage.
	 *
	 * @param event the event
	 */
	void onEntityDamage(EntityDamageEvent event) {
		ServerLogger.log(event.getEntity().toString());
		if (event.getEntity() instanceof Creeper) {
			PetManager.EntityDie(event.getEntity());
		}
	}

	/**
	 * On player interact.
	 *
	 * @param event the event
	 */
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