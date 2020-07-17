package online.smyhw.EschatologicalUnit.ItemEntity;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;


public class smyhw extends JavaPlugin implements Listener 
{
	public static Plugin smyhw_;
	public static Logger loger;
	public static FileConfiguration configer;
	public static String prefix;
	@Override
    public void onEnable() 
	{
		getLogger().info("EschatologicalUnit.ItemEntity加载");
		getLogger().info("正在加载环境...");
		loger=getLogger();
		configer = getConfig();
		smyhw_=this;
		getLogger().info("正在加载配置...");
		saveDefaultConfig();
		prefix = configer.getString("config.prefix");
		getLogger().info("正在注册监听器...");
		Bukkit.getPluginManager().registerEvents(this,this);
		getLogger().info("EschatologicalUnit.ItemEntity加载完成");
    }

	@Override
    public void onDisable() 
	{
		getLogger().info("EschatologicalUnit.ItemEntity卸载");
    }
	
	
	@EventHandler
	public void cantDo(PlayerArmorStandManipulateEvent e)
	{
		if(e.getPlayerItem().getType()!=Material.AIR) {e.setCancelled(true);}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
        if (cmd.getName().equals("euIE"))
        {
                if(!sender.hasPermission("eu.plugin")) 
                {
                	sender.sendMessage(prefix+"非法使用 | 使用者信息已记录，此事将被上报");
                	loger.warning(prefix+"使用者<"+sender.getName()+">试图非法使用指令<"+args+">{权限不足}");
                	return true;
                }
               
                switch(args[0])
                {
                case "c":
                case "create":
                {
                	if(args.length<4) {CSBZ(sender);return true;}
                    Player p = (Player)sender;
                    ItemStack item = p.getInventory().getItemInMainHand();
                    Location l = p.getLocation();
                    l.setPitch(0);
                    l.setYaw(0);
                    l.setWorld(p.getWorld());
                    EulerAngle ea = new EulerAngle(new Double(args[1]),new Double(args[2]),new Double(args[3]));
                    create(item,l,ea);

                	if(args.length>4 && args[4].equals("config"))
                	{
                		Set<String> temp1 = configer.getConfigurationSection("data").getKeys(false);
                		int temp2 = temp1.size();
                		while(configer.getItemStack("data."+temp2+".item")!=null)
                		{temp2++;}
                		configer.set("data."+temp2+".item", item);
                		configer.set("data."+temp2+".x", l.getX());
                		configer.set("data."+temp2+".y", l.getY());
                		configer.set("data."+temp2+".z", l.getZ());
                		configer.set("data."+temp2+".jx",ea.getX());
                		configer.set("data."+temp2+".jy", ea.getY());
                		configer.set("data."+temp2+".jz", ea.getZ());
                		configer.set("data."+temp2+".world", p.getWorld().getName());
                		saveConfig();
                		sender.sendMessage(prefix+"该物品<"+temp1+">已被记录至配置文件");
                	}
                	sender.sendMessage(prefix+"创建完毕");
                    return true;
                }
                case "configer":
                {//将配置文件中的所有物品进行创建
                	Set<String> temp1 = configer.getConfigurationSection("data").getKeys(false);
                	for(String temp2:temp1)
                	{
                		ItemStack item = configer.getItemStack("data."+temp2+".item");
                		Double x = configer.getDouble("data."+temp2+".x");
                		Double y = configer.getDouble("data."+temp2+".y");
                		Double z = configer.getDouble("data."+temp2+".z");
                		Location location = new Location(Bukkit.getWorld(configer.getString("data."+temp2+".world")),x,y,z);
                		Double jx = configer.getDouble("data."+temp2+".jx");
                		Double jy = configer.getDouble("data."+temp2+".jy");
                		Double jz = configer.getDouble("data."+temp2+".jz");
                		EulerAngle ea = new EulerAngle(jx,jy,jz);
                		create(item,location,ea);
                		sender.sendMessage(prefix+"物品<"+temp2+">已被创建在< x="+x+" | y="+y+" | z="+z+" >");
                	}
                	return true;
                }
                default:
                	CSBZ(sender);
                	return true;
                }
                                              
        }
       return false;
	}
	
	/**
	 * 创建一个物品实体</br>
	 * @param item 需要创建的物体
	 * @param location 需要创建的位置(可能会有偏差，这是创建的盔甲架位置，物品应在盔甲架的头部)
	 * @param ea 物品偏转角
	 */
	static void create(ItemStack item,Location location, EulerAngle ea)
	{
        Entity en = location.getWorld().spawnEntity(location,EntityType.ARMOR_STAND);
        ArmorStand as = (ArmorStand)en;
//        as.setArms(true);
        as.setHelmet(item);
    	as.setHeadPose(ea);
    	as.setInvulnerable(true);//无敌
    	as.setGravity(false);//重力
    	as.setVisible(false);//隐身
	}
	
	static void CSBZ(CommandSender sender)
	{
		sender.sendMessage(prefix+"非法使用 | 使用者信息已记录，此事将被上报");
		loger.warning(prefix+"使用者<"+sender.getName()+">试图非法使用指令{参数不足}");
	}
	
}