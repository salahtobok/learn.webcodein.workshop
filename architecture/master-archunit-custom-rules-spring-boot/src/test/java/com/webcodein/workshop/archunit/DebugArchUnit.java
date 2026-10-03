package com.webcodein.workshop.archunit;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
public class DebugArchUnit {
    public static void main(String[] args) {
        JavaClasses classes = new ClassFileImporter().importPackages("com.webcodein.workshop.archunit");
        System.out.println("Imported classes count: " + classes.size());
    }
}
