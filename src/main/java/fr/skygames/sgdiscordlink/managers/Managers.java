package fr.skygames.sgdiscordlink.managers;

import fr.skygames.sgdiscordlink.Main;
import fr.skygames.sgdiscordlink.listeners.bungee.BungeeListener;
import fr.skygames.sgdiscordlink.listeners.bungee.DiscordLinkCommand;
import fr.skygames.sgdiscordlink.utils.CustomLogger;
import fr.skygames.sgdiscordlink.utils.FileManager;
import net.md_5.bungee.api.plugin.PluginManager;

public class Managers {

    private final Main main;
    private final CustomLogger logger;
    private JDAManager jdaManager;

    public Managers(Main main, CustomLogger logger) {
        this.main = main;
        this.logger = logger;
    }

    public void load() {
        logger.info("Loading managers...");

        FileManager fileManager = new FileManager(main);
        fileManager.createFile("config");
        fileManager.createFile("globalMessages");
        fileManager.createFile("apiconfig");

        PluginManager pm = main.getProxy().getPluginManager();
        pm.registerListener(main, new BungeeListener(fileManager));
        pm.registerCommand(main, new DiscordLinkCommand(fileManager));

        logger.info("Loading JDA...");
        jdaManager = new JDAManager(main, logger, fileManager);
        jdaManager.load();

        logger.info("Managers loaded !");
    }

    public void shutdown() {
        logger.info("Shutting down managers...");
        jdaManager.shutdown();
        logger.info("Managers shut down !");
    }
}
