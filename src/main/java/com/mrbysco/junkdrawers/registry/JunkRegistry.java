package com.mrbysco.junkdrawers.registry;

import com.mrbysco.junkdrawers.JunkDrawers;
import com.mrbysco.junkdrawers.block.DrawerBlock;
import com.mrbysco.junkdrawers.block.blockentity.DrawerBlockEntity;
import com.mrbysco.junkdrawers.menu.DrawerMenu;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class JunkRegistry {
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, JunkDrawers.MOD_ID);
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, JunkDrawers.MOD_ID);
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, JunkDrawers.MOD_ID);
	public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, JunkDrawers.MOD_ID);
	public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.CONTAINERS, JunkDrawers.MOD_ID);

	public static final RegistryObject<MenuType<DrawerMenu>> DRAWER_MENU = MENU_TYPES.register("drawer", () ->
			IForgeMenuType.create(DrawerMenu::new));

	public static final RegistryObject<SoundEvent> DRAWER_OPEN = SOUND_EVENTS.register("drawer.open", () ->
			new SoundEvent(new ResourceLocation(JunkDrawers.MOD_ID, "drawer.open")));
	public static final RegistryObject<SoundEvent> DRAWER_CLOSE = SOUND_EVENTS.register("drawer.close", () ->
			new SoundEvent(new ResourceLocation(JunkDrawers.MOD_ID, "drawer.close")));
	public static final RegistryObject<SoundEvent> DRAWER_JAMMED = SOUND_EVENTS.register("drawer.jammed", () ->
			new SoundEvent(new ResourceLocation(JunkDrawers.MOD_ID, "drawer.jammed")));

	public static final RegistryObject<Block> DRAWER = BLOCKS.register("drawer", () -> new DrawerBlock(Block.Properties.of(Material.WOOD)
			.strength(2.5F).sound(SoundType.WOOD).noOcclusion()));
	public static final RegistryObject<Item> DRAWER_ITEM = ITEMS.register("drawer", () -> new BlockItem(DRAWER.get(), new Item.Properties().tab(CreativeModeTab.TAB_REDSTONE)));

	public static final RegistryObject<BlockEntityType<DrawerBlockEntity>> DRAWER_BLOCK_ENTITY = BLOCK_ENTITIES.register("drawer", () ->
			BlockEntityType.Builder.of(DrawerBlockEntity::new, JunkRegistry.DRAWER.get()).build(null));


}
