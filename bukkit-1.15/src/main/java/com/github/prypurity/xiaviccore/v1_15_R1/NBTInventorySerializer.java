package com.github.prypurity.xiaviccore.v1_15_R1;

import com.github.prypurity.xiaviccore.Utils.inventory.InventorySerializer;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftInventoryPlayer;
import org.bukkit.entity.Player;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Represents an inventory serializer which uses NBT as the storage format.
 */
public enum  NBTInventorySerializer implements InventorySerializer {

    INSTANCE;

    /**
     * Serialize an inventory into binary NBT.
     *
     * @param playerInventory The player inventory to serialize.
     * @return Returns a byte array consisting of the binary representation
     * of the inventory NBT.
     */
    @Override public byte[] serialize(
        final org.bukkit.inventory.PlayerInventory playerInventory) {
        final PlayerInventory inventory = ((CraftInventoryPlayer) playerInventory).getInventory();
        final NBTTagCompound compound = new NBTTagCompound();
        compound.set("Data", inventory.a(new NBTTagList()));
        final ByteArrayOutputStream os = new ByteArrayOutputStream(4096);
        try {
            NBTCompressedStreamTools.a(compound, os);
        } catch (final IOException ex) {
            throw new IllegalStateException("Unable to serialise Inventory NBT", ex);
        }
        return os.toByteArray();
    }

    /**
     * Apply the deserialized NBT onto a player. This method mutates the player's inventory!
     *
     * @param bytes  The byte array (binary) representation of a player's inventory in NBT {@link #serialize(org.bukkit.inventory.PlayerInventory)}
     * @param player The {@link Player} object to apply onto.
     */
    @Override public void applyInventoryOnto(final Player player, final byte[] bytes) {
        final NBTTagCompound compound;
        try {
            compound = NBTCompressedStreamTools.a(new ByteArrayInputStream(bytes));
        } catch (final IOException ex) {
            throw new IllegalStateException("Unable to apply Inventory to player from NBT", ex);
        }
        final CraftPlayer craftPlayer = (CraftPlayer) player;
        final EntityPlayer entityPlayer = craftPlayer.getHandle();
        final PlayerInventory inventory = entityPlayer.inventory;
        //Apply serial inventory onto a player.
        inventory.b(compound.getList("Data", 0)); //Nest = 0 since we save at nest 0.
    }

}
