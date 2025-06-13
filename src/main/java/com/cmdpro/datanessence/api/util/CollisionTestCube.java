package com.cmdpro.datanessence.api.util;

import com.mojang.math.Constants;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;
import java.util.function.Predicate;

public class CollisionTestCube {
    public Vector3f center;
    public Vector3f size;
    public Quaternionf rotation;

    public CollisionTestCube(AABB aabb) {
        this(
                aabb,
                new Quaternionf()
        );
    }
    public CollisionTestCube(AABB aabb, Quaternionf rotation) {
        this(
                aabb.getCenter().toVector3f(),
                new Vec3(aabb.getXsize(), aabb.getYsize(), aabb.getZsize()).toVector3f(),
                rotation
        );
    }
    public CollisionTestCube(
            Vector3f center,
            Vector3f size,
            Quaternionf rotation
    ) {
        this.center = center;
        this.size = size;
        this.rotation = rotation;
    }

    public <T extends Entity> List<T> getEntitiesOfClass(Class<T> entityClass, Level level) {
        return getEntitiesOfClass(entityClass, level, false, EntitySelector.NO_SPECTATORS);
    }
    public <T extends Entity> List<T> getEntitiesOfClass(Class<T> entityClass, Level level, boolean alignEntityHitboxes) {
        return getEntitiesOfClass(entityClass, level, alignEntityHitboxes, EntitySelector.NO_SPECTATORS);
    }
    public <T extends Entity> List<T> getEntitiesOfClass(Class<T> entityClass, Level level, boolean alignEntityHitboxes, Predicate<? super T> filter) {
        return level.getEntitiesOfClass(entityClass, AABB.ofSize(new Vec3(center.x, center.y, center.z), size.x*2, size.y*2, size.z*2), (entity) -> {
            if (filter.test(entity)) {
                CollisionTestCube cube = new CollisionTestCube(entity.getBoundingBox(), alignEntityHitboxes ? new Quaternionf(rotation) : new Quaternionf());
                return cube.intersects(this);
            }
            return false;
        });
    }

    public boolean intersects(CollisionTestCube other) {
        Vector3f[] axesA = {
                new Vector3f(1f, 0f, 0f).rotate(rotation),
                new Vector3f(0f, 1f, 0f).rotate(rotation),
                new Vector3f(0f, 0f, 1f).rotate(rotation)
        };

        Vector3f[] axesB = {
                new Vector3f(1f, 0f, 0f).rotate(other.rotation),
                new Vector3f(0f, 1f, 0f).rotate(other.rotation),
                new Vector3f(0f, 0f, 1f).rotate(other.rotation)
        };

        for (Vector3f i : axesA) {
            float[] projA = this.project(i);
            float[] projB = other.project(i);

            if (projA[1] < projB[0] - Constants.EPSILON || projB[1] < projA[0] - Constants.EPSILON) {
                return false;
            }
        }

        for (Vector3f i : axesB) {
            float[] projA = this.project(i);
            float[] projB = other.project(i);

            if (projA[1] < projB[0] - Constants.EPSILON || projB[1] < projA[0] - Constants.EPSILON) {
                return false;
            }
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Vector3f axis = new Vector3f(axesA[i].cross(axesB[j]));

                if (axis.length() < Constants.EPSILON) {
                    continue;
                }

                axis.normalize();

                float[] projA = this.project(axis);
                float[] projB = other.project(axis);

                if (projA[1] < projB[0] - Constants.EPSILON || projB[1] < projA[0] - Constants.EPSILON) {
                    return false;
                }
            }
        }

        return true;
    }
    public Vector3f getHalfSize() {
        return new Vector3f(size).mul(0.5f);
    }

    public float[] project(Vector3f axis) {
        Vector3f halfSize = getHalfSize();

        Vector3f axisX = new Vector3f(1, 0, 0).rotate(rotation);
        Vector3f axisY = new Vector3f(0, 1, 0).rotate(rotation);
        Vector3f axisZ = new Vector3f(0, 0, 1).rotate(rotation);

        float pX = Math.abs(halfSize.x * axisX.dot(axis));
        float pY = Math.abs(halfSize.y * axisY.dot(axis));
        float pZ = Math.abs(halfSize.z * axisZ.dot(axis));

        float r = pX + pY + pZ;

        float centerProjection = center.dot(axis);

        float minProj = centerProjection - r;
        float maxProj = centerProjection + r;

        return new float[]{minProj, maxProj};
    }
}
