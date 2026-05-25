package com.ultramega.cabletiers.neoforge.storage.diskinterface;

import com.ultramega.cabletiers.common.CableTiers;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;
import org.joml.Vector3f;

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createIdentifier;
import static com.ultramega.cabletiers.common.utils.CableTiersIdentifierUtil.createCableTiersIdentifier;

public final class TieredDiskInterfaceRenderingProperties {
    static final Identifier INACTIVE_LED_MODEL = createIdentifier("block/disk/led/inactive");
    static final int DISKS = 6;
    static final Vector3f[] TRANSLATIONS = new Vector3f[DISKS];

    static {
        for (int idx = 0; idx < DISKS; ++idx) {
            TRANSLATIONS[idx] = translationAt(idx);
        }
    }

    private TieredDiskInterfaceRenderingProperties() {
    }

    static Identifier getActiveBaseModel(final CableTiers tier, final DyeColor color) {
        return createCableTiersIdentifier("block/" + tier.getLowercaseName() + "_disk_interface/" + color);
    }

    static Identifier getInactiveBaseModel(final CableTiers tier) {
        return createCableTiersIdentifier("block/" + tier.getLowercaseName() + "_disk_interface/inactive");
    }

    private static Vector3f translationAt(final int idx) {
        final int x = idx < 3 ? 0 : 1;
        final int y = idx % 3;
        return new Vector3f(
            x == 0 ? -(2F / 16F) : -(9F / 16F),
            -((y * 3F) / 16F) - (6F / 16F),
            0
        );
    }
}
