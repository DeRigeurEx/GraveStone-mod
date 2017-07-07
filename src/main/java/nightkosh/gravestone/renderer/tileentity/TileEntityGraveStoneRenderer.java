package nightkosh.gravestone.renderer.tileentity;

import com.google.common.collect.Maps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderEntityItem;
import net.minecraft.client.renderer.texture.LayeredTexture;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nightkosh.gravestone.api.grave.EnumGraveType;
import nightkosh.gravestone.block.enums.EnumGraves;
import nightkosh.gravestone.config.Config;
import nightkosh.gravestone.core.Resources;
import nightkosh.gravestone.models.block.ModelGraveStone;
import nightkosh.gravestone.models.block.graves.*;
import nightkosh.gravestone.tileentity.TileEntityGraveStone;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;

/**
 * GraveStone mod
 *
 * @author NightKosh
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
@SideOnly(Side.CLIENT)
public class TileEntityGraveStoneRenderer extends TileEntityRenderer {

    private static final Map<EnumGraves, ResourceLocation> mossyTexturesMap = Maps.newHashMap();
    public static ModelGraveStone verticalPlate = new ModelVerticalPlateGraveStone();
    public static ModelGraveStone cross = new ModelCrossGraveStone();
    public static ModelGraveStone obelisk = new ModelObeliskGravestone();
    public static ModelGraveStone celticCross = new ModelCelticCrossGravestone();
    public static ModelGraveStone horizontalPlate = new ModelHorizontalPlateGraveStone();
    public static ModelGraveStone villagerStatue = new ModelVillagerStatueGravestone();
    public static ModelGraveStone dogStatue = new ModelDogStatueGraveStone();
    public static ModelGraveStone catStatue = new ModelCatStatueGraveStone();
    public static ModelGraveStone horseStatue = new ModelHorseGraveStone();
    public static ModelGraveStone creeperStatue = new ModelCreeperStatueGravestone();
    public static ModelGraveStone skeletonCorpse = new ModelSkeletonCorpseGravestone(false);
    public static ModelGraveStone witheredSkeletonCorpse = new ModelSkeletonCorpseGravestone(true);

    public static ModelGraveStone swordModel = new ModelSwordGraveStone();

    public static TileEntityGraveStoneRenderer instance;

    private static final TileEntityGraveStone GRAVE_TE = new TileEntityGraveStone();
    private static final ItemStack SWORD = new ItemStack(Items.IRON_SWORD);

    public static final Map<Item, EntityItem> flowersMap = new HashMap<>();
    public static final Map<Item, EntityItem> swordsMap = new HashMap<>();

    public static final Map<Item, ResourceLocation> swordsTextureMap = new HashMap<>();

    static {
        swordsTextureMap.put(Items.WOODEN_SWORD, Resources.WOODEN_SWORD);
        swordsTextureMap.put(Items.STONE_SWORD, Resources.STONE_SWORD);
        swordsTextureMap.put(Items.IRON_SWORD, Resources.IRON_SWORD);
        swordsTextureMap.put(Items.GOLDEN_SWORD, Resources.GOLDEN_SWORD);
        swordsTextureMap.put(Items.DIAMOND_SWORD, Resources.DIAMOND_SWORD);
    }

    static {
        GRAVE_TE.setGraveType(EnumGraves.STONE_VERTICAL_PLATE.ordinal());
    }

    public TileEntityGraveStoneRenderer() {
        instance = this;
    }

    @Override
    public void render(TileEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        TileEntityGraveStone tileEntity = (TileEntityGraveStone) te;
        boolean isSwordGrave;
        boolean isEnchanted;
        ItemStack sword;

        if (tileEntity == null) {
            tileEntity = getDefaultTE();
            isSwordGrave = isSwordGrave();
            isEnchanted = false;
            sword = SWORD;
        } else {
            isSwordGrave = tileEntity.isSwordGrave();
            sword = tileEntity.getSword();
            isEnchanted = tileEntity.isEnchanted();
        }
        EnumGraves graveType = tileEntity.getGraveType();

        int meta = 0;
        if (tileEntity.getWorld() != null) {
            meta = tileEntity.getBlockMetadata();
        }
        EnumFacing facing = EnumFacing.values()[meta];

        renderGrave(x, y, z, tileEntity.getWorld(), graveType, isEnchanted, tileEntity.isMossy(),
                tileEntity.hasFlower(), tileEntity.getFlower(), isSwordGrave, sword, facing);
    }

    public void renderGrave(double x, double y, double z, World world, EnumGraves graveType,
                            boolean isEnchanted, boolean isMossy, boolean hasFlower, ItemStack flower,
                            boolean isSwordGrave, ItemStack sword, EnumFacing facing) {
        GL11.glPushMatrix();

        if (world == null && isSwordGrave) {
            GL11.glTranslatef((float) x + 0.5F, (float) y + 2, (float) z + 0.5F);
            GL11.glScalef(1.5F, -1.5F, -1.5F);
        } else {
            GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
            GL11.glScalef(1, -1, -1);
            if (world == null) {
                GL11.glRotatef(35, 0, 1, 0);
            }
        }

        switch (facing) {
            case SOUTH:
                GL11.glRotatef(0, 0, 1, 0);
                break;
            case WEST:
                GL11.glRotatef(90, 0, 1, 0);
                break;
            case NORTH:
                GL11.glRotatef(180, 0, 1, 0);
                break;
            case EAST:
                GL11.glRotatef(270, 0, 1, 0);
                break;
        }

        renderGrave(world, graveType, isEnchanted, isMossy, hasFlower, flower, isSwordGrave, sword);

        GL11.glPopMatrix();
    }

    private void renderGrave(World world, EnumGraves graveType, boolean isEnchanted, boolean isMossy, boolean hasFlower, ItemStack flower, boolean isSwordGrave, ItemStack sword) {
        if (isSwordGrave) {
            ResourceLocation swordTexture = swordsTextureMap.get(sword.getItem());
            if (Config.vanillaRendererForSwordsGraves || swordTexture == null) {
                if (world == null) {
                    GL11.glScalef(0.5F, -0.5F, -0.5F);
                    GL11.glTranslatef(-0.37F, -1.7F, 0);
                } else {
                    GL11.glScalef(1, -1, -1);
                    GL11.glTranslatef(-0.27F, -0.83F, 0);
                }
                renderSword(world, sword);
            } else {
                ModelGraveStone model = getModel(graveType.getGraveType());
                bindTextureByName(swordTexture);
                if (isEnchanted) {
                    model.renderEnchanted();
                } else {
                    model.renderAll();
                }
            }
        } else {
            ModelGraveStone model = getModel(graveType.getGraveType());

            bindTextureByName(getTexture(graveType, graveType.getTexture(), isMossy));
            if (graveType.getGraveType() == EnumGraveType.CREEPER_STATUE) {
                model.customRender(isEnchanted);
            } else {
                if (isEnchanted) {
                    model.renderEnchanted();
                } else {
                    model.renderAll();
                }
            }

            if (hasFlower) {
                renderFlower(world, flower);
            }
        }
    }

    private ResourceLocation getTexture(EnumGraves graveType, ResourceLocation texture, boolean isMossy) {
        if (isMossy) {
            ResourceLocation mixedMossyTexture = mossyTexturesMap.get(graveType);
            if (mixedMossyTexture == null) {
                ResourceLocation mossyTexture = getMossyTexture(graveType.getGraveType());
                mixedMossyTexture = new ResourceLocation(texture.getResourceDomain() + ":mossy_" + texture.getResourcePath());
                Minecraft.getMinecraft().getTextureManager().loadTexture(mixedMossyTexture,
                        new LayeredTexture(texture.getResourceDomain() + ":" + texture.getResourcePath(),
                                mossyTexture.getResourceDomain() + ":" + mossyTexture.getResourcePath()));
                mossyTexturesMap.put(graveType, mixedMossyTexture);
                return mixedMossyTexture;
            } else {
                return mixedMossyTexture;
            }
        } else {
            return texture;
        }
    }

    private ResourceLocation getMossyTexture(EnumGraveType graveType) {
        switch (graveType) {
            case VERTICAL_PLATE:
            default:
                return Resources.GRAVE_MOSSY_VERTICAL_PLATE;
            case CROSS:
                return Resources.GRAVE_MOSSY_CROSS;
            case OBELISK:
                return Resources.MOSSY_OBELISK;
            case CELTIC_CROSS:
                return Resources.MOSSY_CELTIC_CROSS;
            case HORIZONTAL_PLATE:
                return Resources.GRAVE_MOSSY_HORISONTAL_PLATE;
            case VILLAGER_STATUE:
                return Resources.MOSSY_VILLAGER_STATUE;
            case DOG_STATUE:
                return Resources.MOSSY_DOG_STATUE;
            case CAT_STATUE:
                return Resources.MOSSY_CAT_STATUE;
            case HORSE_STATUE:
                return Resources.GRAVE_MOSSY_HORSE_STATUE;
            case CREEPER_STATUE:
                return Resources.MOSSY_CREEPER_STATUE;
        }
    }

    private ModelGraveStone getModel(EnumGraveType graveType) {
        switch (graveType) {
            case VERTICAL_PLATE:
            default:
                return verticalPlate;
            case CROSS:
                return cross;
            case OBELISK:
                return obelisk;
            case CELTIC_CROSS:
                return celticCross;
            case HORIZONTAL_PLATE:
                return horizontalPlate;
            case VILLAGER_STATUE:
                return villagerStatue;
            case DOG_STATUE:
                return dogStatue;
            case CAT_STATUE:
                return catStatue;
            case HORSE_STATUE:
                return horseStatue;
            case CREEPER_STATUE:
                return creeperStatue;
            case STARVED_CORPSE:
                return skeletonCorpse;
            case WITHERED_CORPSE:
                return witheredSkeletonCorpse;
            case SWORD:
                return swordModel;
        }
    }

    private void renderSword(World world, ItemStack sword) {
        EntityItem entityItem = swordsMap.get(sword.getItem());
        if (entityItem == null) {
            entityItem = new EntityItem(world, 0, 0, 0, sword);
            swordsMap.put(sword.getItem(), entityItem);
        }

        entityItem.hoverStart = 0;
        GL11.glRotatef(225, 0, 0, 1);

        renderItem(sword, entityItem);
    }

    private void renderFlower(World world, ItemStack flower) {
        if (Config.renderGravesFlowers) {
            EntityItem entityItem = flowersMap.get(flower.getItem());
            if (entityItem == null) {
                entityItem = new EntityItem(world, 0, 0, 0, flower);
                flowersMap.put(flower.getItem(), entityItem);
            }

            entityItem.hoverStart = 0;
            GL11.glTranslatef(0, 1.4F, -0.1F);
            GL11.glScalef(0.6F, -0.6F, -0.6F);
            GL11.glRotatef(45, 0, 1, 0);

            renderItem(flower, entityItem);

            GL11.glRotatef(-90, 0, 1, 0);

            renderItem(flower, entityItem);
        }
    }

    protected void renderItem(ItemStack itemstack, EntityItem entityItem) {
        Render<EntityItem> render = Minecraft.getMinecraft().getRenderManager().getEntityRenderObject(entityItem);
        if (render != null && render instanceof RenderEntityItem) {
            GlStateManager.pushMatrix();

            RenderEntityItem renderItem = (RenderEntityItem) render;
            renderItem.bindEntityTexture(entityItem);

            GlStateManager.enableRescaleNormal();
            GlStateManager.alphaFunc(516, 0.1F);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            IBakedModel ibakedmodel = renderItem.itemRenderer.getItemModelMesher().getItemModel(itemstack);

            GlStateManager.translate(0, 0.35F, 0);

            if (ibakedmodel.isGui3d()) {
                GlStateManager.scale(0.5F, 0.5F, 0.5F);
            }
            renderItem.itemRenderer.renderItem(itemstack, ibakedmodel);

            GlStateManager.disableRescaleNormal();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }


    protected TileEntityGraveStone getDefaultTE() {
        return GRAVE_TE;
    }

    protected boolean isSwordGrave() {
        return false;
    }

    public static class VerticalPlateRenderer extends TileEntityGraveStoneRenderer {
        private static final TileEntityGraveStone GRAVE_TE = new TileEntityGraveStone();

        static {
            GRAVE_TE.setGraveType(EnumGraves.STONE_VERTICAL_PLATE.ordinal());
        }

        @Override
        protected TileEntityGraveStone getDefaultTE() {
            return GRAVE_TE;
        }
    }

    public static class CrossRenderer extends TileEntityGraveStoneRenderer {
        private static final TileEntityGraveStone GRAVE_TE = new TileEntityGraveStone();

        static {
            GRAVE_TE.setGraveType(EnumGraves.STONE_CROSS.ordinal());
        }

        @Override
        protected TileEntityGraveStone getDefaultTE() {
            return GRAVE_TE;
        }
    }

    public static class ObeliskRenderer extends TileEntityGraveStoneRenderer {
        private static final TileEntityGraveStone GRAVE_TE = new TileEntityGraveStone();

        static {
            GRAVE_TE.setGraveType(EnumGraves.STONE_OBELISK.ordinal());
        }

        @Override
        protected TileEntityGraveStone getDefaultTE() {
            return GRAVE_TE;
        }
    }

    public static class CelticCrossRenderer extends TileEntityGraveStoneRenderer {
        private static final TileEntityGraveStone GRAVE_TE = new TileEntityGraveStone();

        static {
            GRAVE_TE.setGraveType(EnumGraves.STONE_CELTIC_CROSS.ordinal());
        }

        @Override
        protected TileEntityGraveStone getDefaultTE() {
            return GRAVE_TE;
        }
    }

    public static class HorizontalPlateRenderer extends TileEntityGraveStoneRenderer {
        private static final TileEntityGraveStone GRAVE_TE = new TileEntityGraveStone();

        static {
            GRAVE_TE.setGraveType(EnumGraves.STONE_HORIZONTAL_PLATE.ordinal());
        }

        @Override
        protected TileEntityGraveStone getDefaultTE() {
            return GRAVE_TE;
        }
    }

    public static class VillagerStatueRenderer extends TileEntityGraveStoneRenderer {
        private static final TileEntityGraveStone GRAVE_TE = new TileEntityGraveStone();

        static {
            GRAVE_TE.setGraveType(EnumGraves.STONE_VILLAGER_STATUE.ordinal());
        }

        @Override
        protected TileEntityGraveStone getDefaultTE() {
            return GRAVE_TE;
        }
    }

    public static class DogStatueRenderer extends TileEntityGraveStoneRenderer {
        private static final TileEntityGraveStone GRAVE_TE = new TileEntityGraveStone();

        static {
            GRAVE_TE.setGraveType(EnumGraves.STONE_DOG_STATUE.ordinal());
        }

        @Override
        protected TileEntityGraveStone getDefaultTE() {
            return GRAVE_TE;
        }
    }

    public static class CatStatueRenderer extends TileEntityGraveStoneRenderer {
        private static final TileEntityGraveStone GRAVE_TE = new TileEntityGraveStone();

        static {
            GRAVE_TE.setGraveType(EnumGraves.STONE_CAT_STATUE.ordinal());
        }

        @Override
        protected TileEntityGraveStone getDefaultTE() {
            return GRAVE_TE;
        }
    }

    public static class HorseStatueRenderer extends TileEntityGraveStoneRenderer {
        private static final TileEntityGraveStone GRAVE_TE = new TileEntityGraveStone();

        static {
            GRAVE_TE.setGraveType(EnumGraves.STONE_HORSE_STATUE.ordinal());
        }

        @Override
        protected TileEntityGraveStone getDefaultTE() {
            return GRAVE_TE;
        }
    }

    public static class CreeperStatueRenderer extends TileEntityGraveStoneRenderer {
        private static final TileEntityGraveStone GRAVE_TE = new TileEntityGraveStone();

        static {
            GRAVE_TE.setGraveType(EnumGraves.STONE_CREEPER_STATUE.ordinal());
        }

        @Override
        protected TileEntityGraveStone getDefaultTE() {
            return GRAVE_TE;
        }
    }


    public static class StarvedCorpseRenderer extends TileEntityGraveStoneRenderer {
        private static final TileEntityGraveStone GRAVE_TE = new TileEntityGraveStone();

        static {
            GRAVE_TE.setGraveType(EnumGraves.STARVED_CORPSE.ordinal());
        }

        @Override
        protected TileEntityGraveStone getDefaultTE() {
            return GRAVE_TE;
        }
    }

    public static class WitheredCorpseRenderer extends TileEntityGraveStoneRenderer {
        private static final TileEntityGraveStone GRAVE_TE = new TileEntityGraveStone();

        static {
            GRAVE_TE.setGraveType(EnumGraves.WITHERED_CORPSE.ordinal());
        }

        @Override
        protected TileEntityGraveStone getDefaultTE() {
            return GRAVE_TE;
        }
    }

    public static class SwordRenderer extends TileEntityGraveStoneRenderer {
        private static final TileEntityGraveStone GRAVE_TE = new TileEntityGraveStone();

        static {
            GRAVE_TE.setGraveType(EnumGraves.SWORD.ordinal());
        }

        @Override
        protected TileEntityGraveStone getDefaultTE() {
            return GRAVE_TE;
        }

        @Override
        protected boolean isSwordGrave() {
            return true;
        }
    }
}
