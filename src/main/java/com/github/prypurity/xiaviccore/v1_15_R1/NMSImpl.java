package com.github.prypurity.xiaviccore.v1_15_R1;

import com.github.prypurity.xiaviccore.NMSHandler.NMS;
import com.github.prypurity.xiaviccore.Utils.inventory.InventorySerializer;
import com.github.prypurity.xiaviccore.Utils.signeditor.ISignEditor;

public class NMSImpl implements NMS {

    public NMSImpl() {

    }

    @Override public ISignEditor getSignEditor() {
        throw new UnsupportedOperationException();
    }

    @Override public InventorySerializer getInventorySerializer() {
        return NBTInventorySerializer.INSTANCE;
    }
}
