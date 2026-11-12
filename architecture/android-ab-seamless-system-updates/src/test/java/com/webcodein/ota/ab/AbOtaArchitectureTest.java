package com.webcodein.ota.ab;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import jakarta.persistence.Entity;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

@AnalyzeClasses(packages = "com.webcodein.ota.ab", importOptions = ImportOption.DoNotIncludeTests.class)
public class AbOtaArchitectureTest {

    @ArchTest
    static final ArchRule controllersShouldBeAnnotatedWithRestController =
            classes()
                    .that().haveSimpleNameEndingWith("Controller")
                    .should().beAnnotatedWith(RestController.class)
                    .as("Classes ending with Controller must be annotated with @RestController");

    @ArchTest
    static final ArchRule entitiesShouldBeAnnotatedWithEntity =
            classes()
                    .that().haveSimpleName("DeviceSlotState")
                    .should().beAnnotatedWith(Entity.class)
                    .as("Domain state models must be annotated with @Entity");

    @ArchTest
    static final ArchRule repositoriesShouldBeInterfaces =
            classes()
                    .that().haveSimpleNameEndingWith("Repository")
                    .should().beInterfaces()
                    .as("Spring Data repositories must be interfaces");

    @ArchTest
    static final ArchRule controllersShouldResideInAbPackage =
            classes()
                    .that().areAnnotatedWith(RestController.class)
                    .should().resideInAPackage("..ota.ab..")
                    .as("Controllers must reside in the ota.ab package");
}
