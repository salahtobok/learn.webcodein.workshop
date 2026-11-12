package com.webcodein.workshop.saga;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import io.temporal.activity.ActivityInterface;
import io.temporal.workflow.WorkflowInterface;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "com.webcodein.workshop.saga", importOptions = ImportOption.DoNotIncludeTests.class)
public class SagaArchitectureTest {

    @ArchTest
    static final ArchRule activityInterfacesShouldBeAnnotated =
            classes()
                    .that().haveSimpleNameEndingWith("Activities")
                    .and().areInterfaces()
                    .should().beAnnotatedWith(ActivityInterface.class)
                    .as("Temporal Activity interfaces must be annotated with @ActivityInterface");

    @ArchTest
    static final ArchRule workflowInterfacesShouldBeAnnotated =
            classes()
                    .that().haveSimpleNameEndingWith("Workflow")
                    .and().areInterfaces()
                    .should().beAnnotatedWith(WorkflowInterface.class)
                    .as("Temporal Workflow interfaces must be annotated with @WorkflowInterface");

    @ArchTest
    static final ArchRule controllersShouldNotDependOnWorkflowImpl =
            noClasses()
                    .that().areAnnotatedWith(RestController.class)
                    .should().dependOnClassesThat().haveSimpleNameEndingWith("WorkflowImpl")
                    .as("Controllers must depend on workflow stubs/clients, never on workflow implementation classes directly");

    @ArchTest
    static final ArchRule activitiesImplementationsShouldImplementActivityInterface =
            classes()
                    .that().haveSimpleNameEndingWith("ActivitiesImpl")
                    .should().implement(OrderActivities.class)
                    .as("Activity implementations must implement OrderActivities interface");
}
