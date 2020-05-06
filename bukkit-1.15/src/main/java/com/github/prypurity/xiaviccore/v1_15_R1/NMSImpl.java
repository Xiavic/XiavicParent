package com.github.prypurity.xiaviccore.v1_15_R1;


import com.github.prypurity.xiaviccore.Utils.signedit.ISignEditor;
import com.github.prypurity.xiaviccore.Utils.NMSHandler.NMS;
import com.github.prypurity.xiaviccore.Utils.inventory.InventorySerializer;

public class NMSImpl implements NMS {

    public NMSImpl() {}

    private final ISignEditor signEditor = new SimpleSignEditor();

    @Override public ISignEditor getSignEditor() {
        return signEditor;
    }

    @Override public InventorySerializer getInventorySerializer() {
        return NBTInventorySerializer.INSTANCE;
    }
}
