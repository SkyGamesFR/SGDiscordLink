package fr.skygames.sgdiscordlink.utils;

import net.dv8tion.jda.api.EmbedBuilder;

import java.time.temporal.TemporalAccessor;

public class EmbedUtils {

    private final EmbedBuilder embedBuilder;

    public EmbedUtils() {
        embedBuilder = new EmbedBuilder();
    }

    public EmbedBuilder errorEmbed(String title, String description) {
        embedBuilder.setTitle(title);
        embedBuilder.setDescription(description);
        embedBuilder.setColor(0xff0000);
        return embedBuilder;
    }

    public EmbedBuilder successEmbed(String title, String description) {
        embedBuilder.setTitle(title);
        embedBuilder.setDescription(description);
        embedBuilder.setColor(0x00ff00);
        return embedBuilder;
    }

    public EmbedBuilder infoEmbed(String title, String description) {
        embedBuilder.setTitle(title);
        embedBuilder.setDescription(description);
        embedBuilder.setColor(0x0000ff);
        return embedBuilder;
    }

    public EmbedBuilder warningEmbed(String title, String description) {
        embedBuilder.setTitle(title);
        embedBuilder.setDescription(description);
        embedBuilder.setColor(0xffff00);
        return embedBuilder;
    }

    public EmbedBuilder embed(String title, String description, int color, String url) {
        embedBuilder.setTitle(title);
        embedBuilder.setDescription(description);
        embedBuilder.setColor(color);
        embedBuilder.setImage(url);
        return embedBuilder;
    }


}
