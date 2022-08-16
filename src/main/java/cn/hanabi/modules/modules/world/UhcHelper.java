package cn.hanabi.modules.modules.world;

import aLph4anTi1eaK_cN.Annotation.ObfuscationClass;
import cn.hanabi.events.EventPacket;
import cn.hanabi.events.EventPostMotion;
import cn.hanabi.events.EventPreMotion;
import cn.hanabi.events.EventUpdate;
import cn.hanabi.modules.Category;
import cn.hanabi.modules.Mod;
import cn.hanabi.utils.BlockUtils;
import cn.hanabi.utils.PlayerUtil;
import cn.hanabi.utils.TimeHelper;
import cn.hanabi.value.Value;
import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockHopper;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.server.S2CPacketSpawnGlobalEntity;
import net.minecraft.util.*;
import net.minecraft.world.WorldSettings;
import org.lwjgl.input.Keyboard;


@ObfuscationClass
public class UhcHelper extends Mod {

    public static int movement;
    public static int x;
    public static int y;
    public static int z;
    public Value<Boolean> sandbreak = new Value<>("UHCHelper", "Sand Breaker", false);
    public Value<Boolean> LightningCheck = new Value<>("UHCHelper", "Lightning Check", false);
    //  public Value<Boolean> autoSneak = new Value<Boolean>("UHCHelper", "Auto Sneak", false);
    public Value<Boolean> noSandDamage = new Value<>("UHCHelper", "No Sand Damage", false);
    public Value<Boolean> noWaterDamage = new Value<>("UHCHelper", "No Water Damage", false);
    public Value<Boolean> lessPacket = new Value<>("UHCHelper", "Less Packet", false);
    public Value<Boolean> autojump = new Value<>("UHCHelper", "Stuck Jump", false);
    public Value<Boolean> autowater = new Value<>("UHCHelper", "Auto Water", false);
    public Value<Boolean> sneakily = new Value<>("UHCHelper", "Sneak Move", false);


    boolean sneak = false;
    TimeHelper sneakTimer = new TimeHelper();
    int clock;
    int delay;
    private final TimeHelper timer = new TimeHelper();
    private boolean reFill;
    private final TimeHelper refillTimer = new TimeHelper();

    public UhcHelper() {
        super("UHCHelper", Category.WORLD);
    }

    /* AntiObf By Kody Edit By VanillaMirror */
    @EventTarget
    public void OnUpdate(EventUpdate e) {
        BlockPos sand = new BlockPos(new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + 3, mc.thePlayer.posZ));
        Block sandblock = mc.theWorld.getBlockState(sand).getBlock();
        BlockPos forge = new BlockPos(new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + 2, mc.thePlayer.posZ));
        Block forgeblock = mc.theWorld.getBlockState(forge).getBlock();
        BlockPos obsidianpos = new BlockPos(new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + 1, mc.thePlayer.posZ));
        Block obsidianblock = mc.theWorld.getBlockState(obsidianpos).getBlock();

        if (obsidianblock == Block.getBlockById(49)) {
            bestTool(mc.objectMouseOver.getBlockPos().getX(), mc.objectMouseOver.getBlockPos().getY(),
                    mc.objectMouseOver.getBlockPos().getZ());
            BlockPos downpos = new BlockPos(new Vec3(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ));
            mc.playerController.onPlayerDamageBlock(downpos, EnumFacing.UP);
        }
        if (forgeblock == Block.getBlockById(61)) {
            bestTool(mc.objectMouseOver.getBlockPos().getX(), mc.objectMouseOver.getBlockPos().getY(),
                    mc.objectMouseOver.getBlockPos().getZ());
            BlockPos downpos = new BlockPos(new Vec3(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ));
            mc.playerController.onPlayerDamageBlock(downpos, EnumFacing.UP);
        }

        if (sandbreak.getValueState()) {
            if (sandblock == Block.getBlockById(12) || sandblock == Block.getBlockById(13)) {
                bestTool(mc.objectMouseOver.getBlockPos().getX(), mc.objectMouseOver.getBlockPos().getY(),
                        mc.objectMouseOver.getBlockPos().getZ());
                BlockPos downpos = new BlockPos(new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + 3, mc.thePlayer.posZ));
                PlayerUtil.tellPlayer("Sand On your Head. Care for it :D");
                mc.playerController.onPlayerDamageBlock(downpos, EnumFacing.UP);
            }
        }

        if (lessPacket.getValueState()) {
            mc.thePlayer.setGameType(WorldSettings.GameType.SURVIVAL);
            mc.thePlayer.setGameType(WorldSettings.GameType.CREATIVE);
        }

        if (autojump.getValue()) {
            if (PlayerUtil.isUnderBlock(mc.thePlayer) && PlayerUtil.isMoving2() && mc.thePlayer.onGround && Keyboard.isKeyDown(Keyboard.KEY_LMENU)) {
                mc.thePlayer.jump();
            }
        }

    }

    @EventTarget
    public void onPre(EventPreMotion e) {
        if (autowater.getValue()) {
            if (mc.thePlayer.isBurning())
                if (((getSlotWaterBucket() != -1) || reFill)) {
                    e.setPitch(90.0f);
                }

        }

        if (mc.gameSettings.keyBindSneak.isKeyDown() && sneakily.getValue())
            mc.getNetHandler().getNetworkManager().sendPacket(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));

    }

    @EventTarget
    public void onPost(EventPostMotion post) {

        if (mc.gameSettings.keyBindSneak.isKeyDown() && sneakily.getValue())
            mc.getNetHandler().getNetworkManager().sendPacket(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING));

        if (autowater.getValue()) {
            if (mc.thePlayer.isBurning()) {
                if (reFill) {
                    BlockPos pos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ);
                    PlayerUtil.debug("Re");
                    this.placeWater(pos);
                    reFill = false;
                } else
                    if (refillTimer.isDelayComplete(1500) && reFill) {
                        this.reFill = false;
                    }

                if (this.getSlotWaterBucket() != -1) {
                    if (timer.isDelayComplete(500)) {
                        timer.reset();
                        this.swapToWaterBucket(this.getSlotWaterBucket());
                        BlockPos pos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - BlockUtils.getDistanceToFall() - 1, mc.thePlayer.posZ);
                        this.placeWater(pos);
                        PlayerUtil.debug("Redo");

                        reFill = true;
                        refillTimer.reset();
                    }
                }
            }
        }

    }


    private void placeWater(BlockPos pos) {
        ItemStack heldItem = mc.thePlayer.inventory.getCurrentItem();

        mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.inventory.getCurrentItem(), pos, EnumFacing.UP, new Vec3((double) pos.getX() + 0.5, (double) pos.getY() + 1, (double) pos.getZ() + 0.5));
        if (heldItem != null) {
            mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, heldItem);
            mc.entityRenderer.itemRenderer.resetEquippedProgress2();
        }
    }

    private void swapToWaterBucket(int blockSlot) {
        mc.thePlayer.inventory.currentItem = blockSlot;
        mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C09PacketHeldItemChange(blockSlot));
    }

    private int getSlotWaterBucket() {
        for (int i = 0; i < 8; i++) {
            if (mc.thePlayer.inventory.mainInventory[i] != null && mc.thePlayer.inventory.mainInventory[i].getItem().getUnlocalizedName().contains("bucketWater"))
                return i;
        }
        return -1;
    }


    @EventTarget
    public void onPacketReceive(EventPacket packetEvent) {
        if (noSandDamage.getValueState()) {
            if (packetEvent.packet instanceof C03PacketPlayer) {
                if (this.isInsideBlock()) {
                    packetEvent.setCancelled(true);
                }
            }
        }
        if (noWaterDamage.getValueState()) {
            if (packetEvent.packet instanceof C03PacketPlayer) {
                if ((PlayerUtil.isInWater() && Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))) {
                    packetEvent.setCancelled(true);
                }
            }
        }

        if (LightningCheck.getValueState()) {
            if (packetEvent.packet instanceof S2CPacketSpawnGlobalEntity) {
                S2CPacketSpawnGlobalEntity packetIn = (S2CPacketSpawnGlobalEntity) packetEvent.packet;
                if (packetIn.func_149053_g() == 1) {
                    x = (int) ((double) packetIn.func_149051_d() / 32.0D);
                    y = (int) ((double) packetIn.func_149050_e() / 32.0D);
                    z = (int) ((double) packetIn.func_149049_f() / 32.0D);
                    PlayerUtil.tellPlayer("Found Lightning X:" + x + "-Y:" + y + "-Z:" + z);
                }
            }
        }
    }

    @Override
    public void onEnable() {
        if (noWaterDamage.getValueState()) {
            PlayerUtil.tellPlayer("Lshift In Water To Enable Water GodMode");
        }
        if (autojump.getValue())
            PlayerUtil.debug("LAlt to Toggle Stuck Jump");

        sneakTimer.reset();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        if (lessPacket.getValueState()) {
            if (mc.thePlayer.capabilities.isCreativeMode) {
                mc.thePlayer.setGameType(WorldSettings.GameType.CREATIVE);
            } else {
                mc.thePlayer.setGameType(WorldSettings.GameType.SURVIVAL);
            }
        }
        sneakTimer.reset();
        super.onDisable();
    }

    private boolean isInsideBlock() {
        for (int x = MathHelper.floor_double(Phase.mc.thePlayer.getEntityBoundingBox().minX); x < MathHelper
                .floor_double(Phase.mc.thePlayer.getEntityBoundingBox().maxX) + 1; ++x) {
            for (int y = MathHelper.floor_double(Phase.mc.thePlayer.getEntityBoundingBox().minY); y < MathHelper
                    .floor_double(Phase.mc.thePlayer.getEntityBoundingBox().maxY) + 1; ++y) {
                for (int z = MathHelper.floor_double(Phase.mc.thePlayer.getEntityBoundingBox().minZ); z < MathHelper
                        .floor_double(Phase.mc.thePlayer.getEntityBoundingBox().maxZ) + 1; ++z) {
                    final Block block = Phase.mc.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();
                    if (block != null && !(block instanceof BlockAir) && (block.isFullBlock())) {
                        AxisAlignedBB boundingBox = block.getCollisionBoundingBox(Phase.mc.theWorld,
                                new BlockPos(x, y, z), Phase.mc.theWorld.getBlockState(new BlockPos(x, y, z)));
                        if (block instanceof BlockHopper) {
                            boundingBox = new AxisAlignedBB(x, y, z, x + 1, y + 1, z + 1);
                        }
                        if (boundingBox != null && Phase.mc.thePlayer.getEntityBoundingBox().intersectsWith(boundingBox)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public void bestTool(int x, int y, int z) {
        int blockId = Block.getIdFromBlock(mc.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock());
        int bestSlot = 0;
        float f = -1F;
        for (int i1 = 36; i1 < 45; i1++)
            try {
                ItemStack curSlot = mc.thePlayer.inventoryContainer.getSlot(i1).getStack();
                if (((curSlot.getItem() instanceof ItemTool) || (curSlot.getItem() instanceof ItemSword)
                        || (curSlot.getItem() instanceof ItemShears))
                        && curSlot.getStrVsBlock(Block.getBlockById(blockId)) > f) {
                    bestSlot = i1 - 36;
                    f = curSlot.getStrVsBlock(Block.getBlockById(blockId));
                }
            } catch (Exception ignored) {
            }

        if (f != -1F) {
            mc.thePlayer.inventory.currentItem = bestSlot;
            mc.playerController.updateController();
        }
    }
}
