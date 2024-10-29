package a7.nowateringlass;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.logging.LogUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(NoWaterInGlass.MODID)
public class NoWaterInGlass {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "nowateringlass";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public NoWaterInGlass() {
        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ClientForgeEvents {
        @SubscribeEvent
        public static void onRegisterClientCommands(RegisterClientCommandsEvent event) {
            CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
            dispatcher.register(Commands.literal("nowateringlass")
                    .executes(ctx -> {
                        if (ctx.getSource().getEntity() instanceof Player player) {
                            Config.ENABLED.set(!Config.ENABLED.get());
                            Config.SPEC.save();
                            player.sendSystemMessage(
                                    Component.literal("NoWaterInGlass: Toggled %s (rebuild chunks to update)"
                                            .formatted(Config.ENABLED.get() ? "on" : "off")));
                        }
                        return Command.SINGLE_SUCCESS;
                    }));
        }
    }
}
