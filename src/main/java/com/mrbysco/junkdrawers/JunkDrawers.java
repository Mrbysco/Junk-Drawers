package com.mrbysco.junkdrawers;

import com.mojang.logging.LogUtils;
import com.mrbysco.junkdrawers.client.ClientHandler;
import com.mrbysco.junkdrawers.config.JunkConfig;
import com.mrbysco.junkdrawers.registry.JunkRegistry;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(JunkDrawers.MOD_ID)
public class JunkDrawers {
	public static final String MOD_ID = "junkdrawers";
	public static final Logger LOGGER = LogUtils.getLogger();

	public JunkDrawers() {
		IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, JunkConfig.commonSpec);
		eventBus.register(JunkConfig.class);

		JunkRegistry.BLOCKS.register(eventBus);
		JunkRegistry.ITEMS.register(eventBus);
		JunkRegistry.BLOCK_ENTITIES.register(eventBus);
		JunkRegistry.SOUND_EVENTS.register(eventBus);
		JunkRegistry.MENU_TYPES.register(eventBus);

		eventBus.addListener(this::buildCreativeContents);

		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
			eventBus.addListener(ClientHandler::onClientSetup);
		});
	}

	private void buildCreativeContents(BuildCreativeModeTabContentsEvent event) {
		if (event.getTabKey() == CreativeModeTabs.REDSTONE_BLOCKS) {
			event.accept(new ItemStack(JunkRegistry.DRAWER_ITEM.get()));
		}
	}
}
