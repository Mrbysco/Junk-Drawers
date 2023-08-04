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
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.function.Supplier;

public class JunkRegistry {
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, JunkDrawers.MOD_ID);
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, JunkDrawers.MOD_ID);
	public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, JunkDrawers.MOD_ID);
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, JunkDrawers.MOD_ID);
	public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, JunkDrawers.MOD_ID);
	public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, JunkDrawers.MOD_ID);

	public static final RegistryObject<MenuType<DrawerMenu>> DRAWER_MENU = MENU_TYPES.register("drawer", () ->
			IForgeMenuType.create(DrawerMenu::new));

	public static final RegistryObject<SoundEvent> DRAWER_OPEN = SOUND_EVENTS.register("drawer.open", () ->
			SoundEvent.createVariableRangeEvent(new ResourceLocation(JunkDrawers.MOD_ID, "drawer.open")));
	public static final RegistryObject<SoundEvent> DRAWER_CLOSE = SOUND_EVENTS.register("drawer.close", () ->
			SoundEvent.createVariableRangeEvent(new ResourceLocation(JunkDrawers.MOD_ID, "drawer.close")));
	public static final RegistryObject<SoundEvent> DRAWER_JAMMED = SOUND_EVENTS.register("drawer.jammed", () ->
			SoundEvent.createVariableRangeEvent(new ResourceLocation(JunkDrawers.MOD_ID, "drawer.jammed")));

	public static final RegistryObject<Block> OAK_DRAWER = createDrawer("drawer", () -> new DrawerBlock(Block.Properties.copy(Blocks.OAK_PLANKS).strength(2.5F).sound(SoundType.WOOD).noOcclusion()));
	public static final RegistryObject<Block> SPRUCE_DRAWER = createDrawer("spruce_drawer", () -> new DrawerBlock(Block.Properties.copy(Blocks.SPRUCE_PLANKS).strength(2.5F).sound(SoundType.WOOD).noOcclusion()));
	public static final RegistryObject<Block> BIRCH_DRAWER = createDrawer("birch_drawer", () -> new DrawerBlock(Block.Properties.copy(Blocks.BIRCH_PLANKS).strength(2.5F).sound(SoundType.WOOD).noOcclusion()));
	public static final RegistryObject<Block> JUNGLE_DRAWER = createDrawer("jungle_drawer", () -> new DrawerBlock(Block.Properties.copy(Blocks.JUNGLE_PLANKS).strength(2.5F).sound(SoundType.WOOD).noOcclusion()));
	public static final RegistryObject<Block> ACACIA_DRAWER = createDrawer("acacia_drawer", () -> new DrawerBlock(Block.Properties.copy(Blocks.ACACIA_PLANKS).strength(2.5F).sound(SoundType.WOOD).noOcclusion()));
	public static final RegistryObject<Block> CHERRY_DRAWER = createDrawer("cherry_drawer", () -> new DrawerBlock(Block.Properties.copy(Blocks.CHERRY_PLANKS).strength(2.5F).sound(SoundType.WOOD).noOcclusion()));
	public static final RegistryObject<Block> DARK_OAK_DRAWER = createDrawer("dark_oak_drawer", () -> new DrawerBlock(Block.Properties.copy(Blocks.DARK_OAK_PLANKS).strength(2.5F).sound(SoundType.WOOD).noOcclusion()));
	public static final RegistryObject<Block> MANGROVE_DRAWER = createDrawer("mangrove_drawer", () -> new DrawerBlock(Block.Properties.copy(Blocks.MANGROVE_PLANKS).strength(2.5F).sound(SoundType.WOOD).noOcclusion()));
	public static final RegistryObject<Block> BAMBOO_DRAWER = createDrawer("bamboo_drawer", () -> new DrawerBlock(Block.Properties.copy(Blocks.BAMBOO_PLANKS).strength(2.5F).sound(SoundType.WOOD).noOcclusion()));

	public static final RegistryObject<Block> CRIMSON_DRAWER = createDrawer("crimson_drawer", () -> new DrawerBlock(Block.Properties.copy(Blocks.CRIMSON_PLANKS).strength(2.5F).sound(SoundType.NETHER_WOOD).noOcclusion()));
	public static final RegistryObject<Block> WARPED_DRAWER = createDrawer("warped_drawer", () -> new DrawerBlock(Block.Properties.copy(Blocks.WARPED_PLANKS).strength(2.5F).sound(SoundType.NETHER_WOOD).noOcclusion()));

	public static final RegistryObject<Block> createDrawer(String name, Supplier<Block> blockSupplier) {
		RegistryObject<Block> drawer = BLOCKS.register(name, blockSupplier);
		ITEMS.register(name, () -> new BlockItem(drawer.get(), new Item.Properties()));
		return drawer;
	}

	public static final RegistryObject<CreativeModeTab> DRAWER_TAB = CREATIVE_MODE_TABS.register("tab", () -> CreativeModeTab.builder()
			.icon(() -> JunkRegistry.OAK_DRAWER.get().asItem().getDefaultInstance())
			.withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
			.title(Component.translatable("itemGroup.junkdrawers.tab"))
			.displayItems((parameters, output) -> {
				List<ItemStack> stacks = JunkRegistry.ITEMS.getEntries().stream().map(reg -> new ItemStack(reg.get())).toList();
				output.acceptAll(stacks);
			}).build());

	public static final RegistryObject<BlockEntityType<DrawerBlockEntity>> DRAWER_BLOCK_ENTITY = BLOCK_ENTITIES.register("drawer", () ->
			BlockEntityType.Builder.of(DrawerBlockEntity::new,
					JunkRegistry.OAK_DRAWER.get(), JunkRegistry.SPRUCE_DRAWER.get(), JunkRegistry.BIRCH_DRAWER.get(),
					JunkRegistry.JUNGLE_DRAWER.get(), JunkRegistry.ACACIA_DRAWER.get(), JunkRegistry.CHERRY_DRAWER.get(),
					JunkRegistry.DARK_OAK_DRAWER.get(), JunkRegistry.MANGROVE_DRAWER.get(), JunkRegistry.BAMBOO_DRAWER.get(),
					JunkRegistry.CRIMSON_DRAWER.get(), JunkRegistry.WARPED_DRAWER.get()).build(null));


}
