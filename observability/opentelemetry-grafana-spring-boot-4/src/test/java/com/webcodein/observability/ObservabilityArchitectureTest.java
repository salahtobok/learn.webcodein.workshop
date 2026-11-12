package com.webcodein.observability;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.GeneralCodingRules;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

@AnalyzeClasses(packages = "com.webcodein.observability", importOptions = ImportOption.DoNotIncludeTests.class)
public class ObservabilityArchitectureTest {

    @ArchTest
    static final ArchRule controllersMustResideInControllerPackage = classes()
            .that().haveSimpleNameEndingWith("Controller")
            .should().resideInAPackage("..controller..")
            .because("REST API controllers must be organized in the controller package");

    @ArchTest
    static final ArchRule controllersMustBeAnnotatedWithRestController = classes()
            .that().resideInAPackage("..controller..")
            .and().haveSimpleNameEndingWith("Controller")
            .should().beAnnotatedWith(RestController.class)
            .because("Controller classes must carry the @RestController annotation");

    @ArchTest
    static final ArchRule servicesMustBeAnnotatedWithService = classes()
            .that().haveSimpleNameEndingWith("Service")
            .should().beAnnotatedWith(Service.class)
            .because("Domain and observation services must carry the @Service annotation");

    @ArchTest
    static final ArchRule noClassesShouldAccessStandardStreams = GeneralCodingRules.NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS
            .because("Production classes should use SLF4J loggers instead of System.out/err");

    @ArchTest
    static final ArchRule noClassesShouldUseJavaUtilLogging = GeneralCodingRules.NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING
            .because("Production classes should use standard SLF4J logging");
}
