/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2020 Pylo and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.mcreator.ui.modgui;

import net.mcreator.blockly.data.Dependency;
import net.mcreator.element.types.Overlay;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.procedure.ProcedureSelector;
import net.mcreator.ui.wysiwyg.WYSIWYGEditor;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.elements.VariableTypeLoader;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;

public class OverlayGUI extends ModElementGUI<Overlay> {

	private WYSIWYGEditor editor;

	private ProcedureSelector displayCondition;

	public OverlayGUI(MCreator mcreator, ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		this.initGUI();
		super.finalizeGUI();
	}

	@Override protected void initGUI() {
		JPanel pane5 = new JPanel(new BorderLayout(0, 0));

		displayCondition = new ProcedureSelector(this.withEntry("overlay/display_condition"), mcreator,
				L10N.t("elementgui.overlay.event_display_ingame"), ProcedureSelector.Side.CLIENT, true,
				VariableTypeLoader.BuiltInTypes.LOGIC,
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity"));

		editor = new WYSIWYGEditor(mcreator, false);

		editor.ovst.add(displayCondition);

		editor.setPreferredSize(new Dimension(5, 550));

		pane5.setOpaque(false);
		pane5.add("Center", editor);

		addPage(pane5, false);
	}

	@Override public void reloadDataLists() {
		super.reloadDataLists();

		editor.overlayBaseTexture.reload();

		displayCondition.refreshListKeepSelected();
	}

	@Override public void openInEditingMode(Overlay overlay) {
		editor.priority.setSelectedItem(overlay.priority);
		editor.setComponentList(overlay.components);
		editor.overlayBaseTexture.setTextureFromTextureName(overlay.baseTexture);
		editor.overlayTarget.setSelectedItem(overlay.overlayTarget);
		displayCondition.setSelectedProcedure(overlay.displayCondition);

		editor.sx.setValue(overlay.gridSettings.sx);
		editor.sy.setValue(overlay.gridSettings.sy);
		editor.ox.setValue(overlay.gridSettings.ox);
		editor.oy.setValue(overlay.gridSettings.oy);
		editor.snapOnGrid.setSelected(overlay.gridSettings.snapOnGrid);
		if (overlay.gridSettings.snapOnGrid) {
			editor.editor.showGrid = true;
			editor.editor.repaint();
		}
	}

	@Override public Overlay getElementFromGUI() {
		Overlay overlay = new Overlay(modElement);
		overlay.priority = editor.priority.getSelectedItem();
		overlay.components = editor.getComponentList();
		overlay.baseTexture = editor.overlayBaseTexture.getTextureName();
		overlay.overlayTarget = editor.overlayTarget.getSelectedItem();
		overlay.displayCondition = displayCondition.getSelectedProcedure();

		overlay.gridSettings.sx = (int) editor.sx.getValue();
		overlay.gridSettings.sy = (int) editor.sy.getValue();
		overlay.gridSettings.ox = (int) editor.ox.getValue();
		overlay.gridSettings.oy = (int) editor.oy.getValue();
		overlay.gridSettings.snapOnGrid = editor.snapOnGrid.isSelected();
		return overlay;
	}

	@Override public @Nullable URI contextURL() throws URISyntaxException {
		return new URI(MCreatorApplication.SERVER_DOMAIN + "/wiki/how-make-overlay");
	}

}