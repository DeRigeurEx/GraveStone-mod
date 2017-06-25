package nightkosh.gravestone;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import nightkosh.gravestone.api.GraveStoneAPI;
import nightkosh.gravestone.api.IGraveGeneration;
import nightkosh.gravestone.api.IGraveStoneHelper;
import nightkosh.gravestone.api.ModInfo;
import nightkosh.gravestone.config.Config;
import nightkosh.gravestone.core.*;
import nightkosh.gravestone.core.commands.Commands;
import nightkosh.gravestone.core.compatibility.Compatibility;
import nightkosh.gravestone.core.event.EventHandlerNetwork;
import nightkosh.gravestone.core.event.EventsHandler;
import nightkosh.gravestone.core.proxy.CommonProxy;
import nightkosh.gravestone.helper.GraveGenerationHelper;
import nightkosh.gravestone.helper.api.APIGraveGeneration;

/**
 * GraveStone mod
 *
 * @author NightKosh
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
@Mod(modid = ModInfo.ID, name = ModInfo.NAME, version = ModInfo.VERSION, updateJSON = "https://raw.githubusercontent.com/NightKosh/GraveStone-mod/master/update.json")
public class ModGraveStone {

    @Instance("GraveStone")
    public static ModGraveStone instance;
    @SidedProxy(clientSide = "nightkosh.gravestone.core.proxy.ClientProxy", serverSide = "nightkosh.gravestone.core.proxy.CommonProxy")
    public static CommonProxy proxy;

    public static final IGraveStoneHelper gravestoneHelper = GraveGenerationHelper.INSTANCE;
    public static final IGraveGeneration apiGraveGeneration = APIGraveGeneration.INSTANCE;

    public ModGraveStone() {
        instance = this;
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Config.getInstance(event.getModConfigurationDirectory().getAbsolutePath() + "/GraveStoneMod/", "GraveStone.cfg");

        // API
        GraveStoneAPI.graveStone = gravestoneHelper;
        GraveStoneAPI.graveGenerationAtDeath = apiGraveGeneration;
    }

    @Mod.EventHandler
    public void load(FMLInitializationEvent event) {
        // register death event
        MinecraftForge.EVENT_BUS.register(new EventsHandler());
        FMLCommonHandler.instance().bus().register(new EventHandlerNetwork());
        proxy.registerHandlers();

        // tabs
        Tabs.registration();

        // blocks registration
        GSBlock.registration();


        // tileEntities registration
        GSTileEntity.registration();

        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());

        proxy.registerRenderers();
    }

    //    @Mod.EventHandler
    @SubscribeEvent
    public static void onModelBake(ModelBakeEvent event) {
        IBakedModel chestVoidModel = event.getModelRegistry().getObject(ResourcesModels.GRAVE_STONE_MODEL);

        event.getModelRegistry().putObject(ResourcesModels.GRAVE_STONE_MODEL, chestVoidModel);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        Compatibility.getInstance();
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        Commands.registration(event);
    }
}