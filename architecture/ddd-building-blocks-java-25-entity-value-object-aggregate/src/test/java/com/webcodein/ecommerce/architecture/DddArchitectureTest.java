package com.webcodein.ecommerce.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "com.webcodein.ecommerce", importOptions = ImportOption.DoNotIncludeTests.class)
class DddArchitectureTest {

    @ArchTest
    static final ArchRule controllers_should_reside_in_web_package =
            classes()
                    .that().haveSimpleNameEndingWith("Controller")
                    .should().resideInAPackage("..web..")
                    .andShould().beAnnotatedWith(RestController.class);

    @ArchTest
    static final ArchRule repositories_should_be_interfaces_and_reside_in_repository_package =
            classes()
                    .that().haveSimpleNameEndingWith("Repository")
                    .should().beInterfaces()
                    .andShould().resideInAPackage("..repository..")
                    .andShould().beAssignableTo(JpaRepository.class);

    @ArchTest
    static final ArchRule domain_should_not_depend_on_web_or_repository =
            noClasses()
                    .that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat().resideInAnyPackage("..web..", "..repository..");

    @ArchTest
    static final ArchRule entities_should_be_annotated_with_entity =
            classes()
                    .that().haveSimpleName("Order").or().haveSimpleName("OrderItem")
                    .should().beAnnotatedWith(Entity.class);

    @ArchTest
    static final ArchRule value_objects_should_be_records_and_annotated_with_embeddable =
            classes()
                    .that().haveSimpleName("Money")
                    .should().beRecords()
                    .andShould().beAnnotatedWith(Embeddable.class);
}
