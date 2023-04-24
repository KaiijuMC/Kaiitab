package dev.kugge.kaiitab;

import dev.kugge.kaiitab.watcher.LoginWatcher;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class Kaiitab extends JavaPlugin {

    public static Logger logger;
    public static Kaiitab instance;
    public static double gmspt;

    @Override
    public void onEnable() {
        instance = this;
        logger = getLogger();
        register();
    }

    private void register(){
        //Bukkit.getGlobalRegionScheduler().runAtFixedRate(instance, task -> gmspt = Util.getGlobalMspt(), 10, 200);
        Bukkit.getPluginManager().registerEvents(new LoginWatcher(), this);
    }
}
