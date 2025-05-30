package fr.iglee42.cmr.ponder;

import com.simibubi.create.AllItems;
import com.simibubi.create.content.fluids.FluidFX;
import com.simibubi.create.content.fluids.pump.PumpBlock;
import com.simibubi.create.content.kinetics.deployer.DeployerBlockEntity;

import com.simibubi.create.foundation.fluid.FluidHelper;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import fr.iglee42.cmr.autosmithing.SmithingPressBlockEntity;
import fr.iglee42.cmr.blockspout.BlockSpoutBlockEntity;
import fr.iglee42.cmr.init.CMRRegistries;
import fr.iglee42.cmr.cooler.SnowmanCoolerBlock;
import net.createmod.catnip.math.Pointing;
import net.createmod.catnip.math.VecHelper;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.WorldSectionElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.createmod.ponder.foundation.element.InputWindowElement;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidStack;

public class CustomPonderScenes {

    public static void emptySnowmanCooler(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("empty_snowman_cooler", "Using Empty Snowman Coolers");
        scene.configureBasePlate(0, 0, 5);
        scene.world().showSection(util.select().layer(0), Direction.UP);
        scene.idle(10);
        BlockPos center = util.grid().at(2, 0, 2);

        scene.world().createEntity(w -> {
            SnowGolem snowmanEntity = EntityType.SNOW_GOLEM.create(w);
            Vec3 v = util.vector().topOf(center);
            snowmanEntity.setPosRaw(v.x, v.y, v.z);
            snowmanEntity.setYRot(snowmanEntity.yRotO = 180);
            snowmanEntity.setYHeadRot(snowmanEntity.yHeadRotO = 180);
            return snowmanEntity;
        });

        scene.idle(20);
        scene.overlay()
                .showControls(util.vector().centerOf(center.above(2)).add(0,0.5,0), Pointing.DOWN,60).rightClick()
                        .withItem(CMRRegistries.EMPTY_SNOWMAN_COOLER.asStack());
        scene.idle(10);
        scene.overlay().showText(60)
                .text("Right-click a Snowman with the empty cooler to capture it")
                .attachKeyFrame()
                .pointAt(util.vector().blockSurface(center.above(2), Direction.WEST))
                .placeNearTarget();
        scene.idle(60);

        scene.world().modifyEntities(SnowGolem.class, Entity::discard);
        scene.idle(20);

        scene.world().showSection(util.select().position(2, 1, 2), Direction.DOWN);
        scene.idle(20);
        scene.overlay().showControls(util.vector().topOf(center.above()), Pointing.DOWN,40).rightClick()
                .withItem(CMRRegistries.EMPTY_SNOWMAN_COOLER.asStack());
        scene.idle(10);
        scene.overlay().showText(60)
                .text("Alternatively, Snowmen can be collected from their Spawners directly")
                .attachKeyFrame()
                .pointAt(util.vector().blockSurface(center.above(), Direction.WEST))
                .placeNearTarget();
        scene.idle(50);
        scene.world().hideSection(util.select().position(2, 1, 2), Direction.UP);
        scene.idle(20);
        scene.world().setBlock(util.grid().at(2,1,2),CMRRegistries.SNOWMAN_COOLER.getDefaultState(),false);
        scene.world().showSection(util.select().position(2, 1, 2), Direction.DOWN);
        scene.idle(20);

        scene.world().modifyBlock(util.grid().at(2, 1, 2), s -> s.setValue(SnowmanCoolerBlock.HEAT_LEVEL, SnowmanCoolerBlock.HeatLevel.IDLE),
                false);
        scene.overlay().showText(70)
                .text("You now have an ideal cold source for various machines")
                .attachKeyFrame()
                .pointAt(util.vector().blockSurface(center.west()
                        .above(), Direction.WEST))
                .placeNearTarget();
        scene.idle(70);
    }

    public static void snowmanCooler(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("snowman_cooler", "Feeding Snowman Coolers");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        scene.idle(10);

        BlockPos cooler = util.grid().at(2, 1, 2);
        scene.world().showSection(util.select().position(cooler), Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(util.select().position(cooler.above()), Direction.DOWN);
        scene.idle(10);

        scene.overlay().showText(70)
                .attachKeyFrame()
                .text("Snowman Coolers can provide Cold to Items processed in a Basin")
                .pointAt(util.vector().blockSurface(cooler, Direction.WEST))
                .placeNearTarget();
        scene.idle(80);

        scene.world().hideSection(util.select().position(cooler.above()), Direction.UP);
        scene.idle(20);
        scene.world().setBlock(cooler.above(), Blocks.AIR.defaultBlockState(), false);
        scene.overlay().showControls(util.vector().topOf(cooler), Pointing.DOWN,15).rightClick()
                .withItem(new ItemStack(Items.SNOWBALL));
        scene.idle(7);
        scene.world().modifyBlock(cooler, s -> s.setValue(SnowmanCoolerBlock.HEAT_LEVEL, SnowmanCoolerBlock.HeatLevel.COOLING), false);
        scene.idle(20);

        scene.overlay().showText(70)
                .attachKeyFrame()
                .text("For this, the Snowman has to be fed with cold items")
                .pointAt(util.vector().blockSurface(cooler, Direction.WEST))
                .placeNearTarget();
        scene.idle(80);

        scene.idle(20);
        scene.overlay().showControls(util.vector().topOf(cooler), Pointing.DOWN, 30).withItem(CMRRegistries.FROZEN_CAKE.asStack()).rightClick();
        scene.idle(7);
        scene.world().modifyBlock(cooler, s -> s.setValue(SnowmanCoolerBlock.HEAT_LEVEL, SnowmanCoolerBlock.HeatLevel.FREEZING), false);
        scene.idle(20);

        scene.overlay().showText(80)
                .attachKeyFrame()
                .colored(PonderPalette.MEDIUM)
                .text("With a Snow Cake, the Cooler can reach an even stronger level of cold")
                .pointAt(util.vector().blockSurface(cooler, Direction.WEST))
                .placeNearTarget();
        scene.idle(90);

        Class<DeployerBlockEntity> teType = DeployerBlockEntity.class;
        scene.world().modifyBlockEntityNBT(util.select().position(4, 1, 2), teType,
                nbt -> nbt.put("HeldItem", CMRRegistries.FROZEN_CAKE.asStack()
                        .save(new CompoundTag())));

        scene.world().showSection(util.select().fromTo(3, 0, 5, 2, 0, 5), Direction.UP);
        scene.idle(5);
        scene.world().showSection(util.select().fromTo(4, 1, 2, 4, 1, 5), Direction.DOWN);
        scene.idle(5);
        scene.world().showSection(util.select().fromTo(2, 1, 4, 2, 1, 5), Direction.DOWN);
        scene.idle(10);

        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("The feeding process can be automated using Deployers or Mechanical Arms")
                .pointAt(util.vector().blockSurface(cooler.east(2), Direction.UP));
        scene.idle(90);
    }


    public static void smithing(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("smithing", "Processing Items with the Smithing Press");
        scene.configureBasePlate(0, 0, 5);
        scene.world().showSection(util.select().layer(0), Direction.UP);
        scene.idle(5);

        ElementLink<WorldSectionElement> depot =
                scene.world().showIndependentSection(util.select().position(2, 1, 1), Direction.DOWN);
        scene.world().moveSection(depot, util.vector().of(0, 0, 1), 0);
        scene.idle(10);

        Selection pressS = util.select().position(2, 3, 2);
        BlockPos pressPos = util.grid().at(2, 3, 2);
        BlockPos depotPos = util.grid().at(2, 1, 1);
        scene.world().setKineticSpeed(pressS, 0);
        scene.world().showSection(pressS, Direction.DOWN);
        scene.idle(10);

        scene.world().showSection(util.select().fromTo(2, 1, 3, 2, 1, 5), Direction.NORTH);
        scene.idle(3);
        scene.world().showSection(util.select().position(2, 2, 3), Direction.SOUTH);
        scene.idle(3);
        scene.world().showSection(util.select().position(2, 3, 3), Direction.NORTH);
        scene.world().setKineticSpeed(pressS, -32);
        scene.effects().indicateSuccess(pressPos);
        scene.idle(10);

        Vec3 pressSide = util.vector().blockSurface(pressPos, Direction.WEST);
        scene.overlay().showText(60)
                .pointAt(pressSide)
                .placeNearTarget()
                .attachKeyFrame()
                .text("The Smithing Press can process items provided beneath it");
        scene.idle(70);
        scene.overlay().showText(60)
                .pointAt(pressSide.subtract(0, 2, 0))
                .placeNearTarget()
                .attachKeyFrame()
                .text("The Input item can be placed on a Depot under the Press");
        scene.idle(50);
        ItemStack chestplate = new ItemStack(Items.DIAMOND_CHESTPLATE);
        scene.world().createItemOnBeltLike(depotPos, Direction.NORTH, chestplate);
        Vec3 depotCenter = util.vector().centerOf(depotPos.south());
        scene.overlay().showControls(depotCenter, Pointing.UP, 30).withItem(chestplate);
        scene.idle(70);
        Class<SmithingPressBlockEntity> type = SmithingPressBlockEntity.class;

        scene.overlay().showText(60)
                .pointAt(pressSide)
                .placeNearTarget()
                .attachKeyFrame()
                .text("The Template can be placed by right clicking the top or the bottom of the Press");
        scene.idle(50);
        ItemStack template = new ItemStack(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE);
        scene.world().modifyBlockEntity(pressPos, type, pte -> pte.getInvForSide(Direction.UP)
                .setStackInSlot(0,template));
        Vec3 pressCenter = util.vector().centerOf(pressPos.above());
        scene.overlay().showControls(pressCenter, Pointing.DOWN, 30).withItem(template);
        scene.idle(70);

        scene.overlay().showText(60)
                .pointAt(pressSide)
                .placeNearTarget()
                .attachKeyFrame()
                .text("The Addition can be placed by right clicking the sides of the Press");
        scene.idle(50);
        ItemStack addition = new ItemStack(Items.NETHERITE_INGOT);
        scene.world().modifyBlockEntity(pressPos, type, pte -> pte.getInvForSide(Direction.EAST)
                .setStackInSlot(0,addition));
        scene.overlay().showControls(pressCenter.subtract(0,1,-1), Pointing.LEFT, 30).withItem(addition);
        scene.idle(10);

        scene.world().modifyBlockEntity(pressPos, type, pte -> pte.getSmithingBehaviour()
                .start());
        scene.idle(30);
        scene.world().modifyBlockEntity(pressPos, type, pte -> pte.getSmithingBehaviour()
                .makePressingParticleEffect(depotCenter.add(0, 8 / 16f, 0), chestplate));
        scene.world().modifyBlockEntity(pressPos, type, pte -> pte.getInvForSide(Direction.UP).setStackInSlot(0,ItemStack.EMPTY));
        scene.world().modifyBlockEntity(pressPos, type, pte -> pte.getInvForSide(Direction.EAST).setStackInSlot(0,ItemStack.EMPTY));
        scene.world().removeItemsFromBelt(depotPos);
        ItemStack chestplateOut = new ItemStack(Items.NETHERITE_CHESTPLATE);
        scene.world().createItemOnBeltLike(depotPos, Direction.UP, chestplateOut);
        scene.idle(10);
        scene.overlay().showControls(depotCenter, Pointing.UP, 50).withItem(chestplateOut);
        scene.idle(60);
    }

    public static void blockSpout(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        RandomSource random = RandomSource.create();

        scene.title("block_spout", "Filling Blocks using a Block Spout");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        scene.idle(5);
        BlockPos grassPos = util.grid().at(2, 1, 2);

        scene.world().showSection(util.select().position(grassPos), Direction.DOWN);
        scene.idle(10);

        scene.world().modifyBlock(util.grid().at(2, 3, 3), s -> s.setValue(PumpBlock.FACING, Direction.NORTH), false);

        Selection largeCog = util.select().position(3, 0, 5);
        Selection kinetics = util.select().fromTo(2, 1, 5, 2, 2, 3);
        Selection tank = util.select().fromTo(1, 1, 4, 1, 2, 4);
        Selection pipes = util.select().fromTo(1, 3, 4, 2, 3, 3);

        Selection spoutS = util.select().position(2, 3, 2);
        BlockPos spoutPos = util.grid().at(2, 3, 2);
        scene.world().showSection(spoutS, Direction.DOWN);
        scene.idle(10);

        Vec3 spoutSide = util.vector().blockSurface(spoutPos, Direction.WEST);
        scene.overlay().showText(60)
                .pointAt(spoutSide)
                .placeNearTarget()
                .attachKeyFrame()
                .text("The Block Spout can modify block beneath it with fluid");

        scene.idle(50);

        scene.world().showSection(tank, Direction.DOWN);
        scene.idle(5);
        scene.world().showSection(largeCog, Direction.UP);
        scene.world().showSection(kinetics, Direction.NORTH);
        scene.world().showSection(pipes, Direction.NORTH);

        scene.idle(20);
        FluidStack honey = new FluidStack(FluidHelper.convertToStill(Fluids.WATER), 1000);
        ItemStack bucket = Fluids.WATER
                .getFluidType()
                .getBucket(honey);
        scene.overlay().showControls(util.vector().blockSurface(util.grid().at(2, 3, 2), Direction.NORTH), Pointing.RIGHT, 40)
                .showing(AllIcons.I_MTD_CLOSE)
                .withItem(bucket);
        scene.idle(7);
        scene.overlay().showOutlineWithText(util.select().position(2, 3, 2), 50)
                .pointAt(util.vector().blockSurface(util.grid().at(2, 3, 2), Direction.WEST))
                .attachKeyFrame()
                .colored(PonderPalette.RED)
                .placeNearTarget()
                .text("The content of a Block Spout cannot be accessed manually");
        scene.idle(60);
        scene.overlay().showText(70)
                .pointAt(util.vector().blockSurface(util.grid().at(2, 3, 3), Direction.WEST))
                .colored(PonderPalette.GREEN)
                .placeNearTarget()
                .text("Instead, Pipes can be used to supply it with fluids");

        scene.idle(90);
        scene.overlay().showText(60)
                .pointAt(spoutSide.subtract(0, 2, 0))
                .attachKeyFrame()
                .placeNearTarget()
                .text("The Input block can be placed under the Block Spout");
        scene.idle(50);

        scene.idle(20);
        for (int i = 101; i > 0; i-- ){
            int finalI = i;
            scene.world().modifyBlockEntityNBT(spoutS, BlockSpoutBlockEntity.class, nbt -> nbt.putInt("ProcessingTicks", finalI));
            ParticleOptions particle = new BlockParticleOption(ParticleTypes.BLOCK,Fluids.WATER.defaultFluidState().createLegacyBlock());
            Vec3 m = VecHelper.offsetRandomly(Vec3.ZERO, builder.getScene().getWorld().random, 1.5f);
            m = new Vec3(m.x, Math.abs(m.y), m.z);
            scene.effects().emitParticles(Vec3.atCenterOf(grassPos),scene.effects().simpleParticleEmitter(particle,m), 2, 1);
            scene.idle(1);
        }
        scene.world().modifyBlockEntityNBT(spoutS, BlockSpoutBlockEntity.class, nbt -> nbt.putInt("ProcessingTicks", -1));
        scene.world().setBlock(grassPos,Blocks.GRASS_BLOCK.defaultBlockState(), true);
        ParticleOptions fluidParticle = FluidFX.getFluidParticle(new FluidStack(Fluids.WATER, 1000));
        for (int i = 0; i < 10; i++) {
            scene.effects().emitParticles(util.vector().topOf(grassPos.south())
                            .add(0, 1 / 16f, 0),
                    scene.effects().simpleParticleEmitter(fluidParticle, VecHelper.offsetRandomly(Vec3.ZERO, random, .1f)), 1, 1);
        }
        scene.idle(10);
    }
}
