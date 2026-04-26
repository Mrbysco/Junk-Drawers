package com.mrbysco.junkdrawers.block;

import com.mojang.serialization.MapCodec;
import com.mrbysco.junkdrawers.block.blockentity.DrawerBlockEntity;
import com.mrbysco.junkdrawers.block.blockentity.RandomizedItemStackHandler;
import com.mrbysco.junkdrawers.config.JunkConfig;
import com.mrbysco.junkdrawers.registry.JunkRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.Nullable;

public class DrawerBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
	public static final MapCodec<DrawerBlock> CODEC = simpleCodec(DrawerBlock::new);

	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

	protected static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 15, 16);

	public DrawerBlock(Properties builder) {
		super(builder);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, Boolean.valueOf(false)));
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return CODEC;
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new DrawerBlockEntity(pos, state);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		BlockEntity blockEntity = level.getBlockEntity(pos);
		if (blockEntity instanceof DrawerBlockEntity drawerBlockEntity) {
			BlockPos blockpos = pos.above();
			if (level.getBlockState(blockpos).isRedstoneConductor(level, blockpos)) {
				return InteractionResult.SUCCESS;
			} else if (level.isClientSide()) {
				return InteractionResult.SUCCESS;
			} else {
				ResourceHandler<ItemResource> handler = level.getCapability(Capabilities.Item.BLOCK, pos, null);
				if (handler instanceof RandomizedItemStackHandler randomizedItemStackHandler) {
					try (var tx = Transaction.openRoot()) {
						randomizedItemStackHandler.randomizeInventory(tx);
						drawerBlockEntity.refreshClient();
						float percentageFilled = getFillPercentage(randomizedItemStackHandler);
						if (percentageFilled >= JunkConfig.COMMON.jamPercentage.get() && level.getRandom().nextDouble() <= JunkConfig.COMMON.jamChance.get()) {
							level.playSound(null, pos, JunkRegistry.DRAWER_JAMMED.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
							player.sendOverlayMessage(Component.translatable("junkdrawers.drawer.jammed").withStyle(ChatFormatting.YELLOW));
						} else {
							level.playSound(null, pos, JunkRegistry.DRAWER_OPEN.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
							player.openMenu(drawerBlockEntity, pos);
						}
					}
				}
				PiglinAi.angerNearbyPiglins((ServerLevel) level, player, true);
				return InteractionResult.CONSUME;
			}
		} else {
			return super.useWithoutItem(state, level, pos, player, hitResult);
		}
	}

	private float getFillPercentage(ResourceHandler<ItemResource> handler) {
		if (handler != null) {
			int size = handler.size();
			int filledSlots = 0;
			for (int i = 0; i < size; i++) {
				if (!handler.getResource(i).isEmpty()) {
					filledSlots++;
				}
			}

			return ((float) filledSlots) / ((float) size);
		}

		return 0.0F;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()).setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, WATERLOGGED);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess scheduledTickAccess, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
		if (state.getValue(WATERLOGGED)) {
			scheduledTickAccess.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
		}

		return super.updateShape(state, level, scheduledTickAccess, pos, direction, neighborPos, neighborState, random);
	}
}