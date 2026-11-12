package com.webcodein.workshop;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.GeneralCodingRules;
import jakarta.persistence.Entity;
import org.springframework.data.repository.Repository;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

@AnalyzeClasses(packages = "com.webcodein.workshop", importOptions = ImportOption.DoNotIncludeTests.class)
public class DockerComposeArchitectureTest {

    @ArchTest
    static final ArchRule repositoriesMustBeInterfaces = classes()
            .that().areAssignableTo(Repository.class)
            .should().beInterfaces()
            .because("Spring Data repositories must be declared as interfaces");

    @ArchTest
    static final ArchRule entitiesMustBeAnnotatedWithEntity = classes()
            .that().resideInAPackage("..employee..")
            .and().haveSimpleNameEndingWith("Employee")
            .should().beAnnotatedWith(Entity.class)
            .because("Domain entities must be annotated with @Entity");

    @ArchTest
    static final ArchRule noClassesShouldAccessStandardStreams = GeneralCodingRules.NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS
            .because("Production classes should use SLF4J loggers instead of standard System.out/err streams");

    @ArchTest
    static final ArchRule noClassesShouldUseJavaUtilLogging = GeneralCodingRules.NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING
            .because("Production classes should use standard SLF4J logging");

    @ArchTest
    static final ArchRule entitiesMustResideInEmployeePackage = classes()
            .that().areAnnotatedWith(Entity.class)
            .should().resideInAPackage("..employee..")
            .because("JPA Entities must reside in the dedicated domain employee package");
}
