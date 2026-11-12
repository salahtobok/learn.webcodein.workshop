package com.webcodein.security.rls.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

@AnalyzeClasses(
        packages = "com.webcodein.security.rls",
        importOptions = {ImportOption.DoNotIncludeTests.class}
)
public class RlsArchitectureTest {

    @ArchTest
    public static final ArchRule entitiesMustResideInEntityPackage =
            classes()
                    .that().areAnnotatedWith(jakarta.persistence.Entity.class)
                    .should().resideInAPackage("..entity..")
                    .because("JPA entities must reside in the dedicated entity package");

    @ArchTest
    public static final ArchRule controllersMustBeAnnotatedWithRestController =
            classes()
                    .that().resideInAPackage("..controller..")
                    .should().beAnnotatedWith(RestController.class)
                    .because("REST presentation endpoints must be annotated with @RestController");

    @ArchTest
    public static final ArchRule repositoriesMustBeInterfaces =
            classes()
                    .that().resideInAPackage("..repository..")
                    .should().beInterfaces()
                    .because("Spring Data repositories must be defined as interfaces");

    @ArchTest
    public static final ArchRule aspectsMustBeAnnotatedWithAspect =
            classes()
                    .that().haveSimpleNameEndingWith("Aspect")
                    .should().beAnnotatedWith(Aspect.class)
                    .because("AOP aspect components intercepting database sessions must carry @Aspect");
}
