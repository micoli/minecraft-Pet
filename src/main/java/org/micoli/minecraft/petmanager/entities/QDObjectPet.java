package org.micoli.minecraft.petmanager.entities;

import org.bukkit.EntityEffect;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.micoli.minecraft.petmanager.PetManager;
import org.micoli.minecraft.utils.ServerLogger;
import org.micoli.minecraft.utils.Task;

/**
 * The Class QDObjectPet.
 */
public class QDObjectPet {
	
	/** The owner. */
	Player owner;
	
	/** The mob. */
	LivingEntity mob;

	/**
	 * Gets the owner.
	 *
	 * @return the owner
	 */
	public Player getOwner() {
		return owner;
	}

	/**
	 * Sets the owner.
	 *
	 * @param owner the new owner
	 */
	public void setOwner(Player owner) {
		this.owner = owner;
	}

	/**
	 * Gets the mob.
	 *
	 * @return the mob
	 */
	public LivingEntity getMob() {
		return mob;
	}

	/**
	 * Sets the mob.
	 *
	 * @param mob the new mob
	 */
	public void setMob(LivingEntity mob) {
		this.mob = mob;
	}

	/** The running task. */
	Task runningTask;
	
	/** The plugin. */
	static PetManager plugin;
	
	/** The max distance. */
	Double maxDistance = 3D;
	
	/** The is running. */
	boolean isRunning = true;

	/**
	 * Instantiates a new qD object pet.
	 *
	 * @param player the player
	 * @param typ the typ
	 */
	public QDObjectPet(Player player, EntityType typ) {
		World world = player.getWorld();
		owner = player;
		plugin = PetManager.getInstance();
		mob = world.spawnCreature(player.getLocation(), typ);
		mob.playEffect(EntityEffect.WOLF_SMOKE);
		world.strikeLightningEffect(player.getLocation().add(0, 3, 0));
		runningTask = new Task(plugin, this) {
			public void run() {
				ServerLogger.log("ee");
				((QDObjectPet) this.getArg(0)).run();
				if (isRunning) {
					this.startDelayed(10L);
				}
			}
		};
		runningTask.startDelayed(10L);
	}

	/**
	 * Die.
	 */
	public void die() {
		isRunning = false;
		runningTask.stop();
	}

	/**
	 * Gets the mob target.
	 *
	 * @return the mob target
	 */
	private LivingEntity getMobTarget() {
		return ((Monster) mob).getTarget();
	}

	/**
	 * Run.
	 */
	public void run() {
		if (mob.isDead()) {
			PetManager.EntityDie(mob);
		}

		if (getMobTarget() != null) {
			return;
		}

		while (owner.getLocation().distance(mob.getLocation()) > maxDistance) {
			Vector own2mob = mob.getLocation().subtract(owner.getLocation())
					.toVector();
			// PetManager.log(own2mob.toString());
			mob.teleport(mob.getLocation().add(own2mob.multiply(-0.2)));
		}
	}
}