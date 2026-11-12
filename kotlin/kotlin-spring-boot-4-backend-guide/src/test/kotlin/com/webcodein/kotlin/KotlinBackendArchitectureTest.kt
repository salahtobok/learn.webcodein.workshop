package com.webcodein.kotlin

import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.library.GeneralCodingRules
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RestController

@AnalyzeClasses(packages = ["com.webcodein.kotlin"], importOptions = [ImportOption.DoNotIncludeTests::class])
class KotlinBackendArchitectureTest {

    @ArchTest
    val controllersMustBeAnnotatedWithRestController: ArchRule = classes()
        .that().haveSimpleNameEndingWith("Controller")
        .should().beAnnotatedWith(RestController::class.java)
        .because("REST API controllers must be annotated with @RestController")

    @ArchTest
    val servicesMustBeAnnotatedWithService: ArchRule = classes()
        .that().haveSimpleNameEndingWith("Service")
        .should().beAnnotatedWith(Service::class.java)
        .because("Domain services must be annotated with @Service")

    @ArchTest
    val noClassesShouldAccessStandardStreams: ArchRule = GeneralCodingRules.NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS
        .because("Production classes should use SLF4J loggers instead of standard streams")

    @ArchTest
    val noClassesShouldUseJavaUtilLogging: ArchRule = GeneralCodingRules.NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING
        .because("Production classes should use standard SLF4J logging")

    @ArchTest
    val servicesMustResideInUserPackage: ArchRule = classes()
        .that().haveSimpleNameEndingWith("Service")
        .should().resideInAPackage("..user..")
        .because("Business services must reside in their feature package")
}
