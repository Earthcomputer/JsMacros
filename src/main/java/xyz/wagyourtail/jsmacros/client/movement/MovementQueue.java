package xyz.wagyourtail.jsmacros.client.movement;

import net.minecraft.client.entity.EntityPlayerSP;
import xyz.wagyourtail.jsmacros.client.api.classes.Draw3D;
import xyz.wagyourtail.jsmacros.client.api.classes.PlayerInput;
import xyz.wagyourtail.jsmacros.client.api.library.impl.FHud;
import xyz.wagyourtail.jsmacros.client.api.sharedclasses.PositionCommon;

import java.util.ArrayList;
import java.util.List;

import static xyz.wagyourtail.jsmacros.client.JsMacros.LOGGER;

public class MovementQueue {
    private static final List<PlayerInput> queue = new ArrayList<>();
    private static final List<PositionCommon.Pos3D> predictions = new ArrayList<>();
    public static Draw3D predPoints = new Draw3D();
    private static EntityPlayerSP player;
    private static int queuePos = 0;
    private static boolean reCalcPredictions;

    public static PlayerInput tick(EntityPlayerSP newPlayer) {
        if (queuePos == queue.size()) {
            return null;
        }

        player = newPlayer;

        PositionCommon.Vec3D diff;
        if (predictions.size() > queuePos) {
            diff = new PositionCommon.Vec3D(player.posX, player.posY, player.posZ, predictions.get(0).x, predictions.get(0).y, predictions.get(0).z);
            if (diff.getMagnitude() > 0.01D) {
                LOGGER.debug("Pred of by x={}, y={}, z={}", diff.x1 - diff.x2, diff.y1 - diff.y2, diff.z1 - diff.z2);
                LOGGER.debug("Player pos x={}, y={}, z={}", player.posX, player.posY, player.posZ);
                predPoints.addPoint(player.posX, player.posY, player.posZ, 0.02, 0xde070a);
                reCalcPredictions = true;
            } else {
                LOGGER.debug("No Diff");
                predictions.remove(0);
            }
        } else {
            LOGGER.debug("No Pred");
            reCalcPredictions = true;
        }

        if (reCalcPredictions) {
            calcPredictions();
            drawPredictions();
            reCalcPredictions = false;
        }

        if (predictions.size() > 0)
            LOGGER.debug("Predic pos x={}, y={}, z={}", predictions.get(0).getX(), predictions.get(0).getY(), predictions.get(0).getZ());

        queuePos++;
        return queue.get(queuePos - 1);
    }

    private static void calcPredictions() {
        List<PlayerInput> toCalc = new ArrayList<>(queue.subList(queuePos, queue.size()));
        predictions.clear();
        MovementDummy dummy = new MovementDummy(player);
        for (PlayerInput input : toCalc) {
            predictions.add(dummy.applyInput(input));
        }
    }

    private static void drawPredictions() {
        predictions.forEach(point -> predPoints.addPoint(new PositionCommon.Pos3D(point.getX(), point.getY(), point.getZ()), 0.01, 0xffd000));
    }

    public static void append(PlayerInput input, EntityPlayerSP newPlayer) {
        reCalcPredictions = true;
        player = newPlayer;
        // We do the clone step here, since somewhere one could maybe change the input
        // and we don't want that to affect us.
        queue.add(input.clone());
    }

    public static void clear() {
        queue.clear();
        predictions.clear();
        if (FHud.renders.contains(predPoints)) {
            synchronized (FHud.renders) {
                FHud.renders.remove(predPoints);
            }
        }
        predPoints = new Draw3D();
        synchronized (FHud.renders) {
            FHud.renders.add(predPoints);
        }
        queuePos = 0;
    }
}
