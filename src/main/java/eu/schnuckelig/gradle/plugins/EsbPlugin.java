/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.schnuckelig.gradle.plugins;

import java.util.concurrent.Callable;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.file.FileCollection;
import org.gradle.api.internal.artifacts.publish.ArchivePublishArtifact;
import org.gradle.api.internal.plugins.DefaultArtifactPublicationSet;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.SourceSet;

import eu.schnuckelig.gradle.plugins.Esb;
import eu.schnuckelig.gradle.plugins.EsbPluginConvention;

/**
 * @author Thorsten Klein
 * 
 */
public class EsbPlugin implements Plugin<Project> {

	public static final String PROVIDED_COMPILE_CONFIGURATION_NAME = "providedCompile";
	public static final String PROVIDED_RUNTIME_CONFIGURATION_NAME = "providedRuntime";
	public static final String ESB_TASK_NAME = "esb";

	@Override
	public void apply(final Project project) {
		project.getPlugins().apply(JavaPlugin.class);
		final EsbPluginConvention pluginConvention = new EsbPluginConvention(
				project);
		project.getConvention().getPlugins().put("esb", pluginConvention);

		project.getTasks().withType(Esb.class, new Action<Esb>() {
			public void execute(Esb task) {
				task.from(new Callable() {
					public Object call() throws Exception {
						return pluginConvention.getEsbContentDir();
					}
				});
				task.dependsOn(new Callable() {
					public Object call() throws Exception {
						return project.getConvention()
								.getPlugin(JavaPluginConvention.class)
								.getSourceSets()
								.getByName(SourceSet.MAIN_SOURCE_SET_NAME)
								.getRuntimeClasspath();
					}
				});
				task.classpath(new Object[] { new Callable() {
					public Object call() throws Exception {
						FileCollection runtimeClasspath = project
								.getConvention()
								.getPlugin(JavaPluginConvention.class)
								.getSourceSets()
								.getByName(SourceSet.MAIN_SOURCE_SET_NAME)
								.getRuntimeClasspath();
						Configuration providedRuntime = project
								.getConfigurations().getByName(
										PROVIDED_RUNTIME_CONFIGURATION_NAME);
						return runtimeClasspath.minus(providedRuntime);
					}
				} });
			}
		});

		Esb esb = project.getTasks().add(ESB_TASK_NAME, Esb.class);
		esb.setDescription("Generates an ESB archive with all the compiled classes, the ESB content and the libraries.");
		esb.setGroup(BasePlugin.BUILD_GROUP);
		project.getExtensions().getByType(DefaultArtifactPublicationSet.class)
				.addCandidate(new ArchivePublishArtifact(esb));
		configureConfigurations(project.getConfigurations());
	}

	private void configureConfigurations(
			ConfigurationContainer configurationContainer) {
		Configuration provideCompileConfiguration = configurationContainer
				.add(PROVIDED_COMPILE_CONFIGURATION_NAME)
				.setVisible(false)
				.setDescription(
						"Additional compile classpath for libraries that should not be part of the ESB archive.");
		
		Configuration provideRuntimeConfiguration = configurationContainer
				.add(PROVIDED_RUNTIME_CONFIGURATION_NAME)
				.setVisible(false)
				.extendsFrom(provideCompileConfiguration)
				.setDescription(
						"Additional runtime classpath for libraries that should not be part of the ESB archive.");
		
		configurationContainer.getByName(JavaPlugin.COMPILE_CONFIGURATION_NAME)
				.extendsFrom(provideCompileConfiguration);
		
		configurationContainer.getByName(JavaPlugin.RUNTIME_CONFIGURATION_NAME)
				.extendsFrom(provideRuntimeConfiguration);

	}

}
