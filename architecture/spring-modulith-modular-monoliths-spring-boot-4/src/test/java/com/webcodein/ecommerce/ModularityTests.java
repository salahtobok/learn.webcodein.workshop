package com.webcodein.ecommerce;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

class ModularityTests {

    @Test
    void verifiesModularStructure() {
        ApplicationModules modules = ApplicationModules.of(EcommerceApplication.class);
        modules.verify();
    }
    
    @Test
    void createModuleDocumentation() {
        ApplicationModules modules = ApplicationModules.of(EcommerceApplication.class);
        new Documenter(modules)
            .writeModulesAsPlantUml()
            .writeIndividualModulesAsPlantUml();
    }
}
