package com.cstav.evenmoreinstruments.block;

import com.cstav.evenmoreinstruments.block.blockentity.LooperBlockEntity;
import com.cstav.evenmoreinstruments.block.blockentity.ModBlockEntities;
import com.cstav.evenmoreinstruments.item.partial.emirecord.EMIRecordItem;
import com.cstav.evenmoreinstruments.util.LooperUtil;
import com.cstav.genshinstrument.item.InstrumentItem;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

public class LooperBlock extends Block implements EntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    //TODO: Redstone should trigger this
    public static final BooleanProperty PLAYING = BooleanProperty.create("playing");
    public static final BooleanProperty RECORD_IN = BooleanProperty.create("record_in");
    public static final BooleanProperty REDSTONE_TRIGGERED = BooleanProperty.create("redstone_triggered");
    public static final BooleanProperty LOOPING = BooleanProperty.create("looping");


    public LooperBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState()
            .setValue(PLAYING, false)
            .setValue(RECORD_IN, false)
            .setValue(FACING, Direction.NORTH)
            .setValue(REDSTONE_TRIGGERED, false)
            .setValue(LOOPING, true)
        );
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
        pBuilder.add(PLAYING, FACING, RECORD_IN, REDSTONE_TRIGGERED, LOOPING);
    }
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }


    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new LooperBlockEntity(pPos, pState);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState,
            BlockEntityType<T> pBlockEntityType) {
                
        return (!pLevel.isClientSide && pBlockEntityType == ModBlockEntities.LOOPER.get())
            ? (level, pos, state, be) -> ((LooperBlockEntity)(be)).tick(level, pos, state)
            : null;
    }

    public static BlockState cyclePlaying(LooperBlockEntity lbe, BlockState state) {
        return lbe.setPlaying(!state.getValue(PLAYING), state);
    }


    //#region Looper interaction handlers

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand,
            BlockHitResult pHit) {
        if (pLevel.isClientSide)
            return InteractionResult.CONSUME;

        final BlockEntity be = pLevel.getBlockEntity(pPos);
        if (!(be instanceof LooperBlockEntity lbe))
            return InteractionResult.FAIL;

        final ItemStack heldStack = pPlayer.getItemInHand(pHand);

        return performChainedInteractions(
            List.of(
                this::cycleLooping,
                this::insertRecord,
                this::validateRecordPresence,
                //   /\ Do not perform all following interactions if there is no record
                this::pairInstrumentItem,
                this::ejectRecord,
                this::cyclePlaying
            ),
            (interaction) -> interaction.run(pState, pLevel, pPos, pPlayer, lbe, heldStack, pHit)
        );
    }

    /**
     * Performs the described interactions one after another, until one does not return
     * {@link InteractionResult#FAIL}.
     * @return The interaction result of the successful interaction, or {@link InteractionResult#FAIL if none.
     */
    protected InteractionResult performChainedInteractions(final List<LooperInteractionRunnable> interactions,
                                                           Function<LooperInteractionRunnable, InteractionResult> performer) {
        for (LooperInteractionRunnable interaction : interactions) {
            final InteractionResult result = performer.apply(interaction);
            if (result != InteractionResult.FAIL)
                return result;
        }

        return InteractionResult.FAIL;
    }
    /**
     * Attempts to cycle the {@link LooperBlock#LOOPING looping} state of the looper,
     * provided the player right-clicked on {@link LooperBlock#getLoopDir the correct face}.
     */
    protected InteractionResult cycleLooping(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer,
                                             LooperBlockEntity lbe, ItemStack heldStack, BlockHitResult pHit) {
        if (pHit.getDirection() != getLoopDir(pState))
            return InteractionResult.FAIL;

        pLevel.setBlockAndUpdate(pPos, pState.cycle(LOOPING));
        return InteractionResult.SUCCESS;
    }
    protected InteractionResult insertRecord(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer,
                                             LooperBlockEntity lbe, ItemStack heldStack, BlockHitResult pHit) {
        if (!(heldStack.getItem() instanceof EMIRecordItem))
            return InteractionResult.FAIL;

        // Eject previously inserted record
        if (pState.getValue(RECORD_IN)) {
            lbe.popRecord();
        }

        lbe.setItem(0, heldStack);

        if (!pPlayer.isCreative())
            heldStack.shrink(1);

        if (!lbe.hasFootage()) {
            pPlayer.displayClientMessage(
                Component.translatable("evenmoreinstruments.record.no_footage"),
                true
            );
        }

        return InteractionResult.SUCCESS;
    }
    protected InteractionResult validateRecordPresence(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer,
                                                       LooperBlockEntity lbe, ItemStack heldStack, BlockHitResult pHit) {
        if (!pState.getValue(RECORD_IN)) {
            pPlayer.displayClientMessage(
                Component.translatable("evenmoreinstruments.looper.no_record").withStyle(ChatFormatting.RED),
                true
            );
            return InteractionResult.CONSUME_PARTIAL;
        }

        return InteractionResult.FAIL;
    }

    /**
     * Ejects the present record on the condition it is empty or the player is shifting.
     */
    protected InteractionResult ejectRecord(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer,
                                            LooperBlockEntity lbe, ItemStack heldStack, BlockHitResult pHit) {
        if (!pState.getValue(RECORD_IN))
            return InteractionResult.FAIL;

        if (pPlayer.isShiftKeyDown() || !lbe.hasFootage()) {
            lbe.popRecord();
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.FAIL;
    }
    protected InteractionResult pairInstrumentItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer,
                                                   LooperBlockEntity lbe, ItemStack heldStack, BlockHitResult pHit) {
        if (!(heldStack.getItem() instanceof InstrumentItem))
            return InteractionResult.FAIL;

        if (lbe.isWritable()) {
            if (LooperUtil.performPair(lbe, () -> LooperUtil.createLooperTag(heldStack, pPos), pPlayer))
                return InteractionResult.SUCCESS;
        } else {
            pPlayer.displayClientMessage(
                Component.translatable("evenmoreinstruments.record.not_writable").withStyle(ChatFormatting.YELLOW)
                , true);
            return InteractionResult.CONSUME;
        }

        return InteractionResult.FAIL;
    }
    protected InteractionResult cyclePlaying(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer,
                                             LooperBlockEntity lbe, ItemStack heldStack, BlockHitResult pHit) {
        if (!lbe.hasFootage()) {
            if (lbe.isWritable()) {
                pPlayer.displayClientMessage(
                    Component.translatable("evenmoreinstruments.record.no_footage"),
                    true
                );
            }

            return InteractionResult.CONSUME_PARTIAL;
        }

        pLevel.setBlockAndUpdate(pPos,
            cyclePlaying(lbe, pState)
                .setValue(REDSTONE_TRIGGERED, false)
        );
        return InteractionResult.SUCCESS;
    }

    //#endregion


    @Override
    public void playerWillDestroy(Level pLevel, BlockPos pPos, BlockState pState, Player pPlayer) {
        super.playerWillDestroy(pLevel, pPos, pState, pPlayer);
        if (pLevel.isClientSide())
            return;

        if (!(pLevel.getBlockEntity(pPos) instanceof LooperBlockEntity lbe))
            return;

        lbe.popRecord();
    }


    //#region redstone impl

    public Direction getLoopDir(final BlockState state) {
        return state.getValue(FACING).getOpposite();
    }
    public Direction getPlayDir(final BlockState state) {
        return state.getValue(FACING);
    }

    @Override
    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pNeighborBlock,
            BlockPos pNeighborPos, boolean pMovedByPiston) {
        if (pLevel.isClientSide)
            return;
        if (!(pLevel.getBlockEntity(pPos) instanceof LooperBlockEntity lbe))
            return;

        boolean wasRedstoneTriggered = pState.getValue(REDSTONE_TRIGGERED);
        if (pLevel.hasNeighborSignal(pPos)) {
            // This mechanism should act alike as a T-flip-flop.
            // We must be sure that it wasn't just some random block update messing up
            // with the toggle.
            if (!wasRedstoneTriggered) {
                lbe.setTicks(0);

                pLevel.setBlockAndUpdate(pPos,
                    lbe.setPlaying(true, pState)
                    .setValue(REDSTONE_TRIGGERED, true)
                );
            }
        }
        else if (wasRedstoneTriggered) {
            // Redstone NO LONGER signals
            pLevel.setBlockAndUpdate(pPos,
                lbe.setPlaying(false, pState)
                .setValue(REDSTONE_TRIGGERED, false)
            );
        }
    }

    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction direction) {
        return getPlayDir(state).getOpposite() == direction;
    }

    //#endregion


    @Override
    public boolean triggerEvent(BlockState pState, Level pLevel, BlockPos pPos, int pId, int pParam) {
        switch (pId) {
            case 42:
                return emitNoteParticle(pLevel, pPos, pParam);
        }

        return false;
    }

    protected boolean emitNoteParticle(Level level, BlockPos pos, int noteIndex) {
        // Copied from note block
        level.addParticle(ParticleTypes.NOTE,
            (double)pos.getX() + 0.5D, (double)pos.getY() + 1.2D, (double)pos.getZ() + 0.5D,
            (double)noteIndex / 24.0D, 0.0D, 0.0D
        );

        return true;
    }

}
