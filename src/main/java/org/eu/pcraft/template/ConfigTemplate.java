package org.eu.pcraft.template;
import static org.eu.pcraft.pepperminecart.PepperMinecart.yaml;

public class ConfigTemplate {
    public void loadConfig(){
        enableCustomInteract= yaml.getBoolean("enableCustomInteract");
    }
    public boolean enableCustomInteract;

}