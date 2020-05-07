package com.github.prypurity.xiaviccore.Utils.NMSHandler;

import com.github.prypurity.xiaviccore.Utils.signedit.ISignEditor;
import com.github.prypurity.xiaviccore.Utils.inventory.InventorySerializer;

public interface NMS {

    InventorySerializer getInventorySerializer();

    ISignEditor getSignEditor();

}
