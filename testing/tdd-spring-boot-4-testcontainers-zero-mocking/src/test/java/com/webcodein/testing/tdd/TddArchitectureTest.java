package com.webcodein.testing.tdd;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING;

@AnalyzeClasses(packages = "com.webcodein.testing.tdd", importOptions = ImportOption.DoNotIncludeTests.class)
public class TddArchitectureTest {

    @ArchTest
    static final ArchRule controllers_should_be_annotated_and_named =
            classes().that().haveSimpleNameEndingWith("Controller")
                    .should().beAnnotatedWith(RestController.class);

    @ArchTest
    static final ArchRule services_should_be_annotated_and_named =
            classes().that().haveSimpleNameEndingWith("Service")
                    .should().beAnnotatedWith(Service.class);

    @ArchTest
    static final ArchRule repositories_should_be_interfaces =
            classes().that().haveSimpleNameEndingWith("Repository")
                    .should().beInterfaces();

    @ArchTest
    static final ArchRule no_standard_streams =
            NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS;

    @ArchTest
    static final ArchRule no_java_util_logging =
            NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING;
}
