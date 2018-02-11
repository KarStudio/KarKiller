package top.karpvp.karfunction.karkiller;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import static org.bukkit.Bukkit.getPlayer;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class KarKiller extends JavaPlugin implements Listener, CommandExecutor {

    Map<String, Integer> ks = new HashMap<>();
    int highestKills = 5;
    Player best = null;

    @Override
    public void onEnable() {
        getLogger().info("载入插件KarKiller");
        Bukkit.getPluginManager().registerEvents(this, this);
        saveDefaultConfig();
        reloadConfig();
        getServer().getOnlinePlayers().stream().forEach((p) -> {
            ks.put(p.getName(), 0);
        });
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("karkillermap")) {
            if (args.length == 0) {
                sender.sendMessage("" + ks);
                return true;
            }
            if (args.length == 1) {
                if (getPlayer(args[0]) != null) {
                    sender.sendMessage("§e§l" + args[0] + " §7>连杀/死 §f§l" + ks.get(getPlayer(args[0]).getName()));
                    return true;
                }
                sender.sendMessage("§e玩家不在线");
                return true;
            }
            if (args.length == 2) {
                if (getPlayer(args[0]) != null) {
                    try {
                        int k = Integer.parseInt(args[1]);
                        ks.remove(args[0]);
                        ks.put(args[0], k);
                        return true;
                    } catch (NumberFormatException ex) {
                        sender.sendMessage("§4必须是数字");
                        return true;
                    }
                }
                sender.sendMessage("§e玩家不在线");
                return true;
            }
            sender.sendMessage("" + ks);
            return true;
        }
        return true;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        if (getConfig().getBoolean("NoHungry")) {
            PotionEffect pe = new PotionEffect(PotionEffectType.SATURATION, 99999, 1);
            p.addPotionEffect(pe);
        }
        if (getConfig().getBoolean("KSonLevel")) {
            p.setLevel(0);
        }
        ks.put(p.getName(), 0);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        if (getConfig().getBoolean("KSonLevel")) {
            p.setLevel(0);
        }
        ks.remove(p.getName());
    }

    @EventHandler
    public void onPlayerDeath2(PlayerDeathEvent event) {
        Player p = event.getEntity();
        String pn = p.getName();
        if (p.getKiller() != null) {
            Player killer = p.getKiller();
            String kn = killer.getName();
            
            //半血反杀
            if (killer.getHealth()<1.0D){
                getServer().broadcastMessage("§a§l" + kn + "§e半血反杀");
            }
            
            //终结连杀
            if ((int) ks.get(pn) >= 3 && (int) ks.get(pn) < 5) {
                getServer().broadcastMessage("§a§l" + kn + "§7终结了§a§l" + pn + "§f的§l3§7杀");
            } else if ((int) ks.get(pn) >= 5 && (int) ks.get(pn) < 10) {
                getServer().broadcastMessage("§a§l" + kn + "§7终结了§a§l" + pn + "§f的§l5§7杀");
            } else if ((int) ks.get(pn) >= 10 && (int) ks.get(pn) < 30) {
                getServer().broadcastMessage("§a§l" + kn + "§7终结了§a§l" + pn + "§f的§l10§7杀");
            } else if ((int) ks.get(pn) >= 30 && ks.get(pn) < 50) {
                getServer().broadcastMessage("§a§l" + kn + "§7终结了§a§l" + pn + "§f的§l30§7杀");
            }else if ((int) ks.get(pn) >= 50 && ks.get(pn) < 100) {
                getServer().broadcastMessage("§a§l" + kn + "§7终结了§a§l" + pn + "§f的§l50§7杀");
            }else if ((int) ks.get(pn) > 100) {
                getServer().broadcastMessage("§a§l" + kn + "§7终结了§a§l" + pn + "§f的§l100§7杀");
            }
            //终结连死
            if ((int) ks.get(kn) <= -3 && (int) ks.get(kn) > -5) {
                getServer().broadcastMessage("§a§l" + pn + "§7终结了§a§l" + kn + "§f的§l3§7连死");
            } else if ((int) ks.get(kn) <= -5 && (int) ks.get(kn) > -10) {
                getServer().broadcastMessage("§a§l" + pn + "§7终结了§a§l" + kn + "§f的§l5§7连死");
            } else if ((int) ks.get(kn) <= -10) {
                getServer().broadcastMessage("§a§l" + pn + "§7终结了§a§l" + kn + "§f的§l10§7连死,终于有人送ta了");
            }
        }
        if (ks.get(p.getName()) > 0) {
            ks.remove(p.getName());
            ks.put(p.getName(), -1);
        } else {
            int death = ks.get(p.getName());
            ks.remove(p.getName());
            ks.put(p.getName(), death - 1);
        }
        //连死
        if ((int) ks.get(pn) == -3) {
            getServer().broadcastMessage("§a§l" + pn + "§7完成了§f§l3§7连死");
        } else if ((int) ks.get(pn) == -5) {
            getServer().broadcastMessage("§7碉堡了!§a§l" + pn + "§7完成了§f§l5§7连死");
        } else if ((int) ks.get(pn) == -10) {
            getServer().broadcastMessage("§7谁来送一下ta啊!§a§l" + pn + "§7完成了§f§l10§7连死");
        }
        if (p.getKiller() != null) {
            Player killer = p.getKiller();
            String kn = killer.getName();
            if (ks.get(kn) < 0) {
                ks.remove(kn);
                ks.put(kn, 1);
            } else {
                int kills = ks.get(kn);
                ks.remove(kn);
                ks.put(kn, kills + 1);
            }
            //连杀
            if ((int) ks.get(kn) == 3) {
                getServer().broadcastMessage("§a§l" + kn + "§7完成了§f§l3§7杀");
            } else if ((int) ks.get(kn) == 5) {
                getServer().broadcastMessage("§7碉堡了!§a§l" + kn + "§7完成了§f§l5§7杀");
            } else if ((int) ks.get(kn) == 10) {
                getServer().broadcastMessage("§7简直大触!§a§l" + kn + "§7完成了§e§l10§7杀");
            } else if ((int) ks.get(kn) == 30) {
                getServer().broadcastMessage("§7简直开挂了!§a§l" + kn + "§7完成了§6§l30§7杀,谁来终结一下ta啊");
            } else if ((int) ks.get(kn) == 50) {
                getServer().broadcastMessage("§7这不可能!§a§l" + kn + "§7完成了§c§l50§7杀!");
            } else if ((int) ks.get(kn) == 100) {
                getServer().broadcastMessage("§7虐杀全场!§a§l" + kn + "§7完成了§5§l100§7杀");
            }
            
            if(ks.get(kn) > this.highestKills && killer != this.best){
                getServer().getOnlinePlayers().stream().forEach((pl) -> {
                    pl.sendTitle("§e§l" + kn, "§c§l全场最佳");
                });
                this.highestKills = ks.get(kn);
                this.best = killer;
            }

//            if ((int)ks.get(kn)<0){
//                ks.remove(kn);
//                ks.put(kn, 0);
//            }
//            int x=0;
//            try{
//                x =(int)ks.get(kn);
//            }catch(NullPointerException e){
//                ks.put(kn,0);
//                x =(int)ks.get(kn);
//            }
//            ks.remove(kn);//由于在map中的值不能自增，所以删掉再重新加上去
//            ks.put(kn, ++x);
            //判断是否在level中显示
            if (getConfig().getBoolean("KSonLevel")) {
                killer.setLevel(ks.get(kn));
            }
            if (p.getName().equals(p.getKiller().getName())) {
                ks.remove(p.getName());
                ks.put(p.getName(), 0);
            }
        }

        if (getConfig().getBoolean("KSonLevel")) {
//            Player p = event.getEntity();
            p.setLevel(0);
        }
        if (getConfig().getBoolean("NoHungry")) {
//            Player p = event.getEntity();
            noHungry(p);
        }
        new BukkitRunnable() {
            int x = 0;

            @Override
            public void run() {
                if (x == 2) {
                    if (getConfig().getBoolean("KillerSetHealth")) {
                        Player killer = event.getEntity().getKiller();
                        double health = getConfig().getDouble("HealthSetLevel");
                        try {
                            if (killer != null) {
                                killer.setHealth(health);
                            }
                        } catch (Exception e) {
                        }
                    }
                    if (getConfig().getBoolean("KillerAbsorption")) {
                        Player killer = event.getEntity().getKiller();
                        if (killer != null) {
                            killer.removePotionEffect(PotionEffectType.ABSORPTION);
                            addAbsorption(killer);
                        }
                    }
                    if (getConfig().getBoolean("Health")) {
                        Player killer = event.getEntity().getKiller();
                        if (killer != null) {
                            int level = getConfig().getInt("HealthLevel");
                            int time = getConfig().getInt("HealthTime");
                            PotionEffect pe = new PotionEffect(PotionEffectType.REGENERATION, time, level);
                            killer.addPotionEffect(pe);
                        }
                    }
                    if (getConfig().getBoolean("Sound")) {
                        Player killer = event.getEntity().getKiller();
                        if (killer != null) {
                            killer.playSound(killer.getLocation(), Sound.ORB_PICKUP, 0.5f, 1);
                        }
                    }
                    cancel();
                }
                x++;
            }
        }.runTaskTimer((Plugin) this, 1L, 1L);

    }

//    @EventHandler
//    public void onPlayerDeath(PlayerDeathEvent event){
//        Player p = event.getEntity();
//        String pn = p.getDisplayName();
//        if (ks.get(pn)==null){
//            ks.put(pn, 0);
//        }
//        int y=0;
//        try{
//            y =(int)ks.get(pn);
//        }catch(NullPointerException e){
//            ks.put(pn, 0);
//            y =(int)ks.get(pn);
//        }
//        ks.remove(pn);
//        ks.put(pn, --y);
//        //连死
//        if ((int)ks.get(pn)==-3){
//            getServer().broadcastMessage("§a§l"+pn+"§7完成了§f§l3§7连死");
//        }else if ((int)ks.get(pn)==-5){
//            getServer().broadcastMessage("§7碉堡了!§a§l"+pn+"§7完成了§f§l5§7连死");
//        }else if ((int)ks.get(pn)==-10){
//            getServer().broadcastMessage("§7谁来送一下ta啊!§a§l"+pn+"§7完成了§f§l10§7连死");
//        }
//        if (p.getKiller()!=null){
//            Player killer=p.getKiller();
//            String kn = killer.getDisplayName();
//            if (ks.get(kn)==null){
//                ks.put(kn, 0);
//            }
//            //连杀
//            if ((int)ks.get(kn)==3){
//                getServer().broadcastMessage("§a§l"+kn+"§7完成了§f§l3§7杀");
//            }else if ((int)ks.get(kn)==5){
//                getServer().broadcastMessage("§7碉堡了!§a§l"+kn+"§7完成了§f§l5§7杀");
//            }else if ((int)ks.get(kn)==10){
//                getServer().broadcastMessage("§7简直大触!§a§l"+kn+"§7完成了§f§l10§7杀");
//            }else if ((int)ks.get(kn)==30){
//                getServer().broadcastMessage("§7简直开挂了!§a§l"+kn+"§7完成了§f§l30§7杀,谁来终结一下ta啊");
//            }
//            //终结连杀
//            if ((int)ks.get(pn)>=3&&(int)ks.get(pn)<5){
//                getServer().broadcastMessage("§a§l"+kn+"§7终结了§a§l"+pn+"§f的§l3§7杀");
//            }else if ((int)ks.get(pn)==5&&(int)ks.get(pn)<10){
//                getServer().broadcastMessage("§a§l"+kn+"§7终结了§a§l"+pn+"§f的§l5§7杀");
//            }else if ((int)ks.get(pn)==10&&(int)ks.get(pn)<30){
//                getServer().broadcastMessage("§a§l"+kn+"§7终结了§a§l"+pn+"§f的§l10§7杀");
//            }else if ((int)ks.get(pn)>=30){
//                getServer().broadcastMessage("§a§l"+kn+"§7终结了§a§l"+pn+"§f的§l30§7杀");
//            }
//            //终结连死
//            if ((int)ks.get(kn)<=-3&&(int)ks.get(kn)>-5){
//                getServer().broadcastMessage("§a§l"+pn+"§7终结了§a§l"+kn+"§f的§l3§7连死");
//            }else if ((int)ks.get(kn)<=-5&&(int)ks.get(kn)>-10){
//                getServer().broadcastMessage("§a§l"+pn+"§7终结了§a§l"+kn+"§f的§l5§7连死");
//            }else if ((int)ks.get(kn)<=-10){
//                getServer().broadcastMessage("§a§l"+pn+"§7终结了§a§l"+kn+"§f的§l10§7连死,终于有人送ta了");
//            }
//            if ((int)ks.get(kn)<0){
//                ks.remove(kn);
//                ks.put(kn, 0);
//            }
//            int x=0;
//            try{
//                x =(int)ks.get(kn);
//            }catch(NullPointerException e){
//                ks.put(kn,0);
//                x =(int)ks.get(kn);
//            }
//            ks.remove(kn);//由于在map中的值不能自增，所以删掉再重新加上去
//            ks.put(kn, ++x);
//            //判断是否在level中显示
//            if (getConfig().getBoolean("KSonLevel")){
//                killer.setLevel(x++);
//            }
//            if (p.getName()==p.getKiller().getName()){
//                ks.remove(p.getName());
//                ks.put(p.getName(),0);
//            }
//        }
//        
//        if (getConfig().getBoolean("KSonLevel")){
////            Player p = event.getEntity();
//            p.setLevel(0);
//        }
//        if (getConfig().getBoolean("NoHungry")){
////            Player p = event.getEntity();
//            noHungry(p);
//        }
//        new BukkitRunnable()
//        {
//            int x = 0;
//            @Override
//            public void run() 
//            {
//                if (x == 2){
//                    if (getConfig().getBoolean("KillerSetHealth")){
//                        Player killer = event.getEntity().getKiller();
//                        double health = getConfig().getDouble("HealthSetLevel");
//                        try{
//                            if (killer!=null)
//                            killer.setHealth(health);
//                        }catch(Exception e){
//                        }
//                    }
//                    if (getConfig().getBoolean("KillerAbsorption")){
//                        Player killer = event.getEntity().getKiller();
//                        if (killer!=null){
//                        killer.removePotionEffect(PotionEffectType.ABSORPTION);
//                        addAbsorption(killer);
//                        }
//                    }
//                    if(getConfig().getBoolean("Health")){
//                        Player killer = event.getEntity().getKiller();
//                        if (killer!=null){
//                        int level = getConfig().getInt("HealthLevel");
//                        int time = getConfig().getInt("HealthTime");
//                        PotionEffect pe = new PotionEffect(PotionEffectType.REGENERATION,time,level);
//                        killer.addPotionEffect(pe);
//                        }
//                    }
//                    if(getConfig().getBoolean("Sound")){
//                        Player killer = event.getEntity().getKiller();
//                        if (killer != null){
//                            killer.playSound(killer.getLocation(), Sound.ORB_PICKUP, 0.5f, 1);
//                        }
//                    }
//                    cancel();
//                }
//                x++;
//            }
//        }.runTaskTimer((Plugin)this , 1L ,1L );
//    }
    public void addAbsorption(Player p) {

        new BukkitRunnable() {
            int x = 0;

            @Override
            public void run() {
                if (x == 0) {
                    x++;
                }
                if (x == 1) {
                    int level = getConfig().getInt("AbsorptionLevel");
                    int time = getConfig().getInt("AbsorptionTime");
                    PotionEffect pe = new PotionEffect(PotionEffectType.ABSORPTION, time, level);
                    p.addPotionEffect(pe);
                    cancel();
                }
            }
        }.runTaskTimer((Plugin) this, 1L, 1L);

    }

    public void noHungry(Player p) {
        new BukkitRunnable() {
            int x = 0;

            @Override
            public void run() {
                if (x == 1) {
                    cancel();  // 终止线程
//                     return;
                }
                if (x == 0) {
                    PotionEffect pe = new PotionEffect(PotionEffectType.SATURATION, 99999, 1);
                    p.addPotionEffect(pe);
                    x++;
                }
            }
        }.runTaskTimer((Plugin) this, 20L, 20L);
    }

}
