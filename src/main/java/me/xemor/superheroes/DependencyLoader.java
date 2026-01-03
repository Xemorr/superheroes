package me.xemor.superheroes;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;

import java.util.List;

public class DependencyLoader implements PluginLoader {

    @Override
    public void classloader(PluginClasspathBuilder classpathBuilder) {
        MavenLibraryResolver resolver = new MavenLibraryResolver();
        List<String> mavenCentralDependencies =
                List.of(
                        "com.fasterxml.jackson.core:jackson-core:2.18.0",
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
