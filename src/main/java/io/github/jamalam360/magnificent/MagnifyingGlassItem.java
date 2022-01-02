/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2021 Jamalam360
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package io.github.jamalam360.magnificent;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * @author Jamalam360
 */
public class MagnifyingGlassItem extends Item {
    public MagnifyingGlassItem() {
        super(new FabricItemSettings().maxCount(1).group(ItemGroup.TOOLS).maxDamage(125));
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (!context.getWorld().isClient) {
            context.getPlayer().sendMessage(evaluateUse(context.getWorld().getBlockState(context.getBlockPos())), true);
            context.getStack().damage(1, context.getPlayer(), (p) -> p.sendEquipmentBreakStatus(context.getHand() == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND));
            return ActionResult.SUCCESS;
        } else {
            return ActionResult.PASS;
        }
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (!user.getWorld().isClient) {
            user.sendMessage(evaluateUse(entity), true);
            stack.damage(1, user, (p) -> p.sendEquipmentBreakStatus(hand == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND));
            return ActionResult.SUCCESS;
        } else {
            return ActionResult.PASS;
        }
    }

    private Text evaluateUse(Object o) {
        MutableText txt;
        Identifier id;

        if (Block.class.isAssignableFrom(o.getClass())) {
            txt = new TranslatableText(((Block) o).getTranslationKey());
            id = Registry.BLOCK.getId((Block) o);
        } else if (BlockState.class.isAssignableFrom(o.getClass())) {
            txt = new TranslatableText(((BlockState) o).getBlock().getTranslationKey());
            id = Registry.BLOCK.getId(((BlockState) o).getBlock());
        } else if (Entity.class.isAssignableFrom(o.getClass())) {
            txt = new TranslatableText(((Entity) o).getType().getTranslationKey());
            id = Registry.ENTITY_TYPE.getId(((Entity) o).getType());
        } else {
            return new TranslatableText("text.magnificent.unknown");
        }

        if (MagnificentConfig.get().displayModName) {
            final MutableText[] modTxt = new MutableText[1];

            if ("minecraft".equals(id.getNamespace())) {
                modTxt[0] = new LiteralText(" (Minecraft)");
            } else {
                FabricLoader.getInstance().getModContainer(id.getNamespace()).ifPresentOrElse(
                        mod -> modTxt[0] = new LiteralText(" " + mod.getMetadata().getName())
                        , () -> modTxt[0] = new LiteralText(" ").append(new TranslatableText("text.magnificent.unknown_brackets"))
                );
            }

            txt.append(modTxt[0]);
        }

        return txt.styled(s -> s.withColor(TextColor.fromFormatting(Formatting.AQUA)).withBold(true));
    }
}
