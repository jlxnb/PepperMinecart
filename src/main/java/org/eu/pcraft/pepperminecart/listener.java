package org.eu.pcraft.pepperminecart;

import de.tr7zw.changeme.nbtapi.NBTEntity;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleUpdateEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.Map;
import java.util.Objects;

import static org.eu.pcraft.pepperminecart.PepperMinecart.changeMap;
import static org.eu.pcraft.pepperminecart.PepperMinecart.*;


public class listener implements Listener {
    @EventHandler
    void onMove(VehicleUpdateEvent event){
        //Unfinished! Welcome PRs!
        Minecart minecart=(Minecart) event.getVehicle();
        if(minecart.getDisplayBlockData().getMaterial()==Material.REDSTONE_BLOCK){
            redstoneMinecartSet.add(minecart.getUniqueId());
        }
    }

    @EventHandler
    void onDestroy(VehicleDestroyEvent event) {
        Entity minecart = event.getVehicle();
        NBTEntity entity = new NBTEntity(minecart);
        ItemStack item = entity.getPersistentDataContainer().getItemStack("BlockInfo");
        if (item != null)
            minecart.getWorld().dropItem(minecart.getLocation(), item);
    }
    @EventHandler
    void onCloseInv(InventoryCloseEvent event){
        //if(event.getInventory().getHolder()==)
    }
    @EventHandler
    void onInteract(PlayerInteractEntityEvent event) {
        if (event.getHand() == EquipmentSlot.HAND) {
            if (event.getRightClicked().getType() == EntityType.MINECART) {
                Minecart minecart = (Minecart) event.getRightClicked();
                Player player = event.getPlayer();
                ItemStack item = player.getInventory().getItemInMainHand();
                if (!minecart.getDisplayBlockData().getMaterial().isAir()) {//矿车不为空 不能上去
                    event.setCancelled(true);
                }

                if (!player.isSneaking()) {//交互部分
                    if (PepperMinecart.getInstance().getConfigTemplate().isEnableCustomInteract()) {//允许交互
                        switch (minecart.getDisplayBlockData().getMaterial()) {
                            case CRAFTING_TABLE -> player.openWorkbench(null, true);
                            case GRINDSTONE -> player.openGrindstone(null, true);
                            case LOOM -> player.openLoom(null, true);
                            case CARTOGRAPHY_TABLE -> player.openCartographyTable(null, true);
                            case SMITHING_TABLE -> player.openSmithingTable(null, true);
                            case STONECUTTER -> player.openStonecutter(null, true);
                        }
                        System.out.println(minecart.getDisplayBlockData().getMaterial().getKey().getKey());
                        if(minecart.getDisplayBlockData().getMaterial().getKey().getKey().endsWith("_shulker_box")){
                            //Unfinished!
//                            System.out.println(1);
//                            NBTEntity entity=new NBTEntity(minecart);
//                            Inventory inv=box.getInventory();
//                            System.out.println(inv.getHolder());
//                            player.openInventory(inv.getHolder().getInventory());
                        }
                    }
                    return;
                }

                //取下物体 处理部分
                if (!minecart.getDisplayBlockData().getMaterial().isAir()) {
                    NBTEntity entity = new NBTEntity(minecart);
                    ItemStack is = entity.getPersistentDataContainer().getItemStack("BlockInfo");
                    if (item.getType().isAir()) {//手上为空 取下物体
                        player.getInventory().setItemInMainHand(is);
                        minecart.setDisplayBlockData(Material.AIR.createBlockData());
                        return;
                    }
                    if (is != null && item.asOne().equals(is.asOne())) {//手上为车上物体 取下物体
                        item.add();
                        minecart.setDisplayBlockData(Material.AIR.createBlockData());
                        event.setCancelled(true);
                    }
                    redstoneMinecartSet.remove(minecart.getUniqueId());
                }//放上物体 处理部分
                else {
                    if (item.getType().isBlock()) {//是方块 可以被放置
                        Material material = item.getType();
                        ItemStack copyItem = item.asOne().clone();
                        item.add(-1);
                        for (Map.Entry<Material, EntityType> entry : changeMap.entrySet()) {
                            if (entry.getKey() == material) {
                                Entity entity = minecart.getWorld().spawnEntity(minecart.getLocation(), entry.getValue());
                                entity.setVelocity(minecart.getVelocity());
                                entity.setRotation(minecart.getLocation().getYaw(), minecart.getLocation().getPitch());
                                minecart.remove();
                                return;
                            }
                        }
                        NBTEntity entity = new NBTEntity(minecart);
                        entity.getPersistentDataContainer().setItemStack("BlockInfo", copyItem);
                        minecart.setDisplayBlockData(material.createBlockData());
                        if(material.equals(Material.REDSTONE_BLOCK)){
                            redstoneMinecartSet.add(minecart.getUniqueId());
                        }
//                        if(material.createBlockData() instanceof Lightable){
//                            minecart.
//                        }
                        event.setCancelled(true);
                    }
                }

            }
        }

    }
}
