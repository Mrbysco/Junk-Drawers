package com.mrbysco.junkdrawers.config;

import com.mrbysco.junkdrawers.JunkDrawers;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.apache.commons.lang3.tuple.Pair;

public class JunkConfig {
	public static class Common {
		public final ForgeConfigSpec.BooleanValue randomizeOnContentChange;
		public final ForgeConfigSpec.DoubleValue jamPercentage;
		public final ForgeConfigSpec.DoubleValue jamChance;

		Common(ForgeConfigSpec.Builder builder) {
			builder.comment("General")
					.push("general");

			this.randomizeOnContentChange = builder
					.comment("Randomize the drawer inventory when the content changes [Default: true]")
					.define("randomizeOnChange", true);

			this.jamPercentage = builder
					.comment("The percentage of the drawer that needs to be filled for the inventory to jam [Default: 0.9 (90%)]")
					.defineInRange("jamPercentage", 0.9, 0, 1);

			this.jamChance = builder
					.comment("The chance the drawer jams when the 'jamPercentage' is met [Default: 0.3 (30%)]")
					.defineInRange("jamChance", 0.3, 0, 1);

			builder.pop();
		}
	}


	public static final ForgeConfigSpec commonSpec;
	public static final Common COMMON;

	static {
		final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
		commonSpec = specPair.getRight();
		COMMON = specPair.getLeft();
	}

	@SubscribeEvent
	public static void onLoad(final ModConfigEvent.Loading configEvent) {
		JunkDrawers.LOGGER.debug("Loaded Junk drawers' config file {}", configEvent.getConfig().getFileName());
	}

	@SubscribeEvent
	public static void onFileChange(final ModConfigEvent.Reloading configEvent) {
		JunkDrawers.LOGGER.warn("Junk drawers' config just got changed on the file system!");
	}
}
