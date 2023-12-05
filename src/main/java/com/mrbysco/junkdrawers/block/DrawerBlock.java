package com.mrbysco.junkdrawers.block;

import com.mrbysco.junkdrawers.block.blockentity.DrawerBlockEntity;
import com.mrbysco.junkdrawers.config.JunkConfig;
import com.mrbysco.junkdrawers.registry.JunkRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
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
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.capabilities.Capabilities;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.network.NetworkHooks;

import javax.annotation.Nullable;

public class DrawerBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

	protected static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 15, 16);

	public DrawerBlock(Properties builder) {
		super(builder);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, Boolean.valueOf(false)));
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new DrawerBlockEntity(pos, state);
	}

	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hitResult) {
		BlockEntity blockEntity = level.getBlockEntity(pos);
		if (blockEntity instanceof DrawerBlockEntity drawerBlockEntity) {
			BlockPos blockpos = pos.above();
			if (level.getBlockState(blockpos).isRedstoneConductor(level, blockpos)) {
				return InteractionResult.sidedSuccess(level.isClientSide);
			} else if (level.isClientSide) {
				return InteractionResult.SUCCESS;
			} else {
				drawerBlockEntity.handler.randomizeInventory();
				drawerBlockEntity.refreshClient();
				float percentageFilled = getFillPercentage(drawerBlockEntity.handler);
				if (percentageFilled >= JunkConfig.COMMON.jamPercentage.get() && level.random.nextDouble() <= JunkConfig.COMMON.jamChance.get()) {
					level.playSound(null, pos, JunkRegistry.DRAWER_JAMMED.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
					player.displayClientMessage(Component.translatable("junkdrawers.drawer.jammed").withStyle(ChatFormatting.YELLOW), true);
				} else {
					level.playSound(null, pos, JunkRegistry.DRAWER_OPEN.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
					NetworkHooks.openScreen((ServerPlayer) player, drawerBlockEntity, pos);
				}
				PiglinAi.angerNearbyPiglins(player, true);
				return InteractionResult.CONSUME;
			}
		} else {
			return InteractionResult.sidedSuccess(level.isClientSide);
		}
	}

	private float getFillPercentage(ItemStackHandler handler) {
		if (handler != null) {
			int size = handler.getSlots();
			int filledSlots = 0;
			for (int i = 0; i < size; i++) {
				if (!handler.getStackInSlot(i).isEmpty()) {
					filledSlots++;
				}
			}

			return ((float) filledSlots) / ((float) size);
		}

		return 0.0F;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			BlockEntity blockentity = level.getBlockEntity(pos);
			if (blockentity instanceof DrawerBlockEntity) {
				blockentity.getCapability(Capabilities.ITEM_HANDLER).ifPresent(handler -> {
					for (int i = 0; i < handler.getSlots(); ++i) {
						Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), handler.getStackInSlot(i));
					}
				});
			}

			super.onRemove(state, level, pos, newState, isMoving);
		}
	}

	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	public BlockState getStateForPlacement(BlockPlaceContext context) {
		FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()).setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
	}

	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, WATERLOGGED);
	}

	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
		if (stateIn.getValue(WATERLOGGED)) {
			level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
		}

		return super.updateShape(stateIn, facing, facingState, level, currentPos, facingPos);
	}
}