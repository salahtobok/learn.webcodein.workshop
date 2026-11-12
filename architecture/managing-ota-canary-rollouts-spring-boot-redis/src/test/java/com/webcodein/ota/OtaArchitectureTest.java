package com.webcodein.ota;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.GeneralCodingRules.*;

@AnalyzeClasses(packages = "com.webcodein.ota", importOptions = ImportOption.DoNotIncludeTests.class)
public class OtaArchitectureTest {

    @ArchTest
    static final ArchRule controllers_should_be_annotated_with_rest_controller =
            classes()
                    .that().haveSimpleNameEndingWith("Controller")
                    .should().beAnnotatedWith(RestController.class)
                    .because("REST presentation endpoints must be annotated with @RestController");

    @ArchTest
    static final ArchRule services_should_be_annotated_with_service =
            classes()
                    .that().haveSimpleNameEndingWith("Service")
                    .should().beAnnotatedWith(Service.class)
                    .because("Application services must be annotated with @Service");

    @ArchTest
    static final ArchRule no_field_injection =
            NO_CLASSES_SHOULD_USE_FIELD_INJECTION
                    .because("Constructor injection ensures immutability and testability");

    @ArchTest
    static final ArchRule no_generic_exceptions =
            NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS
                    .because("Precise standard or custom domain exceptions must be thrown");

    @ArchTest
    static final ArchRule model_classes_should_be_records =
            classes()
                    .that().resideInAPackage("..model..")
                    .should().beRecords()
                    .because("Data transfer objects and domain models should be immutable Java records");
}
