package org.eu.pcraft.pepperminecart;

import dev.jorel.commandapi.*;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bstats.bukkit.Metrics;
import org.eu.pcraft.template.ConfigTemplate;

import java.util.*;

public final class PepperMinecart extends JavaPlugin {
    public static HashMap<Material, EntityType> changeMap = new HashMap<>();

    @Getter
    private static PepperMinecart instance;

    @Getter
    private final ConfigTemplate configTemplate = new ConfigTemplate();


    @Override
    public void onLoad() {
        CommandAPI.onLoad(new CommandAPIBukkitConfig(this).silentLogs(true));
    }

    @Override
    public void onEnable() {
        ////bStats////
        int pluginId = 21763;
        Metrics metrics = new Metrics(this, pluginId);

        ////init////
        Bukkit.getPluginManager().registerEvents(new Listener(), this);
        instance = this;
        changeMap.put(Material.HOPPER, EntityType.MINECART_HOPPER);
        changeMap.put(Material.CHEST, EntityType.MINECART_CHEST);
        changeMap.put(Material.TNT, EntityType.MINECART_TNT);
        changeMap.put(Material.COMMAND_BLOCK, EntityType.MINECART_COMMAND);
        changeMap.put(Material.FURNACE, EntityType.MINECART_FURNACE);

        ////config////
        saveDefaultConfig();
        configTemplate.loadConfig();

        ////Commands////
        CommandAPI.onEnable();
        new CommandAPICommand("PepperMinecart")
                .withArguments(
                        new GreedyStringArgument("subCommand")
                                .includeSuggestions(
                                        ArgumentSuggestions.strings("reload")
                                )
                )
                .withPermission(CommandPermission.OP)
                .withAliases("pm", "minecart")
                .executes((sender, args) -> {
                    if(Objects.equals(args.get("subCommand"), "reload")){
                        sender.sendMessage("[PepperMinecart] reloading...");
                        this.reloadConfig();
                        configTemplate.loadConfig();
                        sender.sendMessage("[PepperMinecart] Done!");
                    }
                })
                .register();
    }

    @Override
    public void onDisable() {
        CommandAPI.onDisable();
    }

}
