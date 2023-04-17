package fr.skygames.sgdiscordlink.utils;

import fr.skygames.sgdiscordlink.Main;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class FileManager {

    private final Main instance;

    public FileManager(Main instance) {
        this.instance = instance;
    }

    public void createFile(String fileName) {
        if(!instance.getDataFolder().exists()) {
            instance.getDataFolder().mkdir();
        }

        File file = new File(instance.getDataFolder(), fileName + ".yml");

        if(!file.exists()) {
            try {
                file.createNewFile();
                addDefaultConfig(fileName, file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Configuration getConfig(String fileName) {
        try {
            return ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(instance.getDataFolder(), fileName + ".yml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveConfig(String fileName, Configuration config) {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, new File(instance.getDataFolder(), fileName + ".yml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void addDefaultConfig(String fileName, File file) throws IOException {
        if(fileName.equalsIgnoreCase("config")) {
            Configuration configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
            configuration.set("discordToken", "token");
            configuration.set("discordChannelId", "channelId");
            configuration.set("discordGuildId", "guildId");
            configuration.set("lang", "fr");
            this.saveConfig(fileName, configuration);
        }
        if(fileName.equalsIgnoreCase("apiconfig")) {
            Configuration configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
            configuration.set("url", "http://localhost:8686/api/v1/");
            configuration.set("api-key", "token");
            this.saveConfig(fileName, configuration);
        }

        if(fileName.equalsIgnoreCase("fr")) {
            Configuration configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
            configuration.set("globalJoinMessage.message", "&6[&a+&6] &6[%player_team%] %player_name%");
            configuration.set("globalQuitMessage.message", "&6[&4-&6]&6 &6[%player_team%] %player_name%");
            configuration.set("globalQuitMessage.enabled", "true");
            configuration.set("globalJoinMessage.enabled", "true");
            this.saveConfig(fileName, configuration);
        }
    }

}