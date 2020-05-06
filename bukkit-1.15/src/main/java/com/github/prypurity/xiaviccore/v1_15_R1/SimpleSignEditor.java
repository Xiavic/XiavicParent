package com.github.prypurity.xiaviccore.v1_15_R1;

import com.github.prypurity.xiaviccore.Utils.signedit.AbstractSignEditor;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.v1_15_R1.block.CraftBlockEntityState;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

public class SimpleSignEditor extends AbstractSignEditor {

    @Override public void openUI(final @NotNull Player player, final @NotNull Sign sign) {
        final EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        final PlayerConnection connection = entityPlayer.playerConnection;
        final TileEntitySign tileSign;
        try {
            final Field tileField = CraftBlockEntityState.class.getDeclaredField("tileEntity");
            //For 1.15 mappings - this is now under CraftBlockTileEntity<TileEntitySign>
            tileField.setAccessible(true);
            // Gets the tileEntity sign for later use
            tileSign = (TileEntitySign) tileField.get(sign);
        } catch (final ReflectiveOperationException ex) {
            throw new IllegalStateException("Unable to open sign!", ex);
        }
        tileSign.isEditable = true;
        // Makes the TileEntity sign editable so when we close the ui it
        // doesn't undo the changes
        tileSign.a(entityPlayer);
        // This field is the handle of who's editing it so we set it as the
        // craft player handle
        final BlockPosition position = BlockPosition.PooledBlockPosition
            .d((double) sign.getX(), sign.getY(),
                sign.getZ()); //Cast to double because of method overrides.
        // Gets the "PooledPosition" of the sign for the packet editor
        final PacketPlayOutOpenSignEditor packet = new PacketPlayOutOpenSignEditor(position);
        connection.sendPacket(packet);
        // This sends the packet to the playerconnection so the player
        // "opens" the sign
    }
}
