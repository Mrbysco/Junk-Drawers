package com.mrbysco.junkdrawers;

import com.mojang.logging.LogUtils;
import com.mrbysco.junkdrawers.client.ClientHandler;
import com.mrbysco.junkdrawers.config.JunkConfig;
import com.mrbysco.junkdrawers.registry.JunkRegistry;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
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
		JunkRegistry.CREATIVE_MODE_TABS.register(eventBus);
		JunkRegistry.BLOCK_ENTITIES.register(eventBus);
		JunkRegistry.SOUND_EVENTS.register(eventBus);
		JunkRegistry.MENU_TYPES.register(eventBus);

		eventBus.addListener(this::buildCreativeContents);

		if (FMLEnvironment.dist.isClient()) {
			eventBus.addListener(ClientHandler::onClientSetup);
		}
	}

	private void buildCreativeContents(BuildCreativeModeTabContentsEvent event) {
		if (event.getTabKey() == CreativeModeTabs.REDSTONE_BLOCKS) {
			for (DeferredHolder<Item, ? extends Item> itemRegistryObject : JunkRegistry.ITEMS.getEntries()) {
				event.accept(itemRegistryObject.get());
			}
		}
	}
}
