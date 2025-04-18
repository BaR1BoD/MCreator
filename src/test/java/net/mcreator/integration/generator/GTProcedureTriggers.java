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

package net.mcreator.integration.generator;

import net.mcreator.blockly.data.BlocklyLoader;
import net.mcreator.blockly.data.ExternalTrigger;
import net.mcreator.element.ModElementType;
import net.mcreator.element.types.Procedure;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;
import org.apache.logging.log4j.Logger;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.fail;

public class GTProcedureTriggers {

	public static void runTest(Logger LOG, String generatorName, Workspace workspace) {
		Set<String> generatorTriggers = workspace.getGeneratorStats().getProcedureTriggers();

		for (ExternalTrigger externalTrigger : BlocklyLoader.INSTANCE.getExternalTriggerLoader()
				.getExternalTriggers()) {
			if (!generatorTriggers.contains(externalTrigger.getID())) {
				continue;
			}

			if (externalTrigger.required_apis != null) {
				boolean skip = false;

				for (String required_api : externalTrigger.required_apis) {
					if (!workspace.getWorkspaceSettings().getMCreatorDependencies().contains(required_api)) {
						skip = true;
						break;
					}
				}

				if (skip)
					continue;
			}

			ModElement modElement = new ModElement(workspace, "TestTrigger" + externalTrigger.getID(),
					ModElementType.PROCEDURE);

			Procedure procedure = new Procedure(modElement);

			if (externalTrigger.dependencies_provided != null) {
				procedure.getModElement().putMetadata("dependencies", externalTrigger.dependencies_provided);
				procedure.skipDependencyRegeneration();
			}

			String additionalXML = "";
			if (externalTrigger.has_result) {
				additionalXML = "<next><block type=\"set_event_result\"><field name=\"result\">DENY</field></block></next>";
			} else if (externalTrigger.cancelable) {
				additionalXML = "<next><block type=\"cancel_event\"></block></next>";
			}

			procedure.procedurexml = """
					<xml xmlns="https://developers.google.com/blockly/xml">
						<block type="event_trigger">
							<field name="trigger">%s</field>
							%s
						</block>
					</xml>
					""".formatted(externalTrigger.getID(), additionalXML);

			try {
				workspace.addModElement(modElement);
				workspace.getGenerator().generateElement(procedure, true);
				workspace.getModElementManager().storeModElement(procedure);
			} catch (Throwable t) {
				fail("[" + generatorName + "] Failed generating external trigger: " + externalTrigger.getID(), t);
			}
		}

	}

}
