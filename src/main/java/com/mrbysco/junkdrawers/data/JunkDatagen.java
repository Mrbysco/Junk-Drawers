package com.mrbysco.junkdrawers.data;

import com.mrbysco.junkdrawers.JunkDrawers;
import com.mrbysco.junkdrawers.registry.JunkRegistry;
import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.common.data.SoundDefinitionsProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class JunkDatagen {

	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		PackOutput packOutput = generator.getPackOutput();
		ExistingFileHelper helper = event.getExistingFileHelper();

		if (event.includeServer()) {
			generator.addProvider(true, new JunkLoot(packOutput));
			generator.addProvider(true, new JunkRecipeProvider(packOutput));
		}
		if (event.includeClient()) {
			generator.addProvider(true, new JunkLanguageProvider(packOutput));
			generator.addProvider(true, new JunkSoundProvider(packOutput, helper));
			generator.addProvider(true, new JunkBlockstateProvider(packOutput, helper));
			generator.addProvider(true, new JunkItemModelProvider(packOutput, helper));
		}
	}

	private static class JunkLanguageProvider extends LanguageProvider {

		public JunkLanguageProvider(PackOutput packOutput) {
			super(packOutput, JunkDrawers.MOD_ID, "en_us");
		}

		@Override
		protected void addTranslations() {
			add("itemGroup.junkdrawers.tab", "Junk Drawers");

			addBlock(JunkRegistry.OAK_DRAWER, "Oak Junk Drawer");
			addBlock(JunkRegistry.SPRUCE_DRAWER, "Spruce Junk Drawer");
			addBlock(JunkRegistry.BIRCH_DRAWER, "Birch Junk Drawer");
			addBlock(JunkRegistry.JUNGLE_DRAWER, "Jungle Junk Drawer");
			addBlock(JunkRegistry.ACACIA_DRAWER, "Acacia Junk Drawer");
			addBlock(JunkRegistry.CHERRY_DRAWER, "Cherry Junk Drawer");
			addBlock(JunkRegistry.DARK_OAK_DRAWER, "Dark Oak Junk Drawer");
			addBlock(JunkRegistry.MANGROVE_DRAWER, "Mangrove Junk Drawer");
			addBlock(JunkRegistry.BAMBOO_DRAWER, "Bamboo Junk Drawer");

			addBlock(JunkRegistry.CRIMSON_DRAWER, "Crimson Junk Drawer");
			addBlock(JunkRegistry.WARPED_DRAWER, "Warped Junk Drawer");

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

		public JunkSoundProvider(PackOutput packOutput, ExistingFileHelper existingFileHelper) {
			super(packOutput, JunkDrawers.MOD_ID, existingFileHelper);
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

		public JunkRecipeProvider(PackOutput packOutput) {
			super(packOutput);
		}

		@Override
		protected void buildRecipes(Consumer<FinishedRecipe> recipeConsumer) {
			generateRecipe(recipeConsumer, JunkRegistry.OAK_DRAWER.get(), Items.OAK_PLANKS);
			generateRecipe(recipeConsumer, JunkRegistry.SPRUCE_DRAWER.get(), Items.SPRUCE_PLANKS);
			generateRecipe(recipeConsumer, JunkRegistry.BIRCH_DRAWER.get(), Items.BIRCH_PLANKS);
			generateRecipe(recipeConsumer, JunkRegistry.JUNGLE_DRAWER.get(), Items.JUNGLE_PLANKS);
			generateRecipe(recipeConsumer, JunkRegistry.ACACIA_DRAWER.get(), Items.ACACIA_PLANKS);
			generateRecipe(recipeConsumer, JunkRegistry.CHERRY_DRAWER.get(), Items.CHERRY_PLANKS);
			generateRecipe(recipeConsumer, JunkRegistry.DARK_OAK_DRAWER.get(), Items.DARK_OAK_PLANKS);
			generateRecipe(recipeConsumer, JunkRegistry.MANGROVE_DRAWER.get(), Items.MANGROVE_PLANKS);
			generateRecipe(recipeConsumer, JunkRegistry.BAMBOO_DRAWER.get(), Items.BAMBOO_PLANKS);
			generateRecipe(recipeConsumer, JunkRegistry.CRIMSON_DRAWER.get(), Items.CRIMSON_PLANKS);
			generateRecipe(recipeConsumer, JunkRegistry.WARPED_DRAWER.get(), Items.WARPED_PLANKS);
		}

		private void generateRecipe(Consumer<FinishedRecipe> recipeConsumer, ItemLike drawer, Item planks) {
			ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, drawer)
					.pattern("PCP").pattern("P P").pattern("PCP")
					.define('P', planks)
					.define('C', Tags.Items.CHESTS_WOODEN)
					.unlockedBy("has_chest", has(Tags.Items.CHESTS_WOODEN))
					.unlockedBy("has_planks", has(planks)).save(recipeConsumer);
		}
	}

	private static class JunkLoot extends LootTableProvider {
		public JunkLoot(PackOutput packOutput) {
			super(packOutput, Set.of(), List.of
					(new SubProviderEntry(JunkBlockTables::new, LootContextParamSets.BLOCK)
					));
		}

		public static class JunkBlockTables extends BlockLootSubProvider {

			protected JunkBlockTables() {
				super(Set.of(), FeatureFlags.REGISTRY.allFlags());
			}

			@Override
			protected void generate() {
				this.dropSelf(JunkRegistry.OAK_DRAWER.get());
				this.dropSelf(JunkRegistry.SPRUCE_DRAWER.get());
				this.dropSelf(JunkRegistry.BIRCH_DRAWER.get());
				this.dropSelf(JunkRegistry.JUNGLE_DRAWER.get());
				this.dropSelf(JunkRegistry.ACACIA_DRAWER.get());
				this.dropSelf(JunkRegistry.CHERRY_DRAWER.get());
				this.dropSelf(JunkRegistry.DARK_OAK_DRAWER.get());
				this.dropSelf(JunkRegistry.MANGROVE_DRAWER.get());
				this.dropSelf(JunkRegistry.BAMBOO_DRAWER.get());

				this.dropSelf(JunkRegistry.CRIMSON_DRAWER.get());
				this.dropSelf(JunkRegistry.WARPED_DRAWER.get());
			}

			@Override
			protected Iterable<Block> getKnownBlocks() {
				return (Iterable<Block>) JunkRegistry.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
			}
		}

		@Override
		protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationContext) {
			map.forEach((name, table) -> table.validate(validationContext));
		}
	}

	private static class JunkBlockstateProvider extends BlockStateProvider {
		public JunkBlockstateProvider(PackOutput packOutput, ExistingFileHelper helper) {
			super(packOutput, JunkDrawers.MOD_ID, helper);
		}

		@Override
		protected void registerStatesAndModels() {
			makeDrawer(JunkRegistry.OAK_DRAWER);
			makeAnotherDrawer(JunkRegistry.SPRUCE_DRAWER);
			makeAnotherDrawer(JunkRegistry.BIRCH_DRAWER);
			makeAnotherDrawer(JunkRegistry.JUNGLE_DRAWER);
			makeAnotherDrawer(JunkRegistry.ACACIA_DRAWER);
			makeAnotherDrawer(JunkRegistry.CHERRY_DRAWER);
			makeAnotherDrawer(JunkRegistry.DARK_OAK_DRAWER);
			makeAnotherDrawer(JunkRegistry.MANGROVE_DRAWER);
			makeAnotherDrawer(JunkRegistry.BAMBOO_DRAWER);

			makeAnotherDrawer(JunkRegistry.CRIMSON_DRAWER);
			makeAnotherDrawer(JunkRegistry.WARPED_DRAWER);
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

		private void makeAnotherDrawer(RegistryObject<Block> registryObject) {
			ResourceLocation texture = modLoc("block/" + registryObject.getId().getPath());
			ModelFile model = models().getBuilder(registryObject.getId().getPath())
					.parent(models().getExistingFile(modLoc("block/drawer")))
					.texture("planks", texture)
					.texture("particle", texture);
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
		public JunkItemModelProvider(PackOutput packOutput, ExistingFileHelper helper) {
			super(packOutput, JunkDrawers.MOD_ID, helper);
		}

		@Override
		protected void registerModels() {
			withBlockParent(JunkRegistry.OAK_DRAWER.getId());
			withBlockParent(JunkRegistry.SPRUCE_DRAWER.getId());
			withBlockParent(JunkRegistry.BIRCH_DRAWER.getId());
			withBlockParent(JunkRegistry.JUNGLE_DRAWER.getId());
			withBlockParent(JunkRegistry.ACACIA_DRAWER.getId());
			withBlockParent(JunkRegistry.CHERRY_DRAWER.getId());
			withBlockParent(JunkRegistry.DARK_OAK_DRAWER.getId());
			withBlockParent(JunkRegistry.MANGROVE_DRAWER.getId());
			withBlockParent(JunkRegistry.BAMBOO_DRAWER.getId());

			withBlockParent(JunkRegistry.CRIMSON_DRAWER.getId());
			withBlockParent(JunkRegistry.WARPED_DRAWER.getId());
		}

		private void withBlockParent(ResourceLocation location) {
			withExistingParent(location.getPath(), modLoc("block/" + location.getPath()));
		}
	}
}
