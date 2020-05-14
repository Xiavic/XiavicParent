package com.github.xiavic.lib.NMSHandler;


import com.github.xiavic.lib.inventory.InventorySerializer;
import com.github.xiavic.lib.signedit.ISignEditor;

public interface NMS {

    InventorySerializer getInventorySerializer();

    ISignEditor getSignEditor();

}
