/*******************************************************************************
 * HellFirePvP / Modular Machinery 2019
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/ModularMachinery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.modularmachinery.common.crafting.requirement;

import com.google.common.collect.Iterables;
import crafttweaker.api.minecraft.CraftTweakerMC;
import hellfirepvp.modularmachinery.common.crafting.helper.*;
import hellfirepvp.modularmachinery.common.crafting.requirement.jei.JEIComponentItem;
import hellfirepvp.modularmachinery.common.crafting.requirement.type.RequirementTypeItem;
import hellfirepvp.modularmachinery.common.integration.crafttweaker.helper.AdvancedItemModifier;
import hellfirepvp.modularmachinery.common.integration.crafttweaker.helper.AdvancedItemNBTChecker;
import hellfirepvp.modularmachinery.common.lib.ComponentTypesMM;
import hellfirepvp.modularmachinery.common.lib.RequirementTypesMM;
import hellfirepvp.modularmachinery.common.machine.IOType;
import hellfirepvp.modularmachinery.common.machine.MachineComponent;
import hellfirepvp.modularmachinery.common.modifier.RecipeModifier;
import hellfirepvp.modularmachinery.common.util.IOInventory;
import hellfirepvp.modularmachinery.common.util.ItemUtils;
import hellfirepvp.modularmachinery.common.util.ResultChance;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This class is part of the Modular Machinery Mod
 * The complete source code for this mod can be found on github.
 * Class: RequirementItem
 * Created by HellFirePvP
 * Date: 24.02.2018 / 12:35
 */
public class RequirementItem extends ComponentRequirement<ItemStack, RequirementTypeItem> implements ComponentRequirement.ChancedRequirement {

    public final ItemRequirementType requirementType;

    public final ItemStack required;

    public final String oreDictName;
    public final int oreDictItemAmount;

    public final int fuelBurntime;
    public final List<AdvancedItemModifier> itemModifierList = new ArrayList<>();
    public int countIOBuffer = 0;
    public NBTTagCompound tag = null;
    public NBTTagCompound previewDisplayTag = null;
    public AdvancedItemNBTChecker nbtChecker = null;
    public float chance = 1F;

    public RequirementItem(IOType ioType, ItemStack item) {
        super(RequirementTypesMM.REQUIREMENT_ITEM, ioType);
        this.requirementType = ItemRequirementType.ITEMSTACKS;
        this.required = item.copy();
        this.oreDictName = null;
        this.oreDictItemAmount = 0;
        this.fuelBurntime = 0;
    }

    public RequirementItem(IOType ioType, String oreDictName, int oreDictAmount) {
        super(RequirementTypesMM.REQUIREMENT_ITEM, ioType);
        this.requirementType = ItemRequirementType.OREDICT;
        this.oreDictName = oreDictName;
        this.oreDictItemAmount = oreDictAmount;
        this.required = ItemStack.EMPTY;
        this.fuelBurntime = 0;
    }

    public RequirementItem(IOType actionType, int fuelBurntime) {
        super(RequirementTypesMM.REQUIREMENT_ITEM, actionType);
        this.requirementType = ItemRequirementType.FUEL;
        this.fuelBurntime = fuelBurntime;
        this.oreDictName = null;
        this.oreDictItemAmount = 0;
        this.required = ItemStack.EMPTY;
    }

    public void setNbtChecker(AdvancedItemNBTChecker nbtChecker) {
        this.nbtChecker = nbtChecker;
    }

    public void addItemModifier(AdvancedItemModifier itemModifier) {
        this.itemModifierList.add(itemModifier);
    }

    @Override
    public int getSortingWeight() {
        return PRIORITY_WEIGHT_ITEM;
    }

    @Override
    public ComponentRequirement<ItemStack, RequirementTypeItem> deepCopy() {
        RequirementItem item;
        switch (this.requirementType) {
            case OREDICT:
                item = new RequirementItem(this.actionType, this.oreDictName, this.oreDictItemAmount);
                break;

            case FUEL:
                item = new RequirementItem(this.actionType, this.fuelBurntime);
                break;

            default:
            case ITEMSTACKS:
                item = new RequirementItem(this.actionType, this.required.copy());
                break;
        }
        item.chance = this.chance;
        if (this.nbtChecker != null) {
            item.nbtChecker = this.nbtChecker;
        } else if (this.tag != null) {
            item.tag = this.tag.copy();
        }
        if (!this.itemModifierList.isEmpty()) {
            item.itemModifierList.addAll(this.itemModifierList);
        }
        if (this.previewDisplayTag != null) {
            item.previewDisplayTag = this.previewDisplayTag.copy();
        }
        return item;
    }

    @Override
    public ComponentRequirement<ItemStack, RequirementTypeItem> deepCopyModified(List<RecipeModifier> modifiers) {
        RequirementItem item;
        switch (this.requirementType) {
            case OREDICT:
                int inOreAmt = Math.round(RecipeModifier.applyModifiers(modifiers, this, this.oreDictItemAmount, false));
                item = new RequirementItem(this.actionType, this.oreDictName, inOreAmt);
                break;
            case FUEL:
                int inFuel = Math.round(RecipeModifier.applyModifiers(modifiers, this, this.fuelBurntime, false));
                item = new RequirementItem(this.actionType, inFuel);
                break;
            default:
            case ITEMSTACKS:
                ItemStack inReq = this.required.copy();
                int amt = Math.round(RecipeModifier.applyModifiers(modifiers, this, inReq.getCount(), false));
                inReq.setCount(amt);
                item = new RequirementItem(this.actionType, inReq);
                break;
        }

        item.chance = RecipeModifier.applyModifiers(modifiers, this, this.chance, true);
        if (this.tag != null) {
            item.tag = this.tag.copy();
        }
        if (this.previewDisplayTag != null) {
            item.previewDisplayTag = this.previewDisplayTag.copy();
        }
        return item;
    }

    @Override
    public JEIComponent<ItemStack> provideJEIComponent() {
        return new JEIComponentItem(this);
    }

    @Override
    public void startRequirementCheck(ResultChance contextChance, RecipeCraftingContext context) {
        switch (this.requirementType) {
            case ITEMSTACKS:
                this.countIOBuffer = this.required.getCount();
                break;
            case OREDICT:
                this.countIOBuffer = this.oreDictItemAmount;
                break;
            case FUEL:
                this.countIOBuffer = this.fuelBurntime;
                break;
        }
        this.countIOBuffer = Math.round(RecipeModifier.applyModifiers(context, this, this.countIOBuffer, false));
    }

    @Override
    public void endRequirementCheck() {
        this.countIOBuffer = 0;
    }

    @Override
    public void setChance(float chance) {
        this.chance = chance;
    }

    @Nonnull
    @Override
    public String getMissingComponentErrorMessage(IOType ioType) {
        ResourceLocation compKey = this.getRequirementType().getRegistryName();
        return String.format("component.missing.%s.%s.%s",
                compKey.getNamespace(), compKey.getPath(), ioType.name().toLowerCase());
    }

    @Override
    public boolean isValidComponent(ProcessingComponent<?> component, RecipeCraftingContext ctx) {
        MachineComponent<?> cmp = component.component;
        return cmp.getComponentType().equals(ComponentTypesMM.COMPONENT_ITEM) &&
               cmp instanceof MachineComponent.ItemBus &&
               cmp.ioType == actionType;
    }

    @Nonnull
    @Override
    public CraftCheck canStartCrafting(ProcessingComponent<?> component, RecipeCraftingContext context, List<ComponentOutputRestrictor> restrictions) {
        IOInventory handler = (IOInventory) component.providedComponent;

        if (actionType == IOType.INPUT) {
            switch (this.requirementType) {
                case ITEMSTACKS:
                    ItemStack inReq = this.required.copy();
                    int amt = Math.round(RecipeModifier.applyModifiers(context, this, inReq.getCount(), false));
                    inReq.setCount(amt);

                    boolean isSuccess = (this.nbtChecker != null)
                            ? ItemUtils.consumeFromInventory(handler, inReq, true, this.nbtChecker)
                            : ItemUtils.consumeFromInventory(handler, inReq, true, this.tag);
                    if (isSuccess) {
                        return CraftCheck.success();
                    }
                    break;
                case OREDICT:
                    int inOreAmt = Math.round(RecipeModifier.applyModifiers(context, this, this.oreDictItemAmount, false));

                    isSuccess = (this.nbtChecker != null)
                            ? ItemUtils.consumeFromInventoryOreDict(handler, this.oreDictName, inOreAmt, true, nbtChecker)
                            : ItemUtils.consumeFromInventoryOreDict(handler, this.oreDictName, inOreAmt, true, this.tag);
                    if (isSuccess) {
                        return CraftCheck.success();
                    }
                    break;
                case FUEL:
                    int inFuel = Math.round(RecipeModifier.applyModifiers(context, this, this.fuelBurntime, false));
                    if (ItemUtils.consumeFromInventoryFuel(handler, inFuel, true, this.tag) <= 0) {
                        return CraftCheck.success();
                    }
                    break;
            }
            return CraftCheck.failure("craftcheck.failure.item.input");
        } else if (actionType == IOType.OUTPUT) {
            for (ComponentOutputRestrictor restrictor : restrictions) {
                if (restrictor instanceof ComponentOutputRestrictor.RestrictionInventory) {
                    ComponentOutputRestrictor.RestrictionInventory inv = (ComponentOutputRestrictor.RestrictionInventory) restrictor;

                    if (inv.exactComponent.equals(component)) {
                        ItemUtils.tryPlaceItemInInventory(inv.inserted.copy(), handler, true);
                    }
                }
            }

            ItemStack stack = ItemStack.EMPTY;
            if (oreDictName != null) {
                for (ItemStack oreInstance : OreDictionary.getOres(oreDictName)) {
                    if (oreInstance.isEmpty()) {
                        continue;
                    }
                    stack = ItemUtils.copyStackWithSize(oreInstance, this.countIOBuffer);

                    if (!stack.isEmpty()) { //Try all options first..
                        break;
                    }
                }

                if (this.countIOBuffer > 0 && stack.isEmpty()) {
                    throw new IllegalArgumentException("Unknown ItemStack: Cannot find an item in oredict '" + oreDictName + "'!");
                }
            } else {
                stack = ItemUtils.copyStackWithSize(required, this.countIOBuffer);
            }

            if (tag != null) {
                stack.setTagCompound(tag.copy());
            }
            int inserted = ItemUtils.tryPlaceItemInInventory(stack.copy(), handler, true);
            if (inserted > 0) {
                context.addRestriction(new ComponentOutputRestrictor.RestrictionInventory(ItemUtils.copyStackWithSize(stack, inserted), component));
            }
            this.countIOBuffer -= inserted;
            if (this.countIOBuffer <= 0) {
                return CraftCheck.success();
            }
            return CraftCheck.failure("craftcheck.failure.item.output.space");
        }
        return CraftCheck.skipComponent();
    }

    @Override
    public boolean startCrafting(ProcessingComponent<?> component, RecipeCraftingContext context, ResultChance chance) {
        IOInventory handler = (IOInventory) component.providedComponent;
        float productionChance = RecipeModifier.applyModifiers(context, this, this.chance, true);
        if (actionType == IOType.INPUT) {
            switch (this.requirementType) {
                case ITEMSTACKS:
                    ItemStack stackRequired = this.required.copy();
                    int amt = Math.round(RecipeModifier.applyModifiers(context, this, stackRequired.getCount(), false));
                    stackRequired.setCount(amt);
                    if (!itemModifierList.isEmpty()) {
                        for (AdvancedItemModifier modifier : itemModifierList) {
                            stackRequired = CraftTweakerMC.getItemStack(modifier.apply(CraftTweakerMC.getIItemStackMutable(stackRequired)));
                        }
                    }

                    boolean can = (this.nbtChecker != null)
                            ? ItemUtils.consumeFromInventory(handler, stackRequired, false, this.nbtChecker)
                            : ItemUtils.consumeFromInventory(handler, stackRequired, false, this.tag);
                    if (chance.canProduce(productionChance)) {
                        return can;
                    }
                    return can;
                case OREDICT:
                    int requiredOredict = Math.round(RecipeModifier.applyModifiers(context, this, this.oreDictItemAmount, false));
                    can = (this.nbtChecker != null)
                            ? ItemUtils.consumeFromInventoryOreDict(handler, this.oreDictName, requiredOredict, false, nbtChecker)
                            : ItemUtils.consumeFromInventoryOreDict(handler, this.oreDictName, requiredOredict, false, this.tag);
                    if (chance.canProduce(productionChance)) {
                        return can;
                    }
                    return can;
                case FUEL:
                    int requiredBurnTime = Math.round(RecipeModifier.applyModifiers(context, this, this.fuelBurntime, false));
                    can = ItemUtils.consumeFromInventoryFuel(handler, requiredBurnTime, false, this.tag) <= 0;
                    if (chance.canProduce(productionChance)) {
                        return can;
                    }
                    return can && ItemUtils.consumeFromInventoryFuel(handler, requiredBurnTime, false, this.tag) <= 0;
            }
        }
        return false;
    }

    @Override
    @Nonnull
    public CraftCheck finishCrafting(ProcessingComponent<?> component, RecipeCraftingContext context, ResultChance chance) {
        if (fuelBurntime > 0 && oreDictName == null && required.isEmpty()) {
            throw new IllegalStateException("Invalid item output!");
        }
        IOInventory handler = (IOInventory) component.providedComponent;
        if (Objects.requireNonNull(actionType) == IOType.OUTPUT) {
            ItemStack stack;
            if (oreDictName != null) {
                stack = Iterables.getFirst(OreDictionary.getOres(oreDictName), ItemStack.EMPTY);
                stack = ItemUtils.copyStackWithSize(stack, this.countIOBuffer);
            } else {
                stack = ItemUtils.copyStackWithSize(required, this.countIOBuffer);
            }

            if (stack.isEmpty()) {
                return CraftCheck.success(); //Can't find anything to output. Guess that's a valid state.
            }
            if (!itemModifierList.isEmpty()) {
                for (AdvancedItemModifier modifier : itemModifierList) {
                    stack = CraftTweakerMC.getItemStack(modifier.apply(CraftTweakerMC.getIItemStackMutable(stack)));
                }
            } else {
                if (tag != null) {
                    stack.setTagCompound(tag);
                }
            }

            //If we don't produce the item, we only need to see if there would be space for it at all.
            int inserted = ItemUtils.tryPlaceItemInInventory(stack.copy(), handler, true);
            if (inserted > 0 && chance.canProduce(RecipeModifier.applyModifiers(context, this, this.chance, true))) {
                return CraftCheck.success();
            }
            if (inserted > 0) {
                int actual = ItemUtils.tryPlaceItemInInventory(stack.copy(), handler, false);
                this.countIOBuffer -= actual;
                if (this.countIOBuffer <= 0) {
                    return CraftCheck.success();
                }
                return CraftCheck.partialSuccess();
            }
            return CraftCheck.failure("craftcheck.failure.item.output.space");
        }
        return CraftCheck.skipComponent();
    }

    public enum ItemRequirementType {
        ITEMSTACKS,
        OREDICT,
        FUEL,
        ITEMSTACK_ARRAY

    }

}
