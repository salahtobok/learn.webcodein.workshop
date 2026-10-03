package com.webcodein.workshop.archunit;
import org.junit.jupiter.api.Test;
import java.net.URL;
import java.util.Enumeration;
public class DebugTest {
    @Test
    public void test() throws Exception {
        Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources("com/webcodein/workshop");
        System.out.println("RESOURCES:");
        while(urls.hasMoreElements()) {
            System.out.println(urls.nextElement());
        }
    }
}
