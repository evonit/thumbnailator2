package net.evonit.thumbnailator2.util;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

import java.net.URL;
import java.net.URLClassLoader;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConfigurationsTest {

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    public static class Base {
        @BeforeEach
        public void resetConfiguration() {
            Configurations.init();
        }

        @AfterAll
        public static void clearConfiguration() {
            Configurations.clear();
        }
    }

    @Nested
    @ExtendWith(AllTruePropertiesTestExtension.class)
    public class AllTruePropertiesTest extends Base {
        @Test
        public void test() {
            for (Configurations config : Configurations.values()) {
                assertTrue(config.getBoolean());
            }
        }
    }

    @Nested
    @ExtendWith(AllFalsePropertiesTestExtension.class)
    public class AllFalsePropertiesTest extends Base {
        @Test
        public void test() {
            for (Configurations config : Configurations.values()) {
                assertFalse(config.getBoolean());
            }
        }
    }

    public static class PropertiesFileRedirectingClassLoader extends URLClassLoader {
        private final String resourceForPropertiesFile;

        public PropertiesFileRedirectingClassLoader(String resourceForPropertiesFile) {
            super(new URL[] {});
            this.resourceForPropertiesFile = resourceForPropertiesFile;
        }

        @Override
        public URL getResource(String name) {
            if (name.startsWith("thumbnailator.properties")) {
                return ClassLoader.getSystemClassLoader().getResource(resourceForPropertiesFile);
            }
            return super.getResource(name);
        }
    }
}

class AllTruePropertiesTestExtension implements TestInstancePostProcessor {
    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) {
        Thread.currentThread().setContextClassLoader(getCustomClassLoader());
    }

    protected ClassLoader getCustomClassLoader() {
        return new ConfigurationsTest.PropertiesFileRedirectingClassLoader("Configurations/all_true.txt");
    }
}

class AllFalsePropertiesTestExtension implements TestInstancePostProcessor {
    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) {
        Thread.currentThread().setContextClassLoader(getCustomClassLoader());
    }

    protected ClassLoader getCustomClassLoader() {
        return new ConfigurationsTest.PropertiesFileRedirectingClassLoader("Configurations/all_false.txt");
    }
}