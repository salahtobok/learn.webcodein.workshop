package com.webcodein.shop;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import jakarta.persistence.Entity;
import org.springframework.data.repository.Repository;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

@AnalyzeClasses(packages = "com.webcodein.shop", importOptions = ImportOption.DoNotIncludeTests.class)
public class ShopArchitectureTest {

    @ArchTest
    static final ArchRule controllersShouldBeAnnotatedWithRestController =
            classes()
                    .that().haveSimpleNameEndingWith("Controller")
                    .should().beAnnotatedWith(RestController.class)
                    .andShould().resideInAPackage("..web..")
                    .as("REST endpoints must be annotated with @RestController and reside in web package");

    @ArchTest
    static final ArchRule repositoriesShouldBeInterfaces =
            classes()
                    .that().haveSimpleNameEndingWith("Repository")
                    .should().beInterfaces()
                    .andShould().beAssignableTo(Repository.class)
                    .as("Spring Data repositories must be interfaces extending Spring Data Repository");

    @ArchTest
    static final ArchRule mainEntitiesShouldBeAnnotatedWithEntity =
            classes()
                    .that().haveNameMatching(".*(Product|Employee|Order|AppUser)$")
                    .should().beAnnotatedWith(Entity.class)
                    .as("Core persistence models must be annotated with @Entity");

    @ArchTest
    static final ArchRule controllersShouldResideInWebPackage =
            classes()
                    .that().areAnnotatedWith(RestController.class)
                    .should().resideInAPackage("..web..")
                    .as("Controllers must reside in web package");
}
