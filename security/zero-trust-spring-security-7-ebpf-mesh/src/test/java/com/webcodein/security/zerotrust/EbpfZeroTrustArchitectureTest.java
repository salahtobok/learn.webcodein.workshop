package com.webcodein.security.zerotrust;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING;

@AnalyzeClasses(packages = "com.webcodein.security.zerotrust", importOptions = ImportOption.DoNotIncludeTests.class)
public class EbpfZeroTrustArchitectureTest {

    @ArchTest
    static final ArchRule controllers_should_be_annotated_and_named =
            classes().that().resideInAPackage("..controller..")
                    .should().beAnnotatedWith(RestController.class)
                    .andShould().haveSimpleNameEndingWith("Controller");

    @ArchTest
    static final ArchRule services_should_be_annotated_and_named =
            classes().that().resideInAPackage("..service..")
                    .should().beAnnotatedWith(Service.class)
                    .andShould().haveSimpleNameEndingWith("Service");

    @ArchTest
    static final ArchRule controllers_should_not_depend_on_repositories =
            classes().that().resideInAPackage("..controller..")
                    .should().onlyDependOnClassesThat()
                    .resideInAnyPackage(
                            "com.webcodein.security.zerotrust..",
                            "org.springframework..",
                            "java..",
                            "jakarta.."
                    );

    @ArchTest
    static final ArchRule no_standard_streams =
            NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS;

    @ArchTest
    static final ArchRule no_java_util_logging =
            NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING;
}
