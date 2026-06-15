package com.github.alexthe666.alexsmobs.client.render;

import com.mojang.blaze3d.vertex.PoseStack;

/**
 * Helpers for Minecraft 26.1 deferred {@code submitCustomGeometry} callbacks. The live {@link PoseStack}
 * passed to {@code submit} is often popped before queued geometry replays; always render from {@code pose}.
 */
public final class DeferredPoseStacks {
    private DeferredPoseStacks() {
    }

    public static PoseStack fromCaptured(PoseStack.Pose pose) {
        PoseStack copy = new PoseStack();
        copy.last().set(pose);
        return copy;
    }
}
