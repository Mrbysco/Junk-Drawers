package com.mrbysco.junkdrawers.client.screen;

import com.mrbysco.junkdrawers.menu.DrawerMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class DrawerScreen extends AbstractContainerScreen<DrawerMenu> {
	private static final Identifier CONTAINER_BACKGROUND = Identifier.withDefaultNamespace("textures/gui/container/generic_54.png");
	private final int containerRows;

	public DrawerScreen(DrawerMenu drawerMenu, Inventory inventory, Component component) {
		super(drawerMenu, inventory, component, 222, 114 + 2 * 18);
		this.containerRows = 2;
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		super.extractRenderState(graphics, mouseX, mouseY, a);
		this.extractTooltip(graphics, mouseX, mouseY);
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		int i = (this.width - this.imageWidth) / 2;
		int j = (this.height - this.imageHeight) / 2;
		graphics.blit(RenderPipelines.GUI_TEXTURED, CONTAINER_BACKGROUND, i, j,
				0.0F, 0.0F, this.imageWidth, this.containerRows * 18 + 17,
				256, 256
		);
		graphics.blit(RenderPipelines.GUI_TEXTURED, CONTAINER_BACKGROUND, i, j + this.containerRows * 18 + 17,
				0.0F, 126.0F, this.imageWidth, 96,
				256, 256
		);
	}
}