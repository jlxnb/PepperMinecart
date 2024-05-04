package org.eu.pcraft.pepperminecart;

import de.tr7zw.changeme.nbtapi.NBTEntity;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.AnaloguePowerable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.event.vehicle.VehicleUpdateEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Lever;

import java.util.Map;
import java.util.Objects;

import static org.eu.pcraft.pepperminecart.PepperMinecart.changeMap;
import static org.eu.pcraft.pepperminecart.PepperMinecart.config;


public class listener implements Listener {
    @EventHandler
    void onMove(VehicleUpdateEvent event){//Unfinished!
        Minecart minecart=(Minecart) event.getVehicle();
        if(minecart.getDisplayBlockData().getMaterial()==Material.REDSTONE_BLOCK){
            Block block=minecart.getWorld().getBlockAt(minecart.getLocation());
            for(BlockFace face:BlockFace.values()){
                if(block.getRelative(face).getBlockData() instanceof AnaloguePowerable){
                    AnaloguePowerable x=(AnaloguePowerable) block.getRelative(face).getBlockData();
                    x.setPower(15);
                    block.getRelative(face).setBlockData(x.clone());
                }
            }
        }
    }

    @EventHandler
    void onDestory(VehicleDestroyEvent event) {
        Entity minecart = event.getVehicle();
        NBTEntity entity = new NBTEntity(minecart);
        ItemStack item = entity.getPersistentDataContainer().getItemStack("BlockInfo");
        minecart.getWorld().dropItem(minecart.getLocation(), item);
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
                    if (config.enableCustomInteract) {//允许交互
                        switch (minecart.getDisplayBlockData().getMaterial()) {
                            case CRAFTING_TABLE -> player.openWorkbench(null, true);
                            case GRINDSTONE -> player.openGrindstone(null, true);
                            case LOOM -> player.openLoom(null, true);
                            case CARTOGRAPHY_TABLE -> player.openCartographyTable(null, true);
                            case SMITHING_TABLE -> player.openSmithingTable(null, true);
                            case STONECUTTER -> player.openStonecutter(null, true);
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
                    if (Objects.equals(item.getItemMeta(), is.getItemMeta())) {//手上为车上物体 取下物体
                        item.add();
                        minecart.setDisplayBlockData(Material.AIR.createBlockData());
                        event.setCancelled(true);
                        return;
                    }
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
//                        if(material.createBlockData() instanceof Lightable){
//                            minecart.
//                        }
                        event.setCancelled(true);
                        return;
                    }
                }

            }
        }

    }
}
