package fr.skygames.sgdiscordlink;

import fr.skygames.sgdiscordlink.managers.Managers;
import fr.skygames.sgdiscordlink.utils.CustomLogger;
import net.md_5.bungee.api.plugin.Plugin;

public final class Main extends Plugin {

    private static Main instance;
    private Managers managers;
    private CustomLogger logger;

    @Override
    public void onEnable() {
        instance = this;
        logger = new CustomLogger(this);
        logger.info("Loading plugin...");
        managers = new Managers(this, logger);
        managers.load();
    }

    @Override
    public void onDisable() {
        logger.info("Shutting down plugin...");
        managers.shutdown();
    }

    public static Main getInstance() {
        return instance;
    }
}
