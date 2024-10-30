package a7.nowateringlass;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.logging.LogUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import org.slf4j.Logger;

import java.util.Arrays;


// The value here should match an entry in the META-INF/mods.toml file
@Mod(NoWaterInGlass.MODID)
public class NoWaterInGlass {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "nowateringlass";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    public static ResourceLocation[] BLOCKS_TO_OCCLUDE = {};

    public NoWaterInGlass() {
        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private static Command<CommandSourceStack> handleToggleCommand(ForgeConfigSpec.BooleanValue booleanValue, String displayName) {
        return ctx -> {
            if (ctx.getSource().getEntity() instanceof Player player) {
                booleanValue.set(!booleanValue.get());
                Config.SPEC.save();
                player.sendSystemMessage(
                        Component.literal("NoWaterInGlass: Toggled %s%s".formatted(displayName, "".equals(displayName) ? "" : " "))
                                .append(toOnOff(booleanValue.get()))
                );
                Minecraft.getInstance().levelRenderer.allChanged();
            }
            return Command.SINGLE_SUCCESS;
        };
    }

    public static MutableComponent toOnOff(boolean b) {
        return Component.literal(b ? "on" : "off")
                .withStyle(b ? ChatFormatting.GREEN : ChatFormatting.RED)
                .withStyle(ChatFormatting.BOLD);
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
    public static class ClientForgeEvents {
        @SubscribeEvent
        public static void onRegisterClientCommands(RegisterClientCommandsEvent event) {
            CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
            dispatcher.register(Commands.literal("nowateringlass")
                    .executes(handleToggleCommand(Config.ENABLED, ""))
                    .then(Commands.literal("fixonly")
                            .executes(handleToggleCommand(Config.FIX_ONLY, "fix only")))
            );
        }
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onFMLLoadComplete(FMLLoadCompleteEvent event) {
            ResourceLocation[] blocks = new ResourceLocation[]{
                    new ResourceLocation("alexscaves", "depth_glass"),
                    new ResourceLocation("bettas", "tank"),
                    new ResourceLocation("fintastic", "aquarium_glass"),
                    new ResourceLocation("fintastic", "infernal_aquarium_glass"),
                    new ResourceLocation("fintastic", "radon_aquarium_glass"),
                    new ResourceLocation("fintastic", "sugar_aquarium_glass"),
                    new ResourceLocation("fintastic", "aquarium_glass_pane"),
                    new ResourceLocation("fintastic", "infernal_aquarium_glass_pane"),
                    new ResourceLocation("fintastic", "radon_aquarium_glass_pane"),
                    new ResourceLocation("fintastic", "sugar_aquarium_glass_pane"),
            };
            BLOCKS_TO_OCCLUDE = Arrays.stream(blocks)
                    .filter(id -> ModList.get().isLoaded(id.getNamespace()))
                    .toArray(ResourceLocation[]::new);
        }
    }
}
