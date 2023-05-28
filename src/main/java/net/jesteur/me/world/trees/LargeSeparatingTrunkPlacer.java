package net.jesteur.me.world.trees;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.command.PlaceCommand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.trunk.TrunkPlacer;
import net.minecraft.world.gen.trunk.TrunkPlacerType;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;

public class LargeSeparatingTrunkPlacer extends TrunkPlacer {

    public static final Codec<LargeSeparatingTrunkPlacer> CODEC = RecordCodecBuilder.create(instance ->
            fillTrunkPlacerFields(instance).apply(instance, LargeSeparatingTrunkPlacer::new));

    public LargeSeparatingTrunkPlacer(int baseHeight, int firstRandomHeight, int secondRandomHeight) {
        super(baseHeight, firstRandomHeight, secondRandomHeight);
    }
    @Override
    protected TrunkPlacerType<?> getType() {
        return TrunkPlacerType.UPWARDS_BRANCHING_TRUNK_PLACER;
    }

    public record Branch(Vec3d start, Vec3d dir, float size, float len) {

    }
    @Override
    public List<FoliagePlacer.TreeNode> generate(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, int height, BlockPos startPos, TreeFeatureConfig config) {

        setToDirt(world, replacer, random, startPos, config);

        int trunkHeight = baseHeight + random.nextInt(firstRandomHeight);
        int width = trunkHeight / 12 + 2;
        int cWidth = -2;

        int currentHeight = 0;

        List<Branch> branches = new LinkedList<>();

        int sub = 0;

        while (currentHeight < trunkHeight) {
            float progress = currentHeight / (float) trunkHeight;
            int w = (int)(width + cWidth * progress);

            for (int x = -w; x < w; x++) {
                for (int z = -w; z < w; z++) {
                    var pos = startPos.add(x, currentHeight, z);
                    getAndSetState(world, replacer, random, pos, config);
                }
            }

            if (progress > 0.80) {
                for (int i = 0; i < 6; i++) {
                    branches.add(new Branch(startPos.up(currentHeight).toCenterPos(),
                            new Vec3d((random.nextFloat() - 0.5) * 2, random.nextFloat() * 2.0, (random.nextFloat() - 0.5) * 2),
                            0, (int) (w * 0.8)));
                }
            } else if (progress > 0.2 && progress < 0.4 && sub < 2 && random.nextFloat() > 0.75) {
                sub++;
                branches.add(new Branch(startPos.up(currentHeight).toCenterPos(),
                        new Vec3d((random.nextFloat() - 0.5), random.nextFloat() * 0.2 + 0.2, (random.nextFloat() - 0.5)),
                        0.82f, (int) (w * 1.5)));
            }


            this.getAndSetState(world, replacer, random, startPos, config);

            currentHeight += 1;
        }

        List<FoliagePlacer.TreeNode> bushes = new LinkedList<>();

        while (!branches.isEmpty()) {
            Branch start = branches.remove(0);
            System.out.println(branches.size());
            var add = start.dir.normalize();
            var pos = start.start;
            float s = start.size;
            float addW = (1 / start.len)/16;

            int i = 0;
            for (; s < 1.0 ; s+= addW) {
                pos = pos.add(add);
                float w = (int)(start.len*(1-s));

                for (int x = (int)-w; x <= w; x++) {
                    for (int z = (int) -w; z <= w; z++) {
                        for (int y = (int) -w; y <= w; y++) {
                            var pPos = pos.add(x, y, z);
                            getAndSetState(world, replacer, random, new BlockPos((int) pPos.x, (int) pPos.y, (int) pPos.z), config);
                        }
                    }
                }

                if (i > 2 && s < 0.9 && s > 0.3 && random.nextFloat() > 0.8) {
                    branches.add(new Branch(pos,
                            start.dir.add((random.nextFloat() - 0.5), (random.nextFloat()) * 0.5 + 0.2, (random.nextFloat() - 0.5)),
                            (s + random.nextFloat() * 0.5f), start.len));
                }

                i++;
            }

            bushes.add(new FoliagePlacer.TreeNode(new BlockPos((int) pos.x, (int) pos.y + 2, (int) pos.z),  1, false));

        }

        return bushes;
    }

}
