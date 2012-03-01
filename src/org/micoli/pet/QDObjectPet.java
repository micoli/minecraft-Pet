package org.micoli.pet;

import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class QDObjectPet {
	Player				owner;
	LivingEntity		mob;
	Task				runningTask;
	static PetManager	plugin;
	Double				maxDistance = 3D;


	public QDObjectPet(Player player,CreatureType typ) {
		World world	= player.getWorld();
		owner		= player;
		plugin		= PetManager.getInstance();
		mob			= world.spawnCreature(player.getLocation(), typ);
		mob.playEffect(EntityEffect.WOLF_SMOKE);
		runningTask = new Task(plugin, this) {
			public void run() {
				((QDObjectPet) this.getArg(0)).run();
				this.startDelayed(10L);
			}
		};
		runningTask.startDelayed(10L);
	}

	public void die(){
		runningTask.stop();
	}

	public void run(){
		if (((Monster)mob).getTarget()!=null){
			return;
		}
		Location ownLoc = owner.getLocation();
		Location mobLoc = mob.getLocation();
		Double dist = ownLoc.distance(mobLoc);
		while (dist>maxDistance){
			Vector own2mob = mobLoc.subtract(ownLoc).toVector();
			//PetManager.log(own2mob.toString());
			mob.teleport(mob.getLocation().add(own2mob.multiply(-0.2)));
			mobLoc = mob.getLocation();
			dist = ownLoc.distance(mobLoc);
		}
	}
}
