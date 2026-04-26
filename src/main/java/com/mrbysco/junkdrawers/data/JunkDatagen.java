package com.mrbysco.junkdrawers.data;

import com.mrbysco.junkdrawers.JunkDrawers;
import com.mrbysco.junkdrawers.block.DrawerBlock;
import com.mrbysco.junkdrawers.registry.JunkRegistry;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@EventBusSubscriber
public class JunkDatagen {

	@SubscribeEvent
	public static void gatherData(GatherDataEvent.Client event) {
		DataGenerator generator = event.getGenerator();
		PackOutput packOutput = generator.getPackOutput();
		CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

		generator.addProvider(true, new JunkLoot(packOutput, lookupProvider));
		generator.addProvider(true, new JunkRecipeProvider.Runner(packOutput, lookupProvider));
		generator.addProvider(true, new JunkBlockTagsProvider(packOutput, lookupProvider));

		generator.addProvider(true, new JunkLanguageProvider(packOutput));
		generator.addProvider(true, new JunkSoundProvider(packOutput));
		generator.addProvider(true, new JunkModelProvider(packOutput));
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

			addConfig("general", "General", "General Settings");
			addConfig("randomizeOnChange", "Randomize On Change", "Randomize the drawer inventory when the content changes (This might have unexpected consequences) [Default: false]");
			addConfig("jamPercentage", "Jam percentage", "The percentage of the drawer that needs to be filled for the inventory to jam [Default: 0.9 (90%)]");
			addConfig("jamChance", "Jam Chance", "The chance the drawer jams when the 'jamPercentage' is met [Default: 0.3 (30%)]");
		}

		/**
		 * Add a subtitle to a sound event
		 *
		 * @param sound The sound event
		 * @param text  The subtitle text
		 */
		public void addSubtitle(Supplier<SoundEvent> sound, String text) {
			this.addSubtitle(sound.get(), text);
		}

		/**
		 * Add a subtitle to a sound event
		 *
		 * @param sound The sound event registry object
		 * @param text  The subtitle text
		 */
		public void addSubtitle(SoundEvent sound, String text) {
			String path = JunkDrawers.MOD_ID + ".subtitle." + sound.location().getPath();
			this.add(path, text);
		}

		/**
		 * Add the translation for a config entry
		 *
		 * @param path        The path of the config entry
		 * @param name        The name of the config entry
		 * @param description The description of the config entry (optional in case of targeting "title" or similar entries that have no tooltip)
		 */
		private void addConfig(String path, String name, @Nullable String description) {
			this.add(JunkDrawers.MOD_ID + ".configuration." + path, name);
			if (description != null && !description.isEmpty())
				this.add(JunkDrawers.MOD_ID + ".configuration." + path + ".tooltip", description);
		}
	}

	public static class JunkSoundProvider extends SoundDefinitionsProvider {

		public JunkSoundProvider(PackOutput packOutput) {
			super(packOutput, JunkDrawers.MOD_ID);
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


		public String modSubtitle(Identifier id) {
			return JunkDrawers.MOD_ID + ".subtitle." + id.getPath();
		}

		public Identifier modLoc(String name) {
			return JunkDrawers.modLoc(name);
		}
	}

	private static class JunkRecipeProvider extends RecipeProvider {

		public JunkRecipeProvider(HolderLookup.Provider provider, RecipeOutput recipeOutput) {
			super(provider, recipeOutput);
		}

		@Override
		protected void buildRecipes() {
			generateRecipe(output, JunkRegistry.OAK_DRAWER.get(), Items.OAK_PLANKS);
			generateRecipe(output, JunkRegistry.SPRUCE_DRAWER.get(), Items.SPRUCE_PLANKS);
			generateRecipe(output, JunkRegistry.BIRCH_DRAWER.get(), Items.BIRCH_PLANKS);
			generateRecipe(output, JunkRegistry.JUNGLE_DRAWER.get(), Items.JUNGLE_PLANKS);
			generateRecipe(output, JunkRegistry.ACACIA_DRAWER.get(), Items.ACACIA_PLANKS);
			generateRecipe(output, JunkRegistry.CHERRY_DRAWER.get(), Items.CHERRY_PLANKS);
			generateRecipe(output, JunkRegistry.DARK_OAK_DRAWER.get(), Items.DARK_OAK_PLANKS);
			generateRecipe(output, JunkRegistry.MANGROVE_DRAWER.get(), Items.MANGROVE_PLANKS);
			generateRecipe(output, JunkRegistry.BAMBOO_DRAWER.get(), Items.BAMBOO_PLANKS);
			generateRecipe(output, JunkRegistry.CRIMSON_DRAWER.get(), Items.CRIMSON_PLANKS);
			generateRecipe(output, JunkRegistry.WARPED_DRAWER.get(), Items.WARPED_PLANKS);
		}

		private void generateRecipe(RecipeOutput recipeOutput, ItemLike drawer, Item planks) {
			shaped(RecipeCategory.REDSTONE, drawer)
					.pattern("PCP").pattern("P P").pattern("PCP")
					.define('P', planks)
					.define('C', Tags.Items.CHESTS_WOODEN)
					.unlockedBy("has_chest", has(Tags.Items.CHESTS_WOODEN))
					.unlockedBy("has_planks", has(planks)).save(recipeOutput);
		}

		public static class Runner extends RecipeProvider.Runner {
			public Runner(PackOutput output, CompletableFuture<Provider> completableFuture) {
				super(output, completableFuture);
			}

			@Override
			protected RecipeProvider createRecipeProvider(HolderLookup.Provider provider, RecipeOutput recipeOutput) {
				return new JunkRecipeProvider(provider, recipeOutput);
			}

			@Override
			public String getName() {
				return "Junk Drawers Recipes";
			}
		}
	}

	private static class JunkLoot extends LootTableProvider {
		public JunkLoot(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
			super(packOutput, Set.of(), List.of
					(new SubProviderEntry(JunkBlockTables::new, LootContextParamSets.BLOCK)
					), lookupProvider);
		}

		public static class JunkBlockTables extends BlockLootSubProvider {

			protected JunkBlockTables(HolderLookup.Provider provider) {
				super(Set.of(), FeatureFlags.REGISTRY.allFlags(), provider);
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
				return (Iterable<Block>) JunkRegistry.BLOCKS.getEntries().stream().map(holder -> (Block) holder.get())::iterator;
			}
		}
	}

	private static class JunkBlockTagsProvider extends BlockTagsProvider {
		public JunkBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
			super(output, lookupProvider, JunkDrawers.MOD_ID);
		}

		@Override
		protected void addTags(HolderLookup.Provider provider) {
			this.tag(BlockTags.MINEABLE_WITH_AXE).add(JunkRegistry.OAK_DRAWER.get(), JunkRegistry.SPRUCE_DRAWER.get(),
					JunkRegistry.BIRCH_DRAWER.get(), JunkRegistry.JUNGLE_DRAWER.get(), JunkRegistry.ACACIA_DRAWER.get(),
					JunkRegistry.CHERRY_DRAWER.get(), JunkRegistry.DARK_OAK_DRAWER.get(), JunkRegistry.MANGROVE_DRAWER.get(),
					JunkRegistry.BAMBOO_DRAWER.get(), JunkRegistry.CRIMSON_DRAWER.get(), JunkRegistry.WARPED_DRAWER.get());
		}
	}

	private static class JunkModelProvider extends ModelProvider {
		public static final ModelTemplate DRAWER = ModelTemplates.create("junkdrawers:template_drawer", TextureSlot.TEXTURE);

		public JunkModelProvider(PackOutput output) {
			super(output, JunkDrawers.MOD_ID);
		}

		@Override
		protected void registerModels(@NotNull BlockModelGenerators blockModels, @NotNull ItemModelGenerators itemModels) {
			makeDrawer(blockModels, JunkRegistry.OAK_DRAWER);
			makeDrawer(blockModels, JunkRegistry.SPRUCE_DRAWER);
			makeDrawer(blockModels, JunkRegistry.BIRCH_DRAWER);
			makeDrawer(blockModels, JunkRegistry.JUNGLE_DRAWER);
			makeDrawer(blockModels, JunkRegistry.ACACIA_DRAWER);
			makeDrawer(blockModels, JunkRegistry.CHERRY_DRAWER);
			makeDrawer(blockModels, JunkRegistry.DARK_OAK_DRAWER);
			makeDrawer(blockModels, JunkRegistry.MANGROVE_DRAWER);
			makeDrawer(blockModels, JunkRegistry.BAMBOO_DRAWER);

			makeDrawer(blockModels, JunkRegistry.CRIMSON_DRAWER);
			makeDrawer(blockModels, JunkRegistry.WARPED_DRAWER);
		}

		private void makeDrawer(BlockModelGenerators blockModels, DeferredBlock<DrawerBlock> registryObject) {
			Identifier texture = JunkDrawers.modLoc("block/" + registryObject.getId().getPath());
			Identifier model = DRAWER.create(registryObject.get(), TextureMapping.defaultTexture(new Material(texture)), blockModels.modelOutput);
			blockModels.blockStateOutput
					.accept(
							MultiVariantGenerator.dispatch(
											registryObject.get(), BlockModelGenerators.plainVariant(model)
									)
									.with(BlockModelGenerators.ROTATION_HORIZONTAL_FACING)
					);
			blockModels.registerSimpleItemModel(registryObject.get(), model);
		}
	}
}
