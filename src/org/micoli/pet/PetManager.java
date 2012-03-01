package org.micoli.pet;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.micoli.minecraft.utils.ChatFormater;
import org.micoli.minecraft.utils.EntityManagement;
import org.micoli.pet.listeners.QDListener;
import org.micoli.pet.managers.QDCommandManager;

public class PetManager extends JavaPlugin implements ActionListener {
	private static Logger logger = Logger.getLogger("Minecraft");
	private QDCommandManager myExecutor;
	private static PetManager instance;
	private static Map<String, QDObjectPet> aPets;
	private static String commandString = "qdpet";
	private static boolean comments = true;
	private static String lastMsg = "";

	private static HashMap<Integer,CreatureType> petEggTypes=new HashMap<Integer,CreatureType>() {
		private static final long serialVersionUID = 7921987478075440858L;
	{
		put( 50, CreatureType.CREEPER);
		put( 51, CreatureType.SKELETON);
		put( 52, CreatureType.SPIDER);
		put( 54, CreatureType.ZOMBIE);
		put( 55, CreatureType.SLIME);
		put( 56, CreatureType.GHAST);
		put( 57, CreatureType.PIG_ZOMBIE);
		put( 58, CreatureType.ENDERMAN);
		put( 59, CreatureType.CAVE_SPIDER);
	}};

	public static PetManager getInstance() {
		return instance;
	}

	public static String getCommandString() {
		return commandString;
	}

	public static void setComments(Player player, boolean active) {
		comments = active;
		player.sendMessage(ChatFormater.format("{ChatColor.RED} %s", (active ? "comments activated" : "comments desactived")));
	}

	public static boolean getComments() {
		return comments;
	}

	public static void log(String str) {
		logger.info(str);
	}

	public void actionPerformed(ActionEvent event) {
	}

	public static void sendComments(Player player, String text, boolean global) {
		if (getComments()) {
			if (!PetManager.lastMsg.equalsIgnoreCase(text)) {
				PetManager.lastMsg = text + "";
				if (global) {
					getInstance().getServer().broadcastMessage(text);
				} else {
					player.sendMessage(text);
				}
			}
		}
	}

	@Override
	public void onDisable() {
		PluginDescriptionFile pdfFile = getDescription();
		log(ChatFormater.format("%s version disabled", pdfFile.getName(), pdfFile.getVersion()));
	}

	@Override
	public void onEnable() {
		aPets						= new HashMap<String, QDObjectPet>();
		instance					= this;
		myExecutor					= new QDCommandManager(this);
		PluginManager pm 			= getServer().getPluginManager();
		PluginDescriptionFile pdfFl	= getDescription();
		pm.registerEvents(new QDListener(this), this);
		getCommand(getCommandString()).setExecutor(myExecutor);

		log(ChatFormater.format("%s version enabled", pdfFl.getName(), pdfFl.getVersion()));
	}

	public ArrayList<CreatureType> getEggList(Player player){
		ArrayList<CreatureType> rtn = new ArrayList<CreatureType>();
		ItemStack it[] = player.getInventory().getContents();
		try{
			for (ItemStack i : it) {
				if (i.getType() == Material.MONSTER_EGG) {
					int subData = (int) i.getData().getData();
					if (petEggTypes.containsKey(subData)) {
						rtn.add(petEggTypes.get(subData));
						log("ee" + petEggTypes.get(subData).toString());
					}
				}
			}
		} catch (Exception ex) {
			log("rrrr"+ex.toString());
		}
		return rtn;
	}

	public void invokePet(Player owner,String subCommand){
		ArrayList<CreatureType> myEggs = getEggList(owner);

		if(subCommand.equalsIgnoreCase("list")){
			sendComments(owner,"You can invoke "+ myEggs.toString(),false);
			return;
		}
		if (aPets.containsKey(owner.getName())){
			sendComments(owner,"You already have a pet",false);
		}else{
			CreatureType newMobType = null;
			for(CreatureType typ : myEggs){
				if (typ.toString().equalsIgnoreCase(subCommand)){
					newMobType = typ;
				}
			}
			if (newMobType != null){
				QDObjectPet pet = new QDObjectPet(owner,newMobType);
				aPets.put(owner.getName(), pet);
			}else{
				sendComments(owner,"Unknown pet type",false);
			}
		}
		owner.getWorld().spawnCreature(owner.getLocation(), CreatureType.CHICKEN);
		owner.getWorld().spawnCreature(owner.getLocation(), CreatureType.COW);
		owner.getWorld().spawnCreature(owner.getLocation(), CreatureType.GHAST);
		owner.getWorld().spawnCreature(owner.getLocation(), CreatureType.PIG);
	}

	public static void EntityDie(Entity dead){
		log("testing dead of "+dead.toString());
		Iterator<String> iterator = aPets.keySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			QDObjectPet pet = (QDObjectPet) aPets.get(key);
			if (pet.mob == dead){
				pet.die();
				sendComments(pet.owner,"Your pet died",false);
				log("pet from " + key + " died");
				aPets.remove(key);
			}
		}
	}

	public void setTarget(Player player){
		if (aPets.containsKey(player.getName())){
			Entity tgt = EntityManagement.getTarget(player);
			Date now = new Date();
			SimpleDateFormat hourFmt = new SimpleDateFormat("HH:mm:ss");
			sendComments(player,"target "+tgt.toString()+" "+hourFmt.format(now),false);
			((Monster) aPets.get(player.getName()).mob).setTarget(tgt==null?null:(LivingEntity)tgt);
		}
	}

	public void playerMove(Player player) {
		Iterator<String> iterator = aPets.keySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			QDObjectPet pet = (QDObjectPet) aPets.get(key);
			if (player == pet.owner){
				//Location loc = player.getLocation();
				//pet.mob.teleport(loc.add(new Vector(0,0,1)));
			}
		}
	}
}