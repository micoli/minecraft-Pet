package org.micoli.minecraft.petmanager;

import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.Vector;
import org.micoli.minecraft.bukkit.QDBukkitPlugin;
import org.micoli.minecraft.petmanager.entities.QDObjectPet;
import org.micoli.minecraft.petmanager.listeners.QDListener;
import org.micoli.minecraft.petmanager.managers.QDCommandManager;
import org.micoli.minecraft.utils.ChatFormater;
import org.micoli.minecraft.utils.EntityManagement;
import org.micoli.minecraft.utils.ServerLogger;

/**
 * The Class PetManager.
 */
public class PetManager extends QDBukkitPlugin implements ActionListener {
	
	/** The executor. */
	private QDCommandManager executor;
	
	/** The instance. */
	private static PetManager instance;
	
	/** The a pets. */
	private static Map<String, QDObjectPet> aPets;

	/** The pet egg types. */
	private static HashMap<Integer,EntityType> petEggTypes=new HashMap<Integer,EntityType>() {
		private static final long serialVersionUID = 7921987478075440858L;
	{
		put( 50, EntityType.CREEPER);
		put( 51, EntityType.SKELETON);
		put( 52, EntityType.SPIDER);
		put( 54, EntityType.ZOMBIE);
		put( 55, EntityType.SLIME);
		put( 56, EntityType.GHAST);
		put( 57, EntityType.PIG_ZOMBIE);
		put( 58, EntityType.ENDERMAN);
		put( 59, EntityType.CAVE_SPIDER);
	}};

	/**
	 * Gets the single instance of PetManager.
	 *
	 * @return single instance of PetManager
	 */
	public static PetManager getInstance() {
		return instance;
	}

	/* (non-Javadoc)
	 * @see org.micoli.minecraft.bukkit.QDBukkitPlugin#onEnable()
	 */
	@Override
	public void onEnable() {
		super.onEnable();
		commandString				= "qdpet";
		aPets						= new HashMap<String, QDObjectPet>();
		instance					= this;
		executor					= new QDCommandManager(this);
		PluginManager pm 			= getServer().getPluginManager();
		pm.registerEvents(new QDListener(this), this);
		getCommand(getCommandString()).setExecutor(executor);
	}

	/**
	 * Gets the egg list.
	 *
	 * @param player the player
	 * @return the egg list
	 */
	public ArrayList<EntityType> getEggList(Player player){
		ArrayList<EntityType> rtn = new ArrayList<EntityType>();
		PlayerInventory inventory = player.getInventory();

		try{
			for(int i = 0; i < inventory.getSize(); i++){
				ItemStack item = inventory.getItem(i);
				if (item.getType() == Material.MONSTER_EGG) {
					int subData = (int) item.getData().getData();
					if (petEggTypes.containsKey(subData)) {
						rtn.add(petEggTypes.get(subData));
						ServerLogger.log("EggType " + petEggTypes.get(subData).toString());
					}
				}
			}
		} catch (Exception ex) {
			ServerLogger.log("Exception "+ex.toString());
		}
		return rtn;
	}

	/**
	 * Invoke pet.
	 *
	 * @param owner the owner
	 * @param subCommand the sub command
	 */
	public void invokePet(Player owner,String subCommand){
		ArrayList<EntityType> myEggs = getEggList(owner);

		if(subCommand.equalsIgnoreCase("LIST")){
			sendComments(owner,"You can invoke "+ myEggs.toString(),false);
			return;
		}
		if (aPets.containsKey(owner.getName())){
			sendComments(owner,"You already have a pet",false);
		}else{
			EntityType newMobType = null;
			for(EntityType typ : myEggs){
				if (typ.toString().equalsIgnoreCase(subCommand)){
					newMobType = typ;
				}
			}
			if (newMobType != null){
				QDObjectPet pet = new QDObjectPet(owner,newMobType);
				aPets.put(owner.getName(), pet);
			}else{
				String typeList = "";
				String typeSepa = "";
				Iterator<Integer> iterator = petEggTypes.keySet().iterator();
				while (iterator.hasNext()) {
					Integer key = iterator.next();
					EntityType type = (EntityType) petEggTypes.get(key);
					typeList = typeList + typeSepa + type.toString().toUpperCase();
					typeSepa = ", ";
				}
				sendComments(owner,"You don't have the egg corresponding to that Monster which can be : "+typeList,false);
			}
		}
		//owner.getWorld().spawnCreature(owner.getLocation(), CreatureType.CHICKEN);
		//owner.getWorld().spawnCreature(owner.getLocation(), CreatureType.COW);
		//owner.getWorld().spawnCreature(owner.getLocation(), CreatureType.GHAST);
		//owner.getWorld().spawnCreature(owner.getLocation(), CreatureType.PIG);
	}

	/**
	 * Entity die.
	 *
	 * @param dead the dead
	 */
	public static void EntityDie(Entity dead){
		ServerLogger.log("testing dead of "+dead.toString());
		Iterator<String> iterator = aPets.keySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			QDObjectPet pet = (QDObjectPet) aPets.get(key);
			if (pet.getMob() == dead){
				pet.die();
				sendComments(pet.getOwner(),"Your pet died",false);
				ServerLogger.log("pet from " + key + " died");
				aPets.remove(key);
			}
		}
	}

	/**
	 * Mount target.
	 *
	 * @param player the player
	 */
	public void mountTarget(Player player){
		Entity tgt = EntityManagement.getTarget(player);
		if (tgt!= null){
			tgt.setPassenger(player);
		}else{
			player.leaveVehicle();
		}
	}
	
	/**
	 * Sets the target.
	 *
	 * @param player the new target
	 */
	public void setTarget(Player player){
		if (aPets.containsKey(player.getName())){
			Entity tgt = EntityManagement.getTarget(player);
			Date now = new Date();
			SimpleDateFormat hourFmt = new SimpleDateFormat("HH:mm:ss");
			sendComments(player,"target "+tgt.toString()+" "+hourFmt.format(now),false);
			((Monster) aPets.get(player.getName()).getMob()).setTarget(tgt==null?null:(LivingEntity)tgt);
		}else{
			sendComments(player,ChatFormater.format("You don't have {ChatColor.RED}a pet"),false);
		}
	}

	/**
	 * Heal pet.
	 *
	 * @param player the player
	 */
	public void healPet(Player player){
		if (aPets.containsKey(player.getName())){
			LivingEntity pet = aPets.get(player.getName()).getMob();
			sendComments(player,ChatFormater.format("Your pet was at %d/20, now it is full life",pet.getHealth()),false);
			pet.setHealth(pet.getMaxHealth());
		}
	}

	/**
	 * Player move.
	 *
	 * @param player the player
	 */
	public void playerMove(Player player) {
		Iterator<String> iterator = aPets.keySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			QDObjectPet pet = (QDObjectPet) aPets.get(key);
			if (player == pet.getOwner()){
				Location loc = player.getLocation();
				pet.getMob().teleport(loc.add(new Vector(0,0,1)));
			}
		}
	}
}