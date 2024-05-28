package com.mrbysco.junkdrawers.client;

import com.mrbysco.junkdrawers.client.screen.DrawerScreen;
import com.mrbysco.junkdrawers.registry.JunkRegistry;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public class ClientHandler {
	public static void onMenuRegister(RegisterMenuScreensEvent event) {
		event.register(JunkRegistry.DRAWER_MENU.get(), DrawerScreen::new);
	}
}
