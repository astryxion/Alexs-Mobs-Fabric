package com.github.alexthe666.citadel.mixin;

import net.minecraft.world.level.pathfinder.NodeEvaluator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Accessor for NodeEvaluator entity dimensions. Used by AdvancedPathNavigate to set
 * entity width/height/depth for pathfinding. Mixin rewrites at runtime (intermediary);
 * no reflection or literal field names in production.
 */
@Mixin(NodeEvaluator.class)
public interface NodeEvaluatorAccessor {

    @Accessor("entityWidth")
    void citadel$setEntityWidth(int width);

    @Accessor("entityHeight")
    void citadel$setEntityHeight(int height);

    @Accessor("entityDepth")
    void citadel$setEntityDepth(int depth);
}
