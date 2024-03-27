package github.kasuminova.mmce.common.integration.groovyscript;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.helper.EnumHelper;
import hellfirepvp.modularmachinery.common.block.BlockCasing;
import hellfirepvp.modularmachinery.common.block.prop.EnergyHatchData;
import hellfirepvp.modularmachinery.common.block.prop.FluidHatchSize;
import hellfirepvp.modularmachinery.common.block.prop.ItemBusSize;
import hellfirepvp.modularmachinery.common.lib.BlocksMM;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nullable;

public class MachineBuilderEvent extends Event {

    public GroovyMachineBuilder machineBuilder(String registryName) {
        return new GroovyMachineBuilder(registryName);
    }

    private IBlockState[] getBlockStates(Block block, int min, int max, @Nullable Block additional) {
        int s = max - min;
        if (additional != null) s++;
        IBlockState[] states = new IBlockState[s];
        for (int i = min; i <= max; i++) {
            states[i - min] = block.getStateFromMeta(i);
        }
        if (additional != null) states[s - 1] = additional.getDefaultState();
        return states;
    }

    public IBlockState[] itemInputs(int min, int max, boolean allowME) {
        return getBlockStates(BlocksMM.itemInputBus, min, max, allowME ? BlocksMM.meItemInputBus : null);
    }

    public IBlockState[] itemInputs(boolean allowME) {
        return itemInputs(0, ItemBusSize.values().length - 1, allowME);
    }

    public IBlockState[] itemInputs() {
        return itemInputs(true);
    }

    public IBlockState[] itemInputs(int min, int max) {
        return itemInputs(min, max, true);
    }

    public IBlockState[] itemOutputs(int min, int max, boolean allowME) {
        return getBlockStates(BlocksMM.itemOutputBus, min, max, allowME ? BlocksMM.meItemOutputBus : null);
    }

    public IBlockState[] itemOutputs(boolean allowME) {
        return itemOutputs(0, ItemBusSize.values().length - 1, allowME);
    }

    public IBlockState[] itemOutputs() {
        return itemOutputs(true);
    }

    public IBlockState[] itemOutputs(int min, int max) {
        return itemOutputs(min, max, true);
    }

    public IBlockState[] fluidInputs(int min, int max, boolean allowME) {
        return getBlockStates(BlocksMM.fluidInputHatch, min, max, allowME ? BlocksMM.meFluidInputBus : null);
    }

    public IBlockState[] fluidInputs(boolean allowME) {
        return fluidInputs(0, FluidHatchSize.values().length - 1, allowME);
    }

    public IBlockState[] fluidInputs() {
        return fluidInputs(true);
    }

    public IBlockState[] fluidInputs(int min, int max) {
        return fluidInputs(min, max, true);
    }

    public IBlockState[] fluidOutputs(int min, int max, boolean allowME) {
        return getBlockStates(BlocksMM.fluidOutputHatch, min, max, allowME ? BlocksMM.meFluidOutputBus : null);
    }

    public IBlockState[] fluidOutputs(boolean allowME) {
        return fluidOutputs(0, FluidHatchSize.values().length - 1, allowME);
    }

    public IBlockState[] fluidOutputs() {
        return fluidOutputs(true);
    }

    public IBlockState[] fluidOutputs(int min, int max) {
        return fluidOutputs(min, max, true);
    }

    public IBlockState[] energyInputs(int min, int max) {
        return getBlockStates(BlocksMM.energyInputHatch, min, max, null);
    }

    public IBlockState[] energyInputs() {
        return energyInputs(0, EnergyHatchData.values().length - 1);
    }

    public IBlockState[] energyOutputs(int min, int max) {
        return getBlockStates(BlocksMM.energyOutputHatch, min, max, null);
    }

    public IBlockState[] energyOutputs() {
        return energyOutputs(0, EnergyHatchData.values().length - 1);
    }

    public IBlockState casing(String type) {
        BlockCasing.CasingType casingType = EnumHelper.valueOfNullable(BlockCasing.CasingType.class, type, false);
        if (casingType == null) {
            GroovyLog.get().error("CasingType fot {} not found!", type);
            return Blocks.BEDROCK.getDefaultState();
        }
        return BlocksMM.blockCasing.getStateFromMeta(casingType.ordinal());
    }

    public IBlockState casing() {
        return BlocksMM.blockCasing.getStateFromMeta(0);
    }

    public IBlockState smartInterface() {
        return BlocksMM.smartInterface.getDefaultState();
    }

    public IBlockState parallelController() {
        return BlocksMM.smartInterface.getDefaultState();
    }

    public IBlockState upgradeBus() {
        return BlocksMM.smartInterface.getDefaultState();
    }
}
