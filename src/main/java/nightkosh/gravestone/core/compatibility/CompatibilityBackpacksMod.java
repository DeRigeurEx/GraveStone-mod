package nightkosh.gravestone.core.compatibility;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import nightkosh.gravestone.api.GraveStoneAPI;
import nightkosh.gravestone.config.Config;
import nightkosh.gravestone.core.logger.GSLogger;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * GraveStone mod
 *
 * @author NightKosh
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
public class CompatibilityBackpacksMod implements ICompatibility {

    public static final String MOD_ID = "backpack";

    protected CompatibilityBackpacksMod() {
        if (isModLoaded(MOD_ID) && Config.storeBackpacksItems) {
            GraveStoneAPI.graveGenerationAtDeath.addPlayerItemsHandler((player, source) -> {
                try {
                    List<ItemStack> items = new ArrayList<>();
                    Class<?> PlayerSaveClass = Class.forName("de.eydamos.backpack.data.PlayerSave");
                    if (PlayerSaveClass != null) {
                        Method loadPlayerMethod = PlayerSaveClass.getDeclaredMethod("loadPlayer", World.class, EntityPlayer.class);
                        if (loadPlayerMethod != null) {
                            Object playerSave = loadPlayerMethod.invoke(null, player.getEntityWorld(), player);

                            Method getBackpackMethod = playerSave.getClass().getDeclaredMethod("getBackpack");
                            Method removeStackFromSlotMethod = playerSave.getClass().getDeclaredMethod("removeStackFromSlot", int.class);

                            if (getBackpackMethod != null && removeStackFromSlotMethod != null) {
                                Object backpackObject = getBackpackMethod.invoke(playerSave);
                                if (backpackObject != null && backpackObject instanceof ItemStack && !((ItemStack) backpackObject).isEmpty()) {
                                    items.add(((ItemStack) backpackObject).copy());
                                    removeStackFromSlotMethod.invoke(playerSave, 0);
                                }
                            }
                        }
                    }
                    return items;
                } catch (Exception e) {
                    GSLogger.logError("Can't save Backpacks items!!!");
                    e.printStackTrace();
                    return null;
                }
            });
        }
    }
}
