package cn.hanabi.injection.interfaces;

import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;

public interface IEntity {
    int getNextStepDistance();

    void setNextStepDistance(int distance);

    int getFire();

    void setFire(int i);

    AxisAlignedBB getBoundingBox();


    boolean canEntityBeSeenFixed(Entity entityIn);

}
