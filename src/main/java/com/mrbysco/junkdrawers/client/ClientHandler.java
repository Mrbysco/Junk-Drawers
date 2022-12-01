package com.mrbysco.junkdrawers.client;

import com.mrbysco.junkdrawers.client.screen.DrawerScreen;
import com.mrbysco.junkdrawers.registry.JunkRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientHandler {
	public static void onClientSetup(FMLClientSetupEvent event) {
		MenuScreens.register(JunkRegistry.DRAWER_MENU.get(), DrawerScreen::new);
	}
}
