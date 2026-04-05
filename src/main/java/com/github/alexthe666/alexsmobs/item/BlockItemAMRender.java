package com.github.alexthe666.alexsmobs.item;

import net.minecraft.world.level.block.Block;

/** Block item with custom renderer; renderer registered in client init via BuiltinItemRendererRegistry. */
public class BlockItemAMRender extends AMBlockItem {

    public BlockItemAMRender(Block block, Properties props) {
        super(block, props);
    }
}
