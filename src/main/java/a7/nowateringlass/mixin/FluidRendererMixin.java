package a7.nowateringlass.mixin;

import a7.nowateringlass.Config;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.FluidRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.AbstractGlassBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import static a7.nowateringlass.NoWaterInGlass.BLOCKS_TO_OCCLUDE;

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
            boolean occluded = Shapes.blockOccudes(threshold, shape, dir);

            if (!occluded)
                return;

            if (Config.FIX_ONLY.get()) {
                occluded = false;
                ResourceLocation id = ForgeRegistries.BLOCKS.getKey(state.getBlock());
                for (var id2 : BLOCKS_TO_OCCLUDE) {
//                    NoWaterInGlass.LOGGER.info("checking at {},{},{} {} against {}", x, y, z, id, id2);
                    if (id2.equals(id)) {
                        occluded = true;
                        break;
                    }
                }
            }

            if (occluded) {
                cir.setReturnValue(false);
            }
        }
    }
}
