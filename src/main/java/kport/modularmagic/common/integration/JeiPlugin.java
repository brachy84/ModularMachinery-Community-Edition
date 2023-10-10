package kport.modularmagic.common.integration;

import com.google.common.collect.Lists;
import hellfirepvp.modularmachinery.common.base.Mods;
import kport.modularmagic.common.integration.jei.helper.*;
import kport.modularmagic.common.integration.jei.ingredient.*;
import kport.modularmagic.common.integration.jei.render.*;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import thaumcraft.api.aspects.AspectList;

@JEIPlugin
public class JeiPlugin implements IModPlugin {

    public static IGuiHelper GUI_HELPER;

    @Override
    public void register(IModRegistry registry) {
        GUI_HELPER = registry.getJeiHelpers().getGuiHelper();
    }

    @Override
    public void registerIngredients(IModIngredientRegistration registry) {
        if (Mods.BM2.isPresent()) {
            registry.register(DemonWill.class, Lists.newArrayList(), new DemonWillHelper<>(), new DemonWillRenderer());
            registry.register(LifeEssence.class, Lists.newArrayList(), new LifeEssenceHelper<>(), new LifeEssenceRenderer());
        }
        if (Mods.TC6.isPresent() && !Mods.THAUM_JEI.isPresent()) {
            registry.register(AspectList.class, Lists.newArrayList(), new AspectHelper<>(), new AspectRenderer());
        }
        if (Mods.EXU2.isPresent()) {
            registry.register(Grid.class, Lists.newArrayList(), new GridHelper<>(), new GridRenderer());
            registry.register(Rainbow.class, Lists.newArrayList(), new RainbowHelper<>(), new RainbowRenderer());
        }
        if (Mods.ASTRAL.isPresent()) {
            registry.register(Starlight.class, Lists.newArrayList(), new StarlightHelper<>(), new StarlightRenderer());
            registry.register(Constellation.class, Lists.newArrayList(), new ConstellationHelper<>(), new ConstellationRenderer());
        }
        if (Mods.NATURESAURA.isPresent()) {
            registry.register(Aura.class, Lists.newArrayList(), new AuraHelper<>(), new AuraRenderer());
        }
        if (Mods.BOTANIA.isPresent()) {
            registry.register(Mana.class, Lists.newArrayList(), new ManaHelper<Mana>(), new ManaRenderer());
        }
        if (Mods.TA.isPresent()) {
            registry.register(Impetus.class, Lists.newArrayList(), new ImpetusHelper<>(), new ImpetusRender());
        }
    }
}
