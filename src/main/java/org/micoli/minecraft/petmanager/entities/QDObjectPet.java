package org.micoli.minecraft.petmanager.entities;

import org.bukkit.EntityEffect;
import org.bukkit.World;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.micoli.minecraft.petmanager.PetManager;
import org.micoli.minecraft.utils.ServerLogger;
import org.micoli.minecraft.utils.Task;

public class QDObjectPet {
	Player owner;
	LivingEntity mob;

	public Player getOwner() {
		return owner;
	}

	public void setOwner(Player owner) {
		this.owner = owner;
	}

	public LivingEntity getMob() {
		return mob;
	}

	public void setMob(LivingEntity mob) {
		this.mob = mob;
	}

	Task runningTask;
	static PetManager plugin;
	Double maxDistance = 3D;
	boolean isRunning = true;

	public QDObjectPet(Player player, CreatureType typ) {
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

	public void die() {
		isRunning = false;
		runningTask.stop();
	}

	private LivingEntity getMobTarget() {
		return ((Monster) mob).getTarget();
	}

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