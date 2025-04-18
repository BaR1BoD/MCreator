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
import net.mcreator.element.types.Particle;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.component.JEmptyBox;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.TypedTextureSelectorDialog;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.minecraft.TextureSelectionButton;
import net.mcreator.ui.procedure.AbstractProcedureSelector;
import net.mcreator.ui.procedure.NumberProcedureSelector;
import net.mcreator.ui.procedure.ProcedureSelector;
import net.mcreator.ui.validation.validators.TextureSelectionButtonValidator;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.elements.VariableTypeLoader;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;

public class ParticleGUI extends ModElementGUI<Particle> {

	private TextureSelectionButton texture;

	private final JSpinner width = new JSpinner(new SpinnerNumberModel(0.2, 0, 4096, 0.1));
	private final JSpinner height = new JSpinner(new SpinnerNumberModel(0.2, 0, 4096, 0.1));
	private NumberProcedureSelector scale;
	private final JSpinner gravity = new JSpinner(new SpinnerNumberModel(0, -100, 100, 0.1));
	private final JSpinner speedFactor = new JSpinner(new SpinnerNumberModel(1, -100, 100, 0.1));
	private final JSpinner maxAge = new JSpinner(new SpinnerNumberModel(7, 0, 100000, 1));
	private final JSpinner maxAgeDiff = new JSpinner(new SpinnerNumberModel(0, 0, 100000, 1));
	private final JSpinner frameDuration = new JSpinner(new SpinnerNumberModel(1, 1, 100000, 1));
	private final JSpinner angularVelocity = new JSpinner(new SpinnerNumberModel(0, -100, 100, 0.01));
	private final JSpinner angularAcceleration = new JSpinner(new SpinnerNumberModel(0, -100, 100, 0.01));

	private final JCheckBox emissiveRendering = L10N.checkbox("elementgui.common.enable");
	private final JCheckBox canCollide = L10N.checkbox("elementgui.common.enable");
	private final JCheckBox alwaysShow = L10N.checkbox("elementgui.common.enable");
	private final JCheckBox animate = L10N.checkbox("elementgui.common.enable");

	private final JComboBox<String> renderType = new JComboBox<>(new String[] { "OPAQUE", "TRANSLUCENT" });

	private ProcedureSelector additionalExpiryCondition;

	public ParticleGUI(MCreator mcreator, ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		this.initGUI();
		super.finalizeGUI();
	}

	@Override protected void initGUI() {
		scale = new NumberProcedureSelector(this.withEntry("particle/scale"), mcreator,
				L10N.t("elementgui.particle.visual_scale"), AbstractProcedureSelector.Side.CLIENT,
				new JSpinner(new SpinnerNumberModel(1, 0.1, 4096, 0.1)), 0,
				Dependency.fromString("x:number/y:number/z:number/world:world/age:number/scale:number"));
		additionalExpiryCondition = new ProcedureSelector(this.withEntry("particle/additional_expiry_condition"),
				mcreator, L10N.t("elementgui.particle.expiry_condition"), ProcedureSelector.Side.CLIENT, true,
				VariableTypeLoader.BuiltInTypes.LOGIC, Dependency.fromString(
				"x:number/y:number/z:number/world:world/age:number/onGround:logic")).setDefaultName(
				L10N.t("condition.common.no_additional")).makeInline();

		JPanel pane3 = new JPanel(new BorderLayout());
		pane3.setOpaque(false);

		canCollide.setSelected(true);

		canCollide.setOpaque(false);
		alwaysShow.setOpaque(false);
		animate.setOpaque(false);

		texture = new TextureSelectionButton(new TypedTextureSelectorDialog(mcreator, TextureType.PARTICLE));
		texture.setOpaque(false);

		JComponent textureComponent = PanelUtils.totalCenterInPanel(ComponentUtils.squareAndBorder(
				HelpUtils.wrapWithHelpButton(this.withEntry("particle/texture"), texture),
				L10N.t("elementgui.common.texture")));

		JPanel spo2 = new JPanel(new GridLayout(13, 2, 2, 2));
		spo2.setOpaque(false);

		spo2.add(HelpUtils.wrapWithHelpButton(this.withEntry("particle/animated_texture"),
				L10N.label("elementgui.particle.animated_texture")));
		spo2.add(animate);

		spo2.add(HelpUtils.wrapWithHelpButton(this.withEntry("particle/animated_frame_duration"),
				L10N.label("elementgui.particle.animated_frame_duration")));
		spo2.add(frameDuration);

		spo2.add(HelpUtils.wrapWithHelpButton(this.withEntry("particle/render_type"),
				L10N.label("elementgui.particle.render_type")));
		spo2.add(renderType);

		spo2.add(HelpUtils.wrapWithHelpButton(this.withEntry("particle/emissive_rendering"),
				L10N.label("elementgui.common.emissive_rendering")));
		spo2.add(emissiveRendering);

		spo2.add(HelpUtils.wrapWithHelpButton(this.withEntry("particle/scale"),
				L10N.label("elementgui.particle.visual_scale")));
		spo2.add(scale);

		spo2.add(
				HelpUtils.wrapWithHelpButton(this.withEntry("particle/width"), L10N.label("elementgui.particle.bbox")));
		spo2.add(PanelUtils.gridElements(1, 2, 2, 2, width, height));

		spo2.add(HelpUtils.wrapWithHelpButton(this.withEntry("particle/speed_factor"),
				L10N.label("elementgui.particle.speed_factor")));
		spo2.add(speedFactor);

		spo2.add(HelpUtils.wrapWithHelpButton(this.withEntry("particle/angular_velocity"),
				L10N.label("elementgui.particle.angular_velocity")));
		spo2.add(angularVelocity);

		spo2.add(HelpUtils.wrapWithHelpButton(this.withEntry("particle/angular_acceleration"),
				L10N.label("elementgui.particle.angular_acceleration")));
		spo2.add(angularAcceleration);

		spo2.add(HelpUtils.wrapWithHelpButton(this.withEntry("particle/gravity"),
				L10N.label("elementgui.particle.gravity")));
		spo2.add(gravity);

		spo2.add(HelpUtils.wrapWithHelpButton(this.withEntry("particle/max_age"),
				L10N.label("elementgui.particle.max_age")));
		spo2.add(PanelUtils.gridElements(1, 2, 2, 2, maxAge, maxAgeDiff));

		spo2.add(HelpUtils.wrapWithHelpButton(this.withEntry("particle/always_show"),
				L10N.label("elementgui.particle.always_show")));
		spo2.add(alwaysShow);

		spo2.add(HelpUtils.wrapWithHelpButton(this.withEntry("particle/can_collide"),
				L10N.label("elementgui.particle.does_collide")));
		spo2.add(canCollide);

		pane3.add("Center", PanelUtils.totalCenterInPanel(PanelUtils.northAndCenterElement(textureComponent,
				PanelUtils.centerAndSouthElement(spo2,
						PanelUtils.westAndCenterElement(new JEmptyBox(3, 3), additionalExpiryCondition), 5, 2), 15,
				5)));

		texture.setValidator(new TextureSelectionButtonValidator(texture));

		addPage(L10N.t("elementgui.common.page_properties"), pane3).validate(texture);
	}

	@Override public void reloadDataLists() {
		super.reloadDataLists();

		additionalExpiryCondition.refreshListKeepSelected();
		scale.refreshListKeepSelected();
	}

	@Override public void openInEditingMode(Particle particle) {
		texture.setTexture(particle.texture);
		width.setValue(particle.width);
		height.setValue(particle.height);
		scale.setSelectedProcedure(particle.scale);
		gravity.setValue(particle.gravity);
		speedFactor.setValue(particle.speedFactor);
		frameDuration.setValue(particle.frameDuration);
		emissiveRendering.setSelected(particle.emissiveRendering);
		maxAge.setValue(particle.maxAge);
		maxAgeDiff.setValue(particle.maxAgeDiff);
		angularVelocity.setValue(particle.angularVelocity);
		angularAcceleration.setValue(particle.angularAcceleration);
		canCollide.setSelected(particle.canCollide);
		alwaysShow.setSelected(particle.alwaysShow);
		animate.setSelected(particle.animate);
		renderType.setSelectedItem(particle.renderType);
		additionalExpiryCondition.setSelectedProcedure(particle.additionalExpiryCondition);
	}

	@Override public Particle getElementFromGUI() {
		Particle particle = new Particle(modElement);
		particle.texture = texture.getTextureHolder();
		particle.width = (double) width.getValue();
		particle.height = (double) height.getValue();
		particle.scale = scale.getSelectedProcedure();
		particle.gravity = (double) gravity.getValue();
		particle.speedFactor = (double) speedFactor.getValue();
		particle.maxAge = (int) maxAge.getValue();
		particle.frameDuration = (int) frameDuration.getValue();
		particle.emissiveRendering = emissiveRendering.isSelected();
		particle.maxAgeDiff = (int) maxAgeDiff.getValue();
		particle.angularVelocity = (double) angularVelocity.getValue();
		particle.angularAcceleration = (double) angularAcceleration.getValue();
		particle.canCollide = canCollide.isSelected();
		particle.animate = animate.isSelected();
		particle.alwaysShow = alwaysShow.isSelected();
		particle.renderType = (String) renderType.getSelectedItem();
		particle.additionalExpiryCondition = additionalExpiryCondition.getSelectedProcedure();
		return particle;
	}

	@Override public @Nullable URI contextURL() throws URISyntaxException {
		return new URI(MCreatorApplication.SERVER_DOMAIN + "/wiki/how-make-particle");
	}

}
