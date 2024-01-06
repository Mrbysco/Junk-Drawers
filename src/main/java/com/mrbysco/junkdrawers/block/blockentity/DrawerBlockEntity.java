package com.mrbysco.junkdrawers.block.blockentity;

import com.mrbysco.junkdrawers.JunkDrawers;
import com.mrbysco.junkdrawers.config.JunkConfig;
import com.mrbysco.junkdrawers.menu.DrawerMenu;
import com.mrbysco.junkdrawers.registry.JunkRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class DrawerBlockEntity extends BlockEntity implements MenuProvider {
	public final RandomizedItemStackHandler handler = new RandomizedItemStackHandler(90) {
		@Override
		protected void onContentsChanged(int slot) {
			if (JunkConfig.COMMON.randomizeOnContentChange.get()) {
				this.randomizeInventory();
			}
			super.onContentsChanged(slot);
			refreshClient();
		}
	};

	public DrawerBlockEntity(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
		super(blockEntityType, pos, state);
	}

	public DrawerBlockEntity(BlockPos pos, BlockState state) {
		this(JunkRegistry.DRAWER_BLOCK_ENTITY.get(), pos, state);
	}

	static void playSound(Level level, BlockPos pos, SoundEvent event) {
		double x = (double) pos.getX() + 0.5D;
		double y = (double) pos.getY() + 0.5D;
		double z = (double) pos.getZ() + 0.5D;
		level.playSound((Player) null, x, y, z, event, SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
	}

	@Override
	public void load(CompoundTag compound) {
		super.load(compound);
		handler.deserializeNBT(compound.getCompound("ItemStackHandler"));
	}

	@Override
	public void saveAdditional(CompoundTag compound) {
		super.saveAdditional(compound);
		compound.put("ItemStackHandler", handler.serializeNBT());
	}

	public RandomizedItemStackHandler getHandler(@Nullable Direction direction) {
		return handler;
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
		if (pkt.getTag() != null)
			load(pkt.getTag());

		BlockState state = level.getBlockState(getBlockPos());
		level.sendBlockUpdated(getBlockPos(), state, state, 3);
	}

	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag nbt = new CompoundTag();
		this.saveAdditional(nbt);
		return nbt;
	}

	@Override
	public CompoundTag getPersistentData() {
		CompoundTag nbt = new CompoundTag();
		this.saveAdditional(nbt);
		return nbt;
	}

	public void refreshClient() {
		setChanged();
		BlockState state = level.getBlockState(worldPosition);
		level.sendBlockUpdated(worldPosition, state, state, 2);
	}

	@Nullable
	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public Component getDisplayName() {
		return Component.translatable(JunkDrawers.MOD_ID + ".container.drawer");
	}

	public boolean stillValid(Player player) {
		if (this.level.getBlockEntity(this.worldPosition) != this) {
			return false;
		} else {
			return !(player.distanceToSqr((double) this.worldPosition.getX() + 0.5D,
					(double) this.worldPosition.getY() + 0.5D, (double) this.worldPosition.getZ() + 0.5D) > 64.0D);
		}
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
		return new DrawerMenu(id, inventory, this);
	}
}
