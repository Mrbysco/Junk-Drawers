package com.mrbysco.junkdrawers.client.screen;

import com.mrbysco.junkdrawers.menu.DrawerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class DrawerScreen extends AbstractContainerScreen<DrawerMenu> {
	private static final Identifier CONTAINER_BACKGROUND = Identifier.withDefaultNamespace("textures/gui/container/generic_54.png");
	private final int containerRows;

	public DrawerScreen(DrawerMenu drawerMenu, Inventory inventory, Component component) {
		super(drawerMenu, inventory, component);
		int i = 222;
		int j = 114;
		this.containerRows = 2;
		this.imageHeight = j + this.containerRows * 18;
		this.imageWidth = i;
		this.inventoryLabelY = this.imageHeight - 94;
	}

	@Override
	public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		this.renderTooltip(guiGraphics, mouseX, mouseY);
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
		int i = (this.width - this.imageWidth) / 2;
		int j = (this.height - this.imageHeight) / 2;
		guiGraphics.blit(RenderPipelines.GUI_TEXTURED, CONTAINER_BACKGROUND, i, j,
				0.0F, 0.0F, this.imageWidth, this.containerRows * 18 + 17,
				256, 256
		);
		guiGraphics.blit(RenderPipelines.GUI_TEXTURED, CONTAINER_BACKGROUND, i, j + this.containerRows * 18 + 17,
				0.0F, 126.0F, this.imageWidth, 96,
				256, 256
		);
	}
}