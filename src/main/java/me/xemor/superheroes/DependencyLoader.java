package me.xemor.superheroes;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class DependencyLoader implements PluginLoader {
    @Override
    public void classloader(PluginClasspathBuilder classpathBuilder) {
        // Check if SkillsLibrary2 plugin exists (will provide its own Jackson)
        boolean skillsLibraryExists = checkForSkillsLibrary();

        // Check if Jackson is already loaded
        boolean jacksonAlreadyLoaded = false;
        try {
            Class.forName("com.fasterxml.jackson.dataformat.yaml.YAMLFactory");
            jacksonAlreadyLoaded = true;
        } catch (ClassNotFoundException ignored) {
            // Dependencies not loaded
        }

        // Only load Jackson if SkillsLibrary doesn't exist and Jackson isn't already loaded
        if (!skillsLibraryExists && !jacksonAlreadyLoaded) {
            MavenLibraryResolver resolver = new MavenLibraryResolver();
            List<String> mavenCentralDependencies =
                    List.of(
                            "com.fasterxml.jackson.core:jackson-databind:2.18.2",
                            "com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.7.0"
                    );
            for (String dependency : mavenCentralDependencies) {
                resolver.addDependency(new Dependency(new DefaultArtifact(dependency), null));
            }

            resolver.addRepository(new RemoteRepository.Builder("central", "default", MavenLibraryResolver.MAVEN_CENTRAL_DEFAULT_MIRROR).build());

            classpathBuilder.addLibrary(resolver);
        }
    }

    private boolean checkForSkillsLibrary() {
        try {
            // Get the directory where this plugin jar is located
            File jarFile = new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
            File pluginsDir = jarFile.getParentFile();

            if (pluginsDir == null || !pluginsDir.exists()) {
                return false;
            }

            // Search for any file containing "skillslibrary" (case-insensitive)
            try (Stream<Path> paths = Files.walk(pluginsDir.toPath())) {
                return paths
                        .filter(Files::isRegularFile)
                        .map(Path::getFileName)
                        .map(Path::toString)
                        .anyMatch(name -> name.toLowerCase().contains("skillslibrary"));
            }
        } catch (Exception e) {
            // If we can't determine, assume it doesn't exist
            return false;
        }
    }
}
