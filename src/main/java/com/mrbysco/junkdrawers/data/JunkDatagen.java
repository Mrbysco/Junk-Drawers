package com.mrbysco.junkdrawers.data;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.mrbysco.junkdrawers.JunkDrawers;
import com.mrbysco.junkdrawers.registry.JunkRegistry;
import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.common.data.SoundDefinitionsProvider;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class JunkDatagen {

	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		ExistingFileHelper helper = event.getExistingFileHelper();

		if (event.includeServer()) {
			generator.addProvider(new JunkLoot(generator));
			generator.addProvider(new JunkRecipeProvider(generator));
		}
		if (event.includeClient()) {
			generator.addProvider(new JunkLanguageProvider(generator));
			generator.addProvider(new JunkSoundProvider(generator, helper));
			generator.addProvider(new JunkBlockstateProvider(generator, helper));
			generator.addProvider(new JunkItemModelProvider(generator, helper));
		}
	}

	private static class JunkLanguageProvider extends LanguageProvider {

		public JunkLanguageProvider(DataGenerator gen) {
			super(gen, JunkDrawers.MOD_ID, "en_us");
		}

		@Override
		protected void addTranslations() {
			addBlock(JunkRegistry.DRAWER, "Junk Drawer");

			add("junkdrawers.container.drawer", "Junk Drawer");

			add("junkdrawers.drawer.jammed", "The drawer jammed, please try again");

			addSubtitle(JunkRegistry.DRAWER_OPEN, "Drawer Opens");
			addSubtitle(JunkRegistry.DRAWER_CLOSE, "Drawer Closes");
			addSubtitle(JunkRegistry.DRAWER_JAMMED, "Drawer Jammed");
		}

		public void addSubtitle(RegistryObject<SoundEvent> sound, String name) {
			this.addSubtitle(sound.get(), name);
		}

		public void addSubtitle(SoundEvent sound, String name) {
			String path = JunkDrawers.MOD_ID + ".subtitle." + sound.getLocation().getPath();
			this.add(path, name);
		}
	}

	public static class JunkSoundProvider extends SoundDefinitionsProvider {

		public JunkSoundProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
			super(generator, JunkDrawers.MOD_ID, existingFileHelper);
		}

		@Override
		public void registerSounds() {
			this.add(JunkRegistry.DRAWER_OPEN, definition()
					.subtitle(modSubtitle(JunkRegistry.DRAWER_OPEN.getId()))
					.with(sound(modLoc("drawer_open"))));
			this.add(JunkRegistry.DRAWER_CLOSE, definition()
					.subtitle(modSubtitle(JunkRegistry.DRAWER_CLOSE.getId()))
					.with(sound(modLoc("drawer_close"))));
			this.add(JunkRegistry.DRAWER_JAMMED, definition()
					.subtitle(modSubtitle(JunkRegistry.DRAWER_JAMMED.getId()))
					.with(sound(modLoc("drawer_jammed"))));
		}


		public String modSubtitle(ResourceLocation id) {
			return JunkDrawers.MOD_ID + ".subtitle." + id.getPath();
		}

		public ResourceLocation modLoc(String name) {
			return new ResourceLocation(JunkDrawers.MOD_ID, name);
		}
	}

	private static class JunkRecipeProvider extends RecipeProvider {

		public JunkRecipeProvider(DataGenerator gen) {
			super(gen);
		}

		@Override
		protected void buildCraftingRecipes(Consumer<FinishedRecipe> recipeConsumer) {
			ShapedRecipeBuilder.shaped(JunkRegistry.DRAWER.get())
					.pattern("PCP").pattern("P P").pattern("PCP")
					.define('P', ItemTags.PLANKS)
					.define('C', Tags.Items.CHESTS_WOODEN)
					.unlockedBy("has_chest", has(Tags.Items.CHESTS_WOODEN)).save(recipeConsumer);
		}
	}

	private static class JunkLoot extends LootTableProvider {
		public JunkLoot(DataGenerator gen) {
			super(gen);
		}

		@Override
		protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> getTables() {
			return ImmutableList.of(
					Pair.of(BlockTables::new, LootContextParamSets.BLOCK)
			);
		}

		public static class BlockTables extends BlockLoot {

			@Override
			protected void addTables() {
				this.dropSelf(JunkRegistry.DRAWER.get());
			}

			@Override
			protected Iterable<Block> getKnownBlocks() {
				return (Iterable<Block>) JunkRegistry.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
			}
		}

		@Override
		protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationContext) {
			map.forEach((name, table) -> LootTables.validate(validationContext, name, table));
		}
	}

	private static class JunkBlockstateProvider extends BlockStateProvider {
		public JunkBlockstateProvider(DataGenerator gen, ExistingFileHelper helper) {
			super(gen, JunkDrawers.MOD_ID, helper);
		}

		@Override
		protected void registerStatesAndModels() {
			makeDrawer(JunkRegistry.DRAWER);
		}

		private void makeDrawer(RegistryObject<Block> registryObject) {
			ModelFile model = models().getExistingFile(modLoc("block/" + registryObject.getId().getPath()));
			getVariantBuilder(registryObject.get())
					.partialState().with(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)
					.modelForState().modelFile(model).addModel()
					.partialState().with(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST)
					.modelForState().modelFile(model).rotationY(90).addModel()
					.partialState().with(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH)
					.modelForState().modelFile(model).rotationY(180).addModel()
					.partialState().with(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST)
					.modelForState().modelFile(model).rotationY(270).addModel();
		}
	}

	private static class JunkItemModelProvider extends ItemModelProvider {
		public JunkItemModelProvider(DataGenerator gen, ExistingFileHelper helper) {
			super(gen, JunkDrawers.MOD_ID, helper);
		}

		@Override
		protected void registerModels() {
			withBlockParent(JunkRegistry.DRAWER.getId());
		}

		private void withBlockParent(ResourceLocation location) {
			withExistingParent(location.getPath(), modLoc("block/" + location.getPath()));
		}
	}
}
