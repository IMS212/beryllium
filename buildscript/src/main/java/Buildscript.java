import io.github.coolcrabs.brachyura.decompiler.BrachyuraDecompiler;
import io.github.coolcrabs.brachyura.decompiler.fernflower.FernflowerDecompiler;
import io.github.coolcrabs.brachyura.fabric.*;
import io.github.coolcrabs.brachyura.fabric.FabricContext.ModDependencyCollector;
import io.github.coolcrabs.brachyura.fabric.FabricContext.ModDependencyFlag;
import io.github.coolcrabs.brachyura.maven.Maven;
import io.github.coolcrabs.brachyura.maven.MavenId;
import io.github.coolcrabs.brachyura.minecraft.Minecraft;
import io.github.coolcrabs.brachyura.minecraft.VersionMeta;
import io.github.coolcrabs.brachyura.quilt.QuiltMaven;
import net.fabricmc.mappingio.tree.MappingTree;

public class Buildscript extends SimpleFabricProject {

	@Override
	public String getModId() {
		return Properties.MOD_ID;
	}

	@Override
	public String getVersion() {
		return Properties.MOD_VERSION;
	}

	@Override
	public VersionMeta createMcVersion() {
		return Minecraft.getVersion(Properties.MINECRAFT);
	}

	@Override
	public MappingTree createMappings() {
		return Yarn.ofMaven(FabricMaven.URL, FabricMaven.yarn(Properties.YARN_MAPPINGS)).tree;
	}

	@Override
	public FabricLoader getLoader() {
		return new FabricLoader(FabricMaven.URL, FabricMaven.loader(Properties.FABRIC_LOADER));
	}

	@Override
	public void getModDependencies(ModDependencyCollector d) {
		d.addMaven(FabricMaven.URL, new MavenId(FabricMaven.GROUP_ID + ".fabric-api", "fabric-api-base", "0.4.11+e62f51a390"), ModDependencyFlag.RUNTIME, ModDependencyFlag.COMPILE);
		d.addMaven(FabricMaven.URL, new MavenId(FabricMaven.GROUP_ID + ".fabric-api", "fabric-command-api-v2", "2.1.7+0c17ea9690"), ModDependencyFlag.RUNTIME, ModDependencyFlag.COMPILE);
		d.addMaven(FabricMaven.URL, new MavenId(FabricMaven.GROUP_ID + ".fabric-api", "fabric-convention-tags-v1", "1.1.1+7cd20a1490"), ModDependencyFlag.RUNTIME, ModDependencyFlag.COMPILE);
		d.addMaven(FabricMaven.URL, new MavenId(FabricMaven.GROUP_ID + ".fabric-api", "fabric-crash-report-info-v1", "0.2.5+aeb40ebe90"), ModDependencyFlag.RUNTIME, ModDependencyFlag.COMPILE);
		d.addMaven(FabricMaven.URL, new MavenId(FabricMaven.GROUP_ID + ".fabric-api", "fabric-dimensions-v1", "2.1.30+aeb40ebe90"), ModDependencyFlag.RUNTIME, ModDependencyFlag.COMPILE);
		d.addMaven(FabricMaven.URL, new MavenId(FabricMaven.GROUP_ID + ".fabric-api", "fabric-entity-events-v1", "1.4.18+9ff28f4090"), ModDependencyFlag.RUNTIME, ModDependencyFlag.COMPILE);
		d.addMaven(FabricMaven.URL, new MavenId(FabricMaven.GROUP_ID + ".fabric-api", "fabric-events-interaction-v0", "0.4.28+aeb40ebe90"), ModDependencyFlag.RUNTIME, ModDependencyFlag.COMPILE);
		d.addMaven(FabricMaven.URL, new MavenId(FabricMaven.GROUP_ID + ".fabric-api", "fabric-game-rule-api-v1", "1.0.21+aeb40ebe90"), ModDependencyFlag.RUNTIME, ModDependencyFlag.COMPILE);
		d.addMaven(FabricMaven.URL, new MavenId(FabricMaven.GROUP_ID + ".fabric-api", "fabric-lifecycle-events-v1", "2.1.2+aeb40ebe90"), ModDependencyFlag.RUNTIME, ModDependencyFlag.COMPILE);
		d.addMaven(FabricMaven.URL, new MavenId(FabricMaven.GROUP_ID + ".fabric-api", "fabric-message-api-v1", "5.0.3+176380a290"), ModDependencyFlag.RUNTIME, ModDependencyFlag.COMPILE);
		d.addMaven(FabricMaven.URL, new MavenId(FabricMaven.GROUP_ID + ".fabric-api", "fabric-networking-api-v1", "1.2.4+5eb68ef290"), ModDependencyFlag.RUNTIME, ModDependencyFlag.COMPILE);
		d.addMaven(FabricMaven.URL, new MavenId(FabricMaven.GROUP_ID + ".fabric-api", "fabric-resource-loader-v0", "0.6.1+aeb40ebe90"), ModDependencyFlag.RUNTIME, ModDependencyFlag.COMPILE);

		d.addMaven("https://api.modrinth.com/maven/", new MavenId("maven.modrinth", "lazydfu", Properties.LAZY_DFU), ModDependencyFlag.RUNTIME);
		d.addMaven("https://api.modrinth.com/maven/", new MavenId("maven.modrinth", "ferrite-core", "5.0.0-fabric"), ModDependencyFlag.RUNTIME);
		d.addMaven("https://api.modrinth.com/maven/", new MavenId("maven.modrinth", "starlight", "1.1.1+1.19"), ModDependencyFlag.RUNTIME);
		d.addMaven("https://api.modrinth.com/maven/", new MavenId("maven.modrinth", "lithium", "mc1.19.2-0.8.3"), ModDependencyFlag.RUNTIME);
	}

	@Override
	public int getJavaVersion() {
		return 17;
	}

	@Override
	public BrachyuraDecompiler decompiler() {
		return new FernflowerDecompiler(Maven.getMavenJarDep(QuiltMaven.URL, new MavenId("org.quiltmc", "quiltflower", Properties.QUILTFLOWER)));
	}

}
