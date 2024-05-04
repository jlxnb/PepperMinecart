package org.eu.pcraft.template;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.eu.pcraft.pepperminecart.PepperMinecart;

public class ConfigTemplate {
    public void loadConfig() {
        FileConfiguration conf = PepperMinecart.getInstance().getConfig();
        enableCustomInteract = conf.getBoolean("enableCustomInteract");
    }

    @Getter
    private boolean enableCustomInteract;
}