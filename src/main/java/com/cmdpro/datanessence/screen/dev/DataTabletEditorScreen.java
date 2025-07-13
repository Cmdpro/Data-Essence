package com.cmdpro.datanessence.screen.dev;

import com.cmdpro.databank.rendering.ColorUtil;
import com.cmdpro.datanessence.data.datatablet.Entries;
import com.cmdpro.datanessence.data.datatablet.Entry;
import com.cmdpro.datanessence.screen.DataTabletScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector2i;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataTabletEditorScreen extends DataTabletScreen {
    public DataTabletEditorScreen(Component pTitle) {
        super(pTitle);
    }
    public boolean parenting;
    public Vector2i parentingTargetPos;
    public Entry draggingEntry;
    public HashMap<Entry, Vector2i> entryPositionOverrides = new HashMap<>();
    public HashMap<Entry, List<Entry>> entryParentOverrides = new HashMap<>();
    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        if (draggingEntry != null) {
            double mouseX = pMouseX+pDragX;
            double mouseY = pMouseY+pDragY;
            mouseX -= x;
            mouseY -= y;
            mouseX -= offsetX;
            mouseY -= offsetY;
            int posX = (int)Math.floor((mouseX+10d)/20d);
            int posY = (int)Math.floor((mouseY+10d)/20d);
            if (parenting) {
                parentingTargetPos = new Vector2i(posX, posY);
            } else {
                if (!entryPositionOverrides.containsKey(draggingEntry)) {
                    if (draggingEntry.x != posX && draggingEntry.y != posY) {
                        entryPositionOverrides.put(draggingEntry, new Vector2i(posX, posY));
                    }
                } else {
                    entryPositionOverrides.put(draggingEntry, new Vector2i(posX, posY));
                }
            }
            return true;
        }
        return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (!isHoveringAnyEntry(pMouseX, pMouseY)) {
            boolean found = false;
            for (Entry destinationEntry : Entries.entries.values()) {
                if (destinationEntry.tab.equals(currentTab.id) && isEntryUnlocked(destinationEntry)) {
                    for (Entry sourceEntry : getEntryParentEntries(destinationEntry)) {
                        if (sourceEntry.tab.equals(currentTab.id)) {
                            if (isHoveringLine(sourceEntry, destinationEntry, pMouseX, pMouseY)) {
                                List<Entry> parents = entryParentOverrides.getOrDefault(destinationEntry, destinationEntry.getParentEntries());
                                if (parents.contains(sourceEntry)) {
                                    parents = new ArrayList<>(parents);
                                    parents.remove(sourceEntry);
                                    entryParentOverrides.put(destinationEntry, parents);
                                }
                                found = true;
                                break;
                            }
                        }
                    }
                    if (found) {
                        break;
                    }
                }
            }
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        super.render(graphics, mouseX, mouseY, delta);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        if (draggingEntry != null && parenting) {
            Vector2i entryPosition = getEntryPosition(draggingEntry);
            int destinationX = x + (entryPosition.x * 20) + (int) offsetX;
            int destinationY = y + (entryPosition.y * 20) + (int) offsetY;
            int sourceX = x + (parentingTargetPos.x * 20) + (int) offsetX;
            int sourceY = y + (parentingTargetPos.y * 20) + (int) offsetY;
            drawLine(sourceX, sourceY, destinationX, destinationY, draggingEntry, null, delta, mouseX, mouseY);
        }
    }

    @Override
    public boolean clickEntry(Entry entry) {
        return true;
    }

    @Override
    public boolean clickEntry(Entry entry, int button) {
        parenting = button == 1;
        parentingTargetPos = new Vector2i(getEntryPosition(entry));
        draggingEntry = entry;
        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        if (parenting && draggingEntry != null) {
            for (Entry entry : Entries.entries.values()) {
                if (entry.tab.equals(currentTab.id) && isEntryUnlocked(entry)) {
                    Vector2i entryPosition = getEntryPosition(entry);
                    if (entryPosition.x == parentingTargetPos.x && entryPosition.y == parentingTargetPos.y) {
                        List<Entry> parents = entryParentOverrides.getOrDefault(entry, entry.getParentEntries());
                        if (!parents.contains(draggingEntry)) {
                            parents = new ArrayList<>(parents);
                            parents.add(draggingEntry);
                            entryParentOverrides.put(entry, parents);
                        }
                        break;
                    }
                }
            }
        }
        parenting = false;
        draggingEntry = null;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public Color getLineColor1(Entry source, Entry destination, float pPartialTick, int pMouseX, int pMouseY) {
        Color color = super.getLineColor1(source, destination, pPartialTick, pMouseX, pMouseY);
        if (source != null && destination != null) {
            if (isHoveringLine(source, destination, pMouseX, pMouseY) && !isHoveringAnyEntry(pMouseX, pMouseY)) {
                color = ColorUtil.blendColors(color, Color.WHITE, 0.25f);
            }
        }
        return color;
    }

    @Override
    public Color getLineColor2(Entry source, Entry destination, float pPartialTick, int pMouseX, int pMouseY) {
        Color color = super.getLineColor2(source, destination, pPartialTick, pMouseX, pMouseY);
        if (source != null && destination != null) {
            if (isHoveringLine(source, destination, pMouseX, pMouseY) && !isHoveringAnyEntry(pMouseX, pMouseY)) {
                color = ColorUtil.blendColors(color, Color.WHITE, 0.25f);
            }
        }
        return color;
    }
    public boolean isHoveringLine(Entry source, Entry destination, double mouseX, double mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        if (mouseX >= x && mouseY >= y && mouseX <= x+imageWidth && mouseY <= y+imageHeight) {
            Vector2i sourcePos = getEntryPosition(source);
            Vector2i destinationPos = getEntryPosition(destination);
            int sourceX = x + (sourcePos.x * 20) + (int) offsetX;
            int sourceY = y + (sourcePos.y * 20) + (int) offsetY;
            int destinationX = x + (destinationPos.x * 20) + (int) offsetX;
            int destinationY = y + (destinationPos.y * 20) + (int) offsetY;
            Line2D line = new Line2D.Double(sourceX, sourceY, destinationX, destinationY);
            Rectangle2D cursor = new Rectangle2D.Double(mouseX, mouseY, 4, 4);
            return line.intersects(cursor);
        }
        return false;
    }
    public boolean isHoveringAnyEntry(double mouseX, double mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        if (mouseX >= x && mouseY >= y && mouseX <= x+imageWidth && mouseY <= y+imageHeight) {
            for (Entry entry : Entries.entries.values()) {
                if (entry.tab.equals(currentTab.id) && isEntryUnlocked(entry)) {
                    Vector2i entryPosition = getEntryPosition(entry);
                    if (mouseX >= ((entryPosition.x * 20) - 10) + offsetX + x && mouseX <= ((entryPosition.x * 20) + 10) + offsetX + x) {
                        if (mouseY >= ((entryPosition.y * 20) - 10) + offsetY + y && mouseY <= ((entryPosition.y * 20) + 10) + offsetY + y) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public Vector2i getEntryPosition(Entry entry) {
        if (entryPositionOverrides.containsKey(entry)) {
            return entryPositionOverrides.get(entry);
        }
        return super.getEntryPosition(entry);
    }

    @Override
    public boolean isEntryUnlocked(Entry entry) {
        return true;
    }

    @Override
    public int getEntryCompletionStage(Entry entry) {
        return entry.completionStages.size();
    }

    @Override
    public List<Entry> getEntryParentEntries(Entry entry) {
        if (entryParentOverrides.containsKey(entry)) {
            return entryParentOverrides.get(entry);
        }
        return super.getEntryParentEntries(entry);
    }

    @Override
    public List<ResourceLocation> getEntryParents(Entry entry) {
        if (entryParentOverrides.containsKey(entry)) {
            return entryParentOverrides.get(entry).stream().map((i) -> i.id).toList();
        }
        return super.getEntryParents(entry);
    }
}
