package org.eu.pcraft.pepperminecart;

import de.tr7zw.changeme.nbtapi.NBT;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

import static org.eu.pcraft.pepperminecart.PepperMinecart.changeMap;


public class Listener implements org.bukkit.event.Listener {
    @EventHandler
    void onDestroy(VehicleDestroyEvent event) {
        Entity minecart = event.getVehicle();
        ItemStack item = NBT.getPersistentData(minecart, nbt ->
                nbt.getItemStack("BlockInfo"));
        if (item != null)
            minecart.getWorld().dropItem(minecart.getLocation(), item);
    }

    @EventHandler
    void onCloseInv(InventoryCloseEvent event) {
        //if(event.getInventory().getHolder()==)
    }

    @EventHandler
    void onInteract(PlayerInteractEntityEvent event) {
        if (event.getHand() == EquipmentSlot.HAND) {
            if (event.getRightClicked().getType() == EntityType.MINECART) {
                Minecart minecart = (Minecart) event.getRightClicked();
                Player player = event.getPlayer();
                ItemStack item = player.getInventory().getItemInMainHand();

                if (!player.isSneaking()) {
                    //交互
                    if (PepperMinecart.getInstance().getConfigTemplate().isEnableCustomInteract()) {//允许交互
                        switch (minecart.getDisplayBlockData().getMaterial()) {
                            case CRAFTING_TABLE -> player.openWorkbench(null, true);
                            case GRINDSTONE -> player.openGrindstone(null, true);
                            case LOOM -> player.openLoom(null, true);
                            case CARTOGRAPHY_TABLE -> player.openCartographyTable(null, true);
                            case SMITHING_TABLE -> player.openSmithingTable(null, true);
                            case STONECUTTER -> player.openStonecutter(null, true);
                        }
                        if (minecart.getDisplayBlockData().getMaterial().getKey().getKey().endsWith("_shulker_box")) {
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
                ItemStack is = NBT.getPersistentData(minecart, nbt ->
                        nbt.getItemStack("BlockInfo"));
                if (!minecart.getDisplayBlockData().getMaterial().isAir()) {
                    //取下物体 处理部分
                    if (item.getType().isAir()) {
                        //手上为空 取下物体
                        player.getInventory().setItemInMainHand(is);
                        NBT.modify(minecart, nbt -> {
                            nbt.removeKey("BlockInfo");
                        });
                        minecart.setDisplayBlockData(Material.AIR.createBlockData());
                    }
                    if (item.asOne().equals(is) && item.getAmount() != item.getMaxStackSize()) {
                        //手上为车上物体 取下物体
                        item.add();
                        NBT.modify(minecart, nbt -> {
                            nbt.removeKey("BlockInfo");
                        });
                        minecart.setDisplayBlockData(Material.AIR.createBlockData());
                        event.setCancelled(true);
                    }
                } else if (item.getType().isBlock()) {
                    //是方块 可以被放置
                    Material material = item.getType();
                    ItemStack copyItem = item.asOne().clone();
                    item.subtract(1);
                    for (Map.Entry<Material, EntityType> entry : changeMap.entrySet()) {
                        if (entry.getKey() == material) {
                            Entity entity = minecart.getWorld().spawnEntity(minecart.getLocation(), entry.getValue());
                            entity.setVelocity(minecart.getVelocity());
                            entity.setRotation(minecart.getLocation().getYaw(), minecart.getLocation().getPitch());
                            minecart.remove();
                            return;
                        }
                    }
                    NBT.modifyPersistentData(minecart, nbt -> {
                        nbt.setItemStack("BlockInfo", copyItem);
                    });
                    minecart.setDisplayBlockData(material.createBlockData());
                    event.setCancelled(true);
                }
            }
        }
    }
}
