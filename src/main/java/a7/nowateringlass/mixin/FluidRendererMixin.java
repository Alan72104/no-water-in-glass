package a7.nowateringlass.mixin;

import a7.nowateringlass.Config;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.FluidRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.AbstractGlassBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(FluidRenderer.class)
public class FluidRendererMixin {
    @Inject(method = "isSideExposed", at = @At(value = "RETURN", ordinal = 3), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true, remap = false, require = 1)
    public void isSideExposed(BlockAndTintGetter world,
                              int x,
                              int y,
                              int z,
                              Direction dir,
                              float height,
                              CallbackInfoReturnable<Boolean> cir,
                              BlockPos pos,
                              BlockState state) {
        if (!Config.ENABLED.get())
            return;

        if (dir != Direction.UP && state.getBlock() instanceof AbstractGlassBlock) {
            VoxelShape shape = state.getOcclusionShape(world, pos);
            VoxelShape threshold = Shapes.box(0.0D, 0.0D, 0.0D, 1.0D, height, 1.0D);
            if (Shapes.blockOccudes(threshold, shape, dir)) {
                cir.setReturnValue(false);
//                NoWaterInGlass.LOGGER.info("block at %d %d %d to %s occludes".formatted(x, y, z, dir));
//                return;
            }
//            NoWaterInGlass.LOGGER.info("block at %d %d %d to %s doesn't occlude".formatted(x, y, z, dir));
        }
    }
}
