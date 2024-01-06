package com.mrbysco.junkdrawers.registry;

import com.mrbysco.junkdrawers.JunkDrawers;
import com.mrbysco.junkdrawers.block.DrawerBlock;
import com.mrbysco.junkdrawers.block.blockentity.DrawerBlockEntity;
import com.mrbysco.junkdrawers.menu.DrawerMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.function.Supplier;

public class JunkRegistry {
	public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(JunkDrawers.MOD_ID);
	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(JunkDrawers.MOD_ID);
	public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, JunkDrawers.MOD_ID);
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, JunkDrawers.MOD_ID);
	public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(Registries.SOUND_EVENT, JunkDrawers.MOD_ID);
	public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(Registries.MENU, JunkDrawers.MOD_ID);

	public static final Supplier<MenuType<DrawerMenu>> DRAWER_MENU = MENU_TYPES.register("drawer", () ->
			IMenuTypeExtension.create(DrawerMenu::new));

	public static final DeferredHolder<SoundEvent, SoundEvent> DRAWER_OPEN = SOUND_EVENTS.register("drawer.open", () ->
			SoundEvent.createVariableRangeEvent(new ResourceLocation(JunkDrawers.MOD_ID, "drawer.open")));
	public static final DeferredHolder<SoundEvent, SoundEvent> DRAWER_CLOSE = SOUND_EVENTS.register("drawer.close", () ->
			SoundEvent.createVariableRangeEvent(new ResourceLocation(JunkDrawers.MOD_ID, "drawer.close")));
	public static final DeferredHolder<SoundEvent, SoundEvent> DRAWER_JAMMED = SOUND_EVENTS.register("drawer.jammed", () ->
			SoundEvent.createVariableRangeEvent(new ResourceLocation(JunkDrawers.MOD_ID, "drawer.jammed")));

	public static final DeferredBlock<DrawerBlock> OAK_DRAWER = createDrawer("drawer", () -> new DrawerBlock(Block.Properties.ofFullCopy(Blocks.OAK_PLANKS).strength(2.5F).sound(SoundType.WOOD).noOcclusion()));
	public static final DeferredBlock<DrawerBlock> SPRUCE_DRAWER = createDrawer("spruce_drawer", () -> new DrawerBlock(Block.Properties.ofFullCopy(Blocks.SPRUCE_PLANKS).strength(2.5F).sound(SoundType.WOOD).noOcclusion()));
	public static final DeferredBlock<DrawerBlock> BIRCH_DRAWER = createDrawer("birch_drawer", () -> new DrawerBlock(Block.Properties.ofFullCopy(Blocks.BIRCH_PLANKS).strength(2.5F).sound(SoundType.WOOD).noOcclusion()));
	public static final DeferredBlock<DrawerBlock> JUNGLE_DRAWER = createDrawer("jungle_drawer", () -> new DrawerBlock(Block.Properties.ofFullCopy(Blocks.JUNGLE_PLANKS).strength(2.5F).sound(SoundType.WOOD).noOcclusion()));
	public static final DeferredBlock<DrawerBlock> ACACIA_DRAWER = createDrawer("acacia_drawer", () -> new DrawerBlock(Block.Properties.ofFullCopy(Blocks.ACACIA_PLANKS).strength(2.5F).sound(SoundType.WOOD).noOcclusion()));
	public static final DeferredBlock<DrawerBlock> CHERRY_DRAWER = createDrawer("cherry_drawer", () -> new DrawerBlock(Block.Properties.ofFullCopy(Blocks.CHERRY_PLANKS).strength(2.5F).sound(SoundType.WOOD).noOcclusion()));
	public static final DeferredBlock<DrawerBlock> DARK_OAK_DRAWER = createDrawer("dark_oak_drawer", () -> new DrawerBlock(Block.Properties.ofFullCopy(Blocks.DARK_OAK_PLANKS).strength(2.5F).sound(SoundType.WOOD).noOcclusion()));
	public static final DeferredBlock<DrawerBlock> MANGROVE_DRAWER = createDrawer("mangrove_drawer", () -> new DrawerBlock(Block.Properties.ofFullCopy(Blocks.MANGROVE_PLANKS).strength(2.5F).sound(SoundType.WOOD).noOcclusion()));
	public static final DeferredBlock<DrawerBlock> BAMBOO_DRAWER = createDrawer("bamboo_drawer", () -> new DrawerBlock(Block.Properties.ofFullCopy(Blocks.BAMBOO_PLANKS).strength(2.5F).sound(SoundType.WOOD).noOcclusion()));

	public static final DeferredBlock<DrawerBlock> CRIMSON_DRAWER = createDrawer("crimson_drawer", () -> new DrawerBlock(Block.Properties.ofFullCopy(Blocks.CRIMSON_PLANKS).strength(2.5F).sound(SoundType.NETHER_WOOD).noOcclusion()));
	public static final DeferredBlock<DrawerBlock> WARPED_DRAWER = createDrawer("warped_drawer", () -> new DrawerBlock(Block.Properties.ofFullCopy(Blocks.WARPED_PLANKS).strength(2.5F).sound(SoundType.NETHER_WOOD).noOcclusion()));

	public static <T extends Block> DeferredBlock<T> createDrawer(String name, Supplier<T> blockSupplier) {
		DeferredBlock<T> drawer = BLOCKS.register(name, blockSupplier);
		ITEMS.registerSimpleBlockItem(name, drawer);
		return drawer;
	}

	public static final Supplier<CreativeModeTab> DRAWER_TAB = CREATIVE_MODE_TABS.register("tab", () -> CreativeModeTab.builder()
			.icon(() -> JunkRegistry.OAK_DRAWER.get().asItem().getDefaultInstance())
			.withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
			.title(Component.translatable("itemGroup.junkdrawers.tab"))
			.displayItems((parameters, output) -> {
				List<ItemStack> stacks = JunkRegistry.ITEMS.getEntries().stream().map(reg -> new ItemStack(reg.get())).toList();
				output.acceptAll(stacks);
			}).build());

	public static final Supplier<BlockEntityType<DrawerBlockEntity>> DRAWER_BLOCK_ENTITY = BLOCK_ENTITIES.register("drawer", () ->
			BlockEntityType.Builder.of(DrawerBlockEntity::new,
					JunkRegistry.OAK_DRAWER.get(), JunkRegistry.SPRUCE_DRAWER.get(), JunkRegistry.BIRCH_DRAWER.get(),
					JunkRegistry.JUNGLE_DRAWER.get(), JunkRegistry.ACACIA_DRAWER.get(), JunkRegistry.CHERRY_DRAWER.get(),
					JunkRegistry.DARK_OAK_DRAWER.get(), JunkRegistry.MANGROVE_DRAWER.get(), JunkRegistry.BAMBOO_DRAWER.get(),
					JunkRegistry.CRIMSON_DRAWER.get(), JunkRegistry.WARPED_DRAWER.get()).build(null));


	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, DRAWER_BLOCK_ENTITY.get(), DrawerBlockEntity::getHandler);
	}

}
