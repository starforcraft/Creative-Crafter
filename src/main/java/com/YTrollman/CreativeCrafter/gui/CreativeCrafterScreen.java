package com.YTrollman.CreativeCrafter.gui;

import com.YTrollman.CreativeCrafter.CreativeCrafter;
import com.YTrollman.CreativeCrafter.container.CreativeCrafterContainer;
import com.YTrollman.CreativeCrafter.gui.custombutton.CreativeCrafterGridSearchBoxModeSideButton;
import com.YTrollman.CreativeCrafter.gui.custombutton.CreativeCrafterGridSortingDirectionSideButton;
import com.YTrollman.CreativeCrafter.gui.custombutton.CreativeCrafterGridSortingTypeSideButton;
import com.YTrollman.CreativeCrafter.gui.custombutton.CreativeCrafterGridViewTypeSideButton;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.network.grid.GridType;
import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.apiimpl.render.ElementDrawers;
import com.refinedmods.refinedstorage.network.grid.GridItemGridScrollMessage;
import com.refinedmods.refinedstorage.network.grid.GridItemInventoryScrollMessage;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.screen.IScreenInfoProvider;
import com.refinedmods.refinedstorage.screen.grid.filtering.GridFilterParser;
import com.refinedmods.refinedstorage.screen.grid.sorting.*;
import com.refinedmods.refinedstorage.screen.grid.stack.IGridStack;
import com.refinedmods.refinedstorage.screen.grid.stack.ItemGridStack;
import com.refinedmods.refinedstorage.screen.grid.view.IGridView;
import com.refinedmods.refinedstorage.screen.widget.ScrollbarWidget;
import com.refinedmods.refinedstorage.screen.widget.SearchWidget;
import com.refinedmods.refinedstorage.screen.widget.TabListWidget;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.GridSizeSideButton;
import com.refinedmods.refinedstorage.util.RenderUtils;
import com.refinedmods.refinedstorage.util.TimeUtils;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CreativeCrafterScreen extends BaseScreen<CreativeCrafterContainer> implements IScreenInfoProvider
{
    private static String searchQuery = "";

    private IGridView view;

    private SearchWidget searchField;

    private ScrollbarWidget scrollbar;

    private final IGrid grid;
    private final TabListWidget<CreativeCrafterContainer> tabs;

    private boolean wasConnected;
    private boolean doSort;

    private int slotNumber;

    public CreativeCrafterScreen(CreativeCrafterContainer container, final IGrid grid, PlayerInventory inventory, ITextComponent title)
    {
        super(container, 227, 0, inventory, title);

        this.grid = grid;
        this.view = new GridViewImpl(this, getDefaultSorter(), getSorters());
        this.tabs = new TabListWidget(this, new ElementDrawers(this), grid::getTabs, grid::getTotalTabPages, grid::getTabPage, grid::getTabSelected, 5);
        this.tabs.addListener(new TabListWidget.ITabListListener() {
            public void onSelectionChanged(int tab) {
                grid.onTabSelectionChanged(tab);
            }

            public void onPageChanged(int page) {
                grid.onTabPageChanged(page);
            }
        });
    }

    @Override
    protected void onPreInit() {
        super.onPreInit();
        this.doSort = true;
        this.imageHeight = getTopHeight() + getBottomHeight() + (getVisibleRows() * 18);
    }

    @Override
    public void onPostInit(int x, int y) {
        this.menu.initSlots();

        this.tabs.init(this.imageWidth - 32);

        this.scrollbar = new ScrollbarWidget(this, 174, getTopHeight(), 12, (getVisibleRows() * 18) - 2);

        int sx = x + 80 + 1;
        int sy = y + 6 + 1;
        if (this.searchField == null) {
            this.searchField = new SearchWidget(this.font, sx, sy, 82);
            this.searchField.setResponder((value) -> {
                this.searchField.updateJei();
                this.getView().sort();
                searchQuery = value;
            });
            this.searchField.setMode(this.grid.getSearchBoxMode());
            this.searchField.setValue(searchQuery);
        } else {
            this.searchField.x = sx;
            this.searchField.y = sy;
        }

        addButton(searchField);

        if (grid.getViewType() != -1) {
            addSideButton(new CreativeCrafterGridViewTypeSideButton(this, grid));
        }

        addSideButton(new CreativeCrafterGridSortingDirectionSideButton(this, grid));
        addSideButton(new CreativeCrafterGridSortingTypeSideButton(this, grid));
        addSideButton(new CreativeCrafterGridSearchBoxModeSideButton(this));
        addSideButton(new GridSizeSideButton(this, grid::getSize, grid::onSizeChanged));

        updateScrollbar();
    }

    @Override
    public void tick(int x, int y) {
        if (wasConnected != grid.isGridActive()) {
            wasConnected = grid.isGridActive();

            view.sort();
        }
        this.tabs.update();
    }

    @Override
    public void renderBackground(MatrixStack matrixStack, int x, int y, int mouseX, int mouseY)
    {
        this.tabs.drawBackground(matrixStack, x, y - this.tabs.getHeight());

        bindTexture(CreativeCrafter.MOD_ID, "gui/creative_crafter.png");

        int yy = y;

        blit(matrixStack, x, yy, 0, 0, getXSize() - 34, getTopHeight());

        // Filters and/or portable grid disk
        blit(matrixStack, x + imageWidth - 34 + 4, y, 197, 0, 30, 82);

        int rows = getVisibleRows();

        for (int i = 0; i < rows; ++i) {
            yy += 18;

            int yTextureStart = getTopHeight();
            if (i > 0) {
                if (i == rows - 1) {
                    yTextureStart += 18 * 2;
                } else {
                    yTextureStart += 18;
                }
            }

            blit(matrixStack, x, yy, 0, yTextureStart, imageWidth - 34, 18);
        }

        yy += 18;

        blit(matrixStack, x, yy, 0, getTopHeight() + (18 * 3), imageWidth - 34, getBottomHeight());

        tabs.drawForeground(matrixStack, x, y - tabs.getHeight(), mouseX, mouseY, true);

        searchField.render(matrixStack, 0, 0, 0);

        scrollbar.render(matrixStack);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        // Drawn in here for bug #1844 (https://github.com/refinedmods/refinedstorage/issues/1844)
        // Item tooltips can't be rendered in the foreground layer due to the X offset translation.
        if (isOverSlotWithStack()) {
            drawGridTooltip(matrixStack, view.getStacks().get(slotNumber), mouseX, mouseY);
        }
    }

    @Override
    public void renderForeground(MatrixStack matrixStack, int mouseX, int mouseY)
    {
        renderString(matrixStack, 7, 7, title.getString());
        renderString(matrixStack, 7, this.getYPlayerInventory() - 12, new TranslationTextComponent("container.inventory").getString());

        int x = 8;
        int y = 19;

        this.slotNumber = -1;

        int slot = scrollbar != null ? (scrollbar.getOffset() * 9) : 0;

        RenderHelper.setupFor3DItems();

        for (int i = 0; i < 9 * getVisibleRows(); ++i) {
            if (RenderUtils.inBounds(x, y, 16, 16, mouseX, mouseY) || !grid.isGridActive()) {
                this.slotNumber = slot;
            }

            if (slot < view.getStacks().size()) {
                view.getStacks().get(slot).draw(matrixStack, this, x, y);
            }

            if (RenderUtils.inBounds(x, y, 16, 16, mouseX, mouseY) || !grid.isGridActive()) {
                int color = grid.isGridActive() ? -2130706433 : 0xFF5B5B5B;

                matrixStack.pushPose();
                RenderSystem.disableLighting();
                RenderSystem.disableDepthTest();
                RenderSystem.colorMask(true, true, true, false);
                fillGradient(matrixStack, x, y, x + 16, y + 16, color, color);
                RenderSystem.colorMask(true, true, true, true);
                matrixStack.popPose();
            }

            slot++;

            x += 18;

            if ((i + 1) % 9 == 0) {
                x = 8;
                y += 18;
            }
        }

        this.tabs.drawTooltip(matrixStack, this.font, mouseX, mouseY);
    }

    private boolean isOverSlotWithStack() {
        return grid.isGridActive() && isOverSlot() && slotNumber < view.getStacks().size();
    }

    private boolean isOverSlot() {
        return slotNumber >= 0;
    }

    private void drawGridTooltip(MatrixStack matrixStack, IGridStack gridStack, int mouseX, int mouseY) {
        List<ITextComponent> textLines = gridStack.getTooltip(true);
        List<String> smallTextLines = Lists.newArrayList();

        if (!gridStack.isCraftable()) {
            smallTextLines.add(I18n.get("misc.refinedstorage.total", gridStack.getFormattedFullQuantity()));
        }

        if (gridStack.getTrackerEntry() != null) {
            smallTextLines.add(TimeUtils.getAgo(gridStack.getTrackerEntry().getTime(), gridStack.getTrackerEntry().getName()));
        }

        ItemStack stack = gridStack instanceof ItemGridStack ? ((ItemGridStack) gridStack).getStack() : ItemStack.EMPTY;

        RenderUtils.drawTooltipWithSmallText(matrixStack, textLines, smallTextLines, RS.CLIENT_CONFIG.getGrid().getDetailedTooltip(), stack, mouseX, mouseY, width, height, font);
    }

    @Override
    public int getTopHeight() {
        return 19;
    }

    @Override
    public int getBottomHeight() {
        return 99;
    }

    @Override
    public int getYPlayerInventory() {
        int yp = this.getTopHeight() + this.getVisibleRows() * 18;

        yp += 16;

        return yp;
    }

    @Override
    public int getRows() {
        return Math.max(0, (int)Math.ceil(((float)this.view.getStacks().size() / 9.0F)));
    }

    @Override
    public int getCurrentOffset() {
        return this.scrollbar.getOffset();
    }

    @Override
    public String getSearchFieldText() {
        return searchField.getValue();
    }

    @Override
    public int getVisibleRows() {
        switch(this.grid.getSize()) {
            case 0:
                int screenSpaceAvailable = this.height - this.getTopHeight() - this.getBottomHeight();
                return Math.max(3, Math.min(screenSpaceAvailable / 18 - 3, RS.CLIENT_CONFIG.getGrid().getMaxRowsStretch()));
            case 1:
                return 3;
            case 2:
                return 5;
            case 3:
                return 8;
            default:
                return 3;
        }
    }

    @Override
    public void removed() {
        super.removed();
        if (!RS.CLIENT_CONFIG.getGrid().getRememberSearchQuery()) {
            searchQuery = "";
        }
    }

    @Override
    public void mouseMoved(double mx, double my) {
        scrollbar.mouseMoved(mx, my);

        super.mouseMoved(mx, my);
    }

    @Override
    public boolean mouseReleased(double mx, double my, int button) {
        return scrollbar.mouseReleased(mx, my, button) || super.mouseReleased(mx, my, button);
    }

    @Override
    public boolean mouseScrolled(double x, double y, double delta) {
        if (!hasShiftDown() && !hasControlDown()) {
            return this.scrollbar.mouseScrolled(x, y, delta) || super.mouseScrolled(x, y, delta);
        } else {
            if (RS.CLIENT_CONFIG.getGrid().getPreventSortingWhileShiftIsDown()) {
                this.doSort = !this.isOverSlotArea(x - (double)this.leftPos, y - (double)this.topPos) && !this.isOverCraftingOutputArea(x - (double)this.leftPos, y - (double)this.topPos);
            }

            if (this.grid.getGridType() != GridType.FLUID) {
                if (this.isOverInventory(x - (double)this.leftPos, y - (double)this.topPos) && this.hoveredSlot != null && this.hoveredSlot.hasItem()) {
                    RS.NETWORK_HANDLER.sendToServer(new GridItemInventoryScrollMessage(this.hoveredSlot.getSlotIndex(), hasShiftDown(), delta > 0.0D));
                } else if (this.isOverSlotArea(x - (double)this.leftPos, y - (double)this.topPos)) {
                    RS.NETWORK_HANDLER.sendToServer(new GridItemGridScrollMessage(this.isOverSlotWithStack() ? ((IGridStack)this.view.getStacks().get(this.slotNumber)).getId() : null, hasShiftDown(), delta > 0.0D));
                }
            }

            return super.mouseScrolled(x, y, delta);
        }
    }

    @Override
    public boolean charTyped(char unknown1, int unknown2) {
        if (searchField.charTyped(unknown1, unknown2)) {
            return true;
        }

        return super.charTyped(unknown1, unknown2);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_LEFT_SHIFT ||
                keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT ||
                keyCode == GLFW.GLFW_KEY_LEFT_CONTROL ||
                keyCode == GLFW.GLFW_KEY_RIGHT_CONTROL) {
            view.sort();
        }

        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        if (searchField.keyPressed(key, scanCode, modifiers) || searchField.canConsumeInput()) {
            return true;
        }

        return super.keyPressed(key, scanCode, modifiers);
    }

    @Override
    public void onClose() {
        super.onClose();

        if (!RS.CLIENT_CONFIG.getGrid().getRememberSearchQuery()) {
            searchQuery = "";
        }
    }

    private boolean isOverInventory(double x, double y) {
        return RenderUtils.inBounds(8, getYPlayerInventory(), 9 * 18 - 2, 4 * 18 + 2, x, y);
    }

    public boolean isOverSlotArea(double mouseX, double mouseY) {
        return RenderUtils.inBounds(7, 19, 162, 18 * getVisibleRows(), mouseX, mouseY);
    }

    private boolean isOverCraftingOutputArea(double mouseX, double mouseY) {
        if (grid.getGridType() != GridType.CRAFTING) {
            return false;
        }
        return RenderUtils.inBounds(130, getTopHeight() + getVisibleRows() * 18 + 18, 24, 24, mouseX, mouseY);
    }

    public void updateScrollbar() {
        this.scrollbar.setEnabled(this.getRows() > this.getVisibleRows());
        this.scrollbar.setMaxOffset(this.getRows() - this.getVisibleRows());
    }

    public IGridView getView() {
        return this.view;
    }

    public static List<IGridSorter> getSorters() {
        List<IGridSorter> sorters = new LinkedList();
        sorters.add(getDefaultSorter());
        sorters.add(new QuantityGridSorter());
        sorters.add(new IdGridSorter());
        sorters.add(new LastModifiedGridSorter());
        sorters.add(new InventoryTweaksGridSorter());
        return sorters;
    }

    public static IGridSorter getDefaultSorter() {
        return new NameGridSorter();
    }

    public IGrid getGrid() {
        return this.grid;
    }

    public boolean canSort() {
        return this.doSort || !hasShiftDown() && !hasControlDown();
    }

    public SearchWidget getSearchField() {
        return this.searchField;
    }

    public class GridViewImpl implements IGridView {
        private final CreativeCrafterScreen screen;
        private boolean canCraft;
        private boolean active = false;
        private final IGridSorter defaultSorter;
        private final List<IGridSorter> sorters;
        private List<IGridStack> stacks = new ArrayList();
        protected final Map<UUID, IGridStack> map = new HashMap();

        public GridViewImpl(CreativeCrafterScreen screen, IGridSorter defaultSorter, List<IGridSorter> sorters) {
            this.screen = screen;
            this.defaultSorter = defaultSorter;
            this.sorters = sorters;
        }

        public List<IGridStack> getStacks() {
            return this.stacks;
        }

        public Collection<IGridStack> getAllStacks() {
            return this.map.values();
        }

        @Nullable
        public IGridStack get(UUID id) {
            return this.map.get(id);
        }

        public void sort() {
            if (this.screen.canSort()) {
                if (this.screen.getGrid().isGridActive()) {
                    this.stacks = this.map.values().stream().filter(this.getActiveFilters()).sorted(this.getActiveSort()).collect(Collectors.toCollection(ArrayList::new));
                    this.active = true;
                } else {
                    this.stacks = new ArrayList();
                    this.active = false;
                }

                this.screen.updateScrollbar();
            }
        }

        private Comparator<IGridStack> getActiveSort() {
            IGrid grid = screen.getGrid();
            SortingDirection sortingDirection = grid.getSortingDirection() == IGrid.SORTING_DIRECTION_DESCENDING ? SortingDirection.DESCENDING : SortingDirection.ASCENDING;
            return Stream.concat(Stream.of(defaultSorter), sorters.stream().filter(s -> s.isApplicable(grid)))
                    .map(sorter -> (Comparator<IGridStack>) (o1, o2) -> sorter.compare(o1, o2, sortingDirection))
                    .reduce((l, r) -> r.thenComparing(l))
                    .orElseThrow(IllegalStateException::new);  // There is at least 1 value in the stream (i.e. defaultSorter)
        }

        private Predicate<IGridStack> getActiveFilters() {
            IGrid grid = screen.getGrid();

            Predicate<IGridStack> filters = GridFilterParser.getFilters(
                    grid,
                    screen.getSearchFieldText(),
                    (grid.getTabSelected() >= 0 && grid.getTabSelected() < grid.getTabs().size()) ? grid.getTabs().get(grid.getTabSelected()).getFilters() : grid.getFilters()
            );

            if (screen.getGrid().getViewType() != IGrid.VIEW_TYPE_CRAFTABLES) {
                return stack -> {
                    // If this is a crafting stack,
                    // and there is a regular matching stack in the view too,
                    // and we aren't in "view only craftables" mode,
                    // we don't want the duplicate stacks and we will remove this stack.
                    if (stack.isCraftable() &&
                            stack.getOtherId() != null &&
                            map.containsKey(stack.getOtherId())) {
                        return false;
                    }

                    return filters.test(stack);
                };
            } else {
                return filters;
            }
        }

        public void setStacks(List<IGridStack> stacks) {
            this.map.clear();
            Iterator var2 = stacks.iterator();

            while(var2.hasNext()) {
                IGridStack stack = (IGridStack)var2.next();
                this.map.put(stack.getId(), stack);
            }

        }

        public void postChange(IGridStack stack, int delta) {
            if (this.active) {
                IGridStack craftingStack;
                if (!stack.isCraftable() && stack.getOtherId() != null && this.map.containsKey(stack.getOtherId())) {
                    craftingStack = this.map.get(stack.getOtherId());
                    craftingStack.updateOtherId(stack.getId());
                    craftingStack.setTrackerEntry(stack.getTrackerEntry());
                } else {
                    craftingStack = null;
                }

                IGridStack existing = this.map.get(stack.getId());
                boolean stillExists = true;
                boolean shouldSort = this.screen.canSort();
                if (existing == null) {
                    stack.setQuantity(delta);
                    this.map.put(stack.getId(), stack);
                    existing = stack;
                    if (craftingStack != null && shouldSort) {
                        this.stacks.remove(craftingStack);
                    }
                } else {
                    if (shouldSort) {
                        this.stacks.remove(existing);
                    }

                    existing.setQuantity(existing.getQuantity() + delta);
                    if (existing.getQuantity() <= 0) {
                        this.map.remove(existing.getId());
                        stillExists = false;
                        if (craftingStack != null && shouldSort && this.getActiveFilters().test(craftingStack)) {
                            this.addStack(craftingStack);
                        }
                    }

                    existing.setTrackerEntry(stack.getTrackerEntry());
                }

                if (shouldSort) {
                    if (stillExists && this.getActiveFilters().test(existing)) {
                        this.addStack(existing);
                    }

                    this.screen.updateScrollbar();
                }

            }
        }

        private void addStack(IGridStack stack) {
            int insertionPos = Collections.binarySearch(this.stacks, stack, this.getActiveSort());
            if (insertionPos < 0) {
                insertionPos = -insertionPos - 1;
            }

            this.stacks.add(insertionPos, stack);
        }

        public void setCanCraft(boolean canCraft) {
            this.canCraft = canCraft;
        }

        public boolean canCraft() {
            return this.canCraft;
        }
    }
}
