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

package net.mcreator.generator.template.base;

import net.mcreator.generator.Generator;
import net.mcreator.generator.GeneratorConfiguration;
import net.mcreator.plugin.PluginLoader;
import net.mcreator.util.TestUtil;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused") public class FileProvider {

	private static final Logger LOG = LogManager.getLogger(FileProvider.class);

	private final Map<String, String> CACHE = new HashMap<>();

	private final GeneratorConfiguration generatorConfiguration;

	public FileProvider(@Nonnull Generator generator) {
		this.generatorConfiguration = generator.getGeneratorConfiguration();
	}

	public String file(@Nonnull String file) {
		return CACHE.computeIfAbsent(file, key -> {
			try {
				for (String path : generatorConfiguration.getGeneratorPaths(key)) {
					InputStream stream = PluginLoader.INSTANCE.getResourceAsStream(path);
					if (stream != null) {
						return IOUtils.toString(stream, StandardCharsets.UTF_8);
					}
				}
			} catch (Exception e) {
				LOG.error("Failed to load file provider for {}", key, e);
				TestUtil.failIfTestingEnvironment();
			}
			return null;
		});
	}

}
