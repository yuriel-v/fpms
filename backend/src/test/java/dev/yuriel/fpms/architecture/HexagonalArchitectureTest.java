package dev.yuriel.fpms.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Enforces the strict hexagonal (ports & adapters) dependency direction:
 * {@code adapter -> application -> domain}.
 *
 * <p>Rules are expressed with ArchUnit's {@code layeredArchitecture()} for the
 * inward dependency direction, plus explicit {@code noClasses()} rules that keep
 * the domain free of any framework (Spring, JPA/Jakarta persistence, Jackson).
 *
 * <p>The skeleton currently has essentially no real classes (only
 * {@code package-info.java}), so should-rules allow being empty via
 * {@code allowEmptyShould(true)}; the rules stay meaningful once real classes
 * arrive.
 */
@AnalyzeClasses(
        packages = "dev.yuriel.fpms",
        importOptions = ImportOption.DoNotIncludeTests.class)
class HexagonalArchitectureTest {

    /**
     * Layered architecture: dependencies point inward only.
     * <ul>
     *   <li>Domain is innermost and may be accessed by application, adapter and config.</li>
     *   <li>Application may access domain; may be accessed by adapter and config.</li>
     *   <li>Adapter may access application and domain; may be accessed only by config.</li>
     *   <li>Config wires everything and may be accessed by nothing.</li>
     * </ul>
     */
    @ArchTest
    static final ArchRule layered_architecture_points_inward = layeredArchitecture()
            .consideringOnlyDependenciesInLayers()
            .layer("Domain").definedBy("dev.yuriel.fpms.domain..")
            .layer("Application").definedBy("dev.yuriel.fpms.application..")
            .layer("Adapter").definedBy("dev.yuriel.fpms.adapter..")
            .layer("Config").definedBy("dev.yuriel.fpms.config..")

            .whereLayer("Config").mayNotBeAccessedByAnyLayer()
            .whereLayer("Adapter").mayOnlyBeAccessedByLayers("Config")
            .whereLayer("Application").mayOnlyBeAccessedByLayers("Adapter", "Config")
            .whereLayer("Domain").mayOnlyBeAccessedByLayers("Application", "Adapter", "Config")

            .as("hexagonal layers: dependencies must point inward (adapter -> application -> domain)")
            .allowEmptyShould(true);

    /**
     * The domain must not depend on the application layer.
     */
    @ArchTest
    static final ArchRule domain_does_not_depend_on_application =
            noClasses()
                    .that().resideInAPackage("dev.yuriel.fpms.domain..")
                    .should().dependOnClassesThat()
                    .resideInAPackage("dev.yuriel.fpms.application..")
                    .as("the domain must not depend on the application layer")
                    .allowEmptyShould(true);

    /**
     * The domain must not depend on any adapter.
     */
    @ArchTest
    static final ArchRule domain_does_not_depend_on_adapters =
            noClasses()
                    .that().resideInAPackage("dev.yuriel.fpms.domain..")
                    .should().dependOnClassesThat()
                    .resideInAPackage("dev.yuriel.fpms.adapter..")
                    .as("the domain must not depend on any adapter")
                    .allowEmptyShould(true);

    /**
     * The application layer must not depend on any adapter.
     */
    @ArchTest
    static final ArchRule application_does_not_depend_on_adapters =
            noClasses()
                    .that().resideInAPackage("dev.yuriel.fpms.application..")
                    .should().dependOnClassesThat()
                    .resideInAPackage("dev.yuriel.fpms.adapter..")
                    .as("the application layer must not depend on any adapter")
                    .allowEmptyShould(true);

    /**
     * The domain must stay pure Java: no Spring, JPA/Jakarta persistence,
     * or Jackson imports.
     */
    @ArchTest
    static final ArchRule domain_is_free_of_frameworks =
            noClasses()
                    .that().resideInAPackage("dev.yuriel.fpms.domain..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage(
                            "org.springframework..",
                            "jakarta.persistence..",
                            "javax.persistence..",
                            "com.fasterxml.jackson..")
                    .as("the domain must be framework-free (no Spring, JPA/Jakarta persistence, Jackson)")
                    .allowEmptyShould(true);
}
