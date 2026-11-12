package com.webcodein.workshop.ota;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import jakarta.persistence.Entity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "com.webcodein.workshop.ota", importOptions = ImportOption.DoNotIncludeTests.class)
class OtaArchitectureTest {

    @ArchTest
    static final ArchRule controllers_must_be_annotated_with_rest_controller =
            classes()
                    .that().haveSimpleNameEndingWith("Controller")
                    .should().beAnnotatedWith(RestController.class);

    @ArchTest
    static final ArchRule services_must_be_annotated_with_service =
            classes()
                    .that().haveSimpleNameEndingWith("Service")
                    .should().beAnnotatedWith(Service.class);

    @ArchTest
    static final ArchRule repositories_must_be_interfaces =
            classes()
                    .that().haveSimpleNameEndingWith("Repository")
                    .should().beInterfaces();

    @ArchTest
    static final ArchRule entities_must_be_annotated_with_entity =
            classes()
                    .that().haveSimpleNameEndingWith("Patch")
                    .or().haveSimpleNameEndingWith("Version")
                    .should().beAnnotatedWith(Entity.class);

    @ArchTest
    static final ArchRule no_classes_should_use_legacy_date =
            noClasses()
                    .should().dependOnClassesThat().haveFullyQualifiedName("java.util.Date")
                    .because("Modern Java 25 applications must use java.time types like Instant or LocalDateTime");
}
