package fr.iglee42.cmr.ponder;

import com.simibubi.create.AllItems;
import com.simibubi.create.content.kinetics.deployer.DeployerBlockEntity;
import com.simibubi.create.foundation.ponder.PonderPalette;
import com.simibubi.create.foundation.ponder.SceneBuilder;
import com.simibubi.create.foundation.ponder.SceneBuildingUtil;
import com.simibubi.create.foundation.ponder.element.InputWindowElement;
import com.simibubi.create.foundation.utility.Pointing;
import fr.iglee42.cmr.CMRRegistries;
import fr.iglee42.cmr.cooler.SnowmanCoolerBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

public class CustomPonderScenes {

    public static void emptySnowmanCooler(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("empty_snowman_cooler", "Using Empty Snowman Coolers");
        scene.configureBasePlate(0, 0, 5);
        scene.world.showSection(util.select.layer(0), Direction.UP);
        scene.idle(10);
        BlockPos center = util.grid.at(2, 0, 2);

        scene.world.createEntity(w -> {
            SnowGolem snowmanEntity = EntityType.SNOW_GOLEM.create(w);
            Vec3 v = util.vector.topOf(center);
            snowmanEntity.setPosRaw(v.x, v.y, v.z);
            snowmanEntity.setYRot(snowmanEntity.yRotO = 180);
            snowmanEntity.setYHeadRot(snowmanEntity.yHeadRotO = 180);
            return snowmanEntity;
        });

        scene.idle(20);
        scene.overlay
                .showControls(new InputWindowElement(util.vector.centerOf(center.above(2)).add(0,0.5,0), Pointing.DOWN).rightClick()
                        .withItem(CMRRegistries.EMPTY_SNOWMAN_COOLER.asStack()), 60);
        scene.idle(10);
        scene.overlay.showText(60)
                .text("Right-click a Snowman with the empty cooler to capture it")
                .attachKeyFrame()
                .pointAt(util.vector.blockSurface(center.above(2), Direction.WEST))
                .placeNearTarget();
        scene.idle(60);

        scene.world.modifyEntities(SnowGolem.class, Entity::discard);
        scene.idle(20);

        scene.world.showSection(util.select.position(2, 1, 2), Direction.DOWN);
        scene.idle(20);
        scene.overlay.showControls(new InputWindowElement(util.vector.topOf(center.above()), Pointing.DOWN).rightClick()
                .withItem(CMRRegistries.EMPTY_SNOWMAN_COOLER.asStack()), 40);
        scene.idle(10);
        scene.overlay.showText(60)
                .text("Alternatively, Snowmen can be collected from their Spawners directly")
                .attachKeyFrame()
                .pointAt(util.vector.blockSurface(center.above(), Direction.WEST))
                .placeNearTarget();
        scene.idle(50);
        scene.world.hideSection(util.select.position(2, 1, 2), Direction.UP);
        scene.idle(20);
        scene.world.setBlock(util.grid.at(2,1,2),CMRRegistries.SNOWMAN_COOLER.getDefaultState(),false);
        scene.world.showSection(util.select.position(2, 1, 2), Direction.DOWN);
        scene.idle(20);

        scene.world.modifyBlock(util.grid.at(2, 1, 2), s -> s.setValue(SnowmanCoolerBlock.HEAT_LEVEL, SnowmanCoolerBlock.HeatLevel.IDLE),
                false);
        scene.overlay.showText(70)
                .text("You now have an ideal cold source for various machines")
                .attachKeyFrame()
                .pointAt(util.vector.blockSurface(center.west()
                        .above(), Direction.WEST))
                .placeNearTarget();
        scene.idle(70);
    }

    public static void snowmanCooler(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("snowman_cooler", "Feeding Snowman Coolers");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        scene.idle(10);

        BlockPos cooler = util.grid.at(2, 1, 2);
        scene.world.showSection(util.select.position(cooler), Direction.DOWN);
        scene.idle(10);
        scene.world.showSection(util.select.position(cooler.above()), Direction.DOWN);
        scene.idle(10);

        scene.overlay.showText(70)
                .attachKeyFrame()
                .text("Snowman Coolers can provide Cold to Items processed in a Basin")
                .pointAt(util.vector.blockSurface(cooler, Direction.WEST))
                .placeNearTarget();
        scene.idle(80);

        scene.world.hideSection(util.select.position(cooler.above()), Direction.UP);
        scene.idle(20);
        scene.world.setBlock(cooler.above(), Blocks.AIR.defaultBlockState(), false);
        scene.overlay.showControls(new InputWindowElement(util.vector.topOf(cooler), Pointing.DOWN).rightClick()
                .withItem(new ItemStack(Items.SNOWBALL)), 15);
        scene.idle(7);
        scene.world.modifyBlock(cooler, s -> s.setValue(SnowmanCoolerBlock.HEAT_LEVEL, SnowmanCoolerBlock.HeatLevel.COOLING), false);
        scene.idle(20);

        scene.overlay.showText(70)
                .attachKeyFrame()
                .text("For this, the Snowman has to be fed with cold items")
                .pointAt(util.vector.blockSurface(cooler, Direction.WEST))
                .placeNearTarget();
        scene.idle(80);

        scene.idle(20);
        scene.overlay.showControls(new InputWindowElement(util.vector.topOf(cooler), Pointing.DOWN).rightClick()
                .withItem(AllItems.BLAZE_CAKE.asStack()), 30);
        scene.idle(7);
        scene.world.modifyBlock(cooler, s -> s.setValue(SnowmanCoolerBlock.HEAT_LEVEL, SnowmanCoolerBlock.HeatLevel.FREEZING), false);
        scene.idle(20);

        scene.overlay.showText(80)
                .attachKeyFrame()
                .colored(PonderPalette.MEDIUM)
                .text("With a Snow Cake, the Cooler can reach an even stronger level of cold")
                .pointAt(util.vector.blockSurface(cooler, Direction.WEST))
                .placeNearTarget();
        scene.idle(90);

        Class<DeployerBlockEntity> teType = DeployerBlockEntity.class;
        scene.world.modifyBlockEntityNBT(util.select.position(4, 1, 2), teType,
                nbt -> nbt.put("HeldItem", AllItems.BLAZE_CAKE.asStack()
                        .serializeNBT()));

        scene.world.showSection(util.select.fromTo(3, 0, 5, 2, 0, 5), Direction.UP);
        scene.idle(5);
        scene.world.showSection(util.select.fromTo(4, 1, 2, 4, 1, 5), Direction.DOWN);
        scene.idle(5);
        scene.world.showSection(util.select.fromTo(2, 1, 4, 2, 1, 5), Direction.DOWN);
        scene.idle(10);

        scene.overlay.showText(80)
                .attachKeyFrame()
                .text("The feeding process can be automated using Deployers or Mechanical Arms")
                .pointAt(util.vector.blockSurface(cooler.east(2), Direction.UP));
        scene.idle(90);
    }
}
