package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.world.level.block.Block;

public class BlockItemAMRender extends AMBlockItem {

    public BlockItemAMRender(Block blockSupplier, Properties props) {
        super(blockSupplier, props);
    }

    public void initializeClient(java.util.function.Consumer<Object> consumer) {
        consumer.accept(AlexsMobs.PROXY.getISTERProperties());
    }
}
