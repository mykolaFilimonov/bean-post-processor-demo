package com.example.beanpostprocessordemo.postprocessor;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.beanpostprocessordemo.TrimmedAnnotationBeanPostProcessor;
import com.example.beanpostprocessordemo.postprocessor.configuration.TrimmingEnabledConfig;
import com.example.beanpostprocessordemo.postprocessor.configuration.TrimmingNotEnabledConfig;
import com.example.beanpostprocessordemo.postprocessor.service.TrimmedService;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EnableStringTrimmingTest {

    @Test
    @Order(1)
    @DisplayName("Annotation @EnableStringTrimming has target 'TYPE'")
    void enableStringTrimmingAnnotationHasCorrectTarget() {
        Optional<Class<? extends Annotation>> annotation = findAnnotationInPackage("EnableStringTrimming");
        Optional<ElementType[]> targetValues = annotation.map(a -> a.getAnnotation(Target.class)).map(Target::value);

        assertTrue(targetValues.isPresent());
        assertEquals(1, targetValues.get().length);
        assertThat(List.of(targetValues.get())).contains(ElementType.TYPE);

    }

    @Test
    @Order(2)
    @DisplayName("Annotation @EnableStringTrimming has retention 'RUNTIME'")
    void enableStringTrimmingAnnotationHasRuntimeRetention() {
        Optional<Class<? extends Annotation>> annotation = findAnnotationInPackage("EnableStringTrimming");
        Optional<Retention> retention = annotation.map(a -> a.getAnnotation(Retention.class))
                .filter(r -> r.value().equals(RetentionPolicy.RUNTIME));

        assertTrue(retention.isPresent());
    }

    @Test
    @Order(3)
    @DisplayName("Annotation @EnableStringTrimming imports StringTrimmingConfiguration")
    void enableStringTrimmingAnnotationImportsCorrectConfig() {
        Optional<Class<? extends Annotation>> annotation = findAnnotationInPackage("EnableStringTrimming");
        List<Class<?>> importValues = Arrays.stream(annotation
                        .map(a -> a.getAnnotation(Import.class))
                        .map(Import::value)
                        .orElseGet(() -> new Class<?>[0]))
                .toList();
        List<String> importNames = importValues.stream().map(Class::getSimpleName).collect(toList());
        assertThat(importNames).contains("StringTrimmingConfiguration");
    }

    @Test
    @Order(4)
    @DisplayName("StringTrimmingConfiguration is implemented")
    void stringTrimmingConfiguration() {
        Optional<Class<?>> configClass = findClassInPackage("StringTrimmingConfiguration");
        assertTrue(configClass.isPresent());
    }

    @Test
    @Order(5)
    @DisplayName("StringTrimmingConfiguration is not annotated by @Configuration Spring annotation.")
    void stringTrimmingConfigurationIsNotAnnotatedAsConfiguration() {
        Optional<Class<?>> configClass = findClassInPackage("StringTrimmingConfiguration");
        assertNull(configClass.get().getDeclaredAnnotation(Configuration.class));
    }

    @Test
    @Order(6)
    @DisplayName("StringTrimmingConfiguration declares a TrimmedAnnotationBeanPostProcessor bean")
    void stringTrimmingConfigurationCreatesBeanOfTrimmedAnnotationPostProcessor() {
        Optional<Class<?>> configClass = findClassInPackage("StringTrimmingConfiguration");

        Method[] methods =
                configClass.orElseThrow(() -> new NoSuchElementException("StringTrimmingConfiguration class is not found"))
                        .getDeclaredMethods();

        Optional<Method> postProcessorBeanMethod =
                Stream.of(methods).filter(m -> m.getReturnType().equals(TrimmedAnnotationBeanPostProcessor.class))
                        .findFirst();
        assertTrue(postProcessorBeanMethod.isPresent());
    }

    @Test
    @Order(7)
    @DisplayName("Methods of StringTrimmingConfiguration that provides TrimmedAnnotationBeanPostProcessor instance annotated with" +
            " @Bean annotation")
    void trimmedAnnotationBeanPostProcessorMethodMarkedAsBean() {
        Optional<Class<?>> configClass = findClassInPackage("StringTrimmingConfiguration");
        Method[] methods = configClass.orElseThrow(
                () -> new NoSuchElementException("StringTrimmingConfiguration class is not found")).getDeclaredMethods();
        Optional<Bean> beanAnnotation = Stream.of(methods)
                .filter(m -> m.getReturnType().equals(TrimmedAnnotationBeanPostProcessor.class))
                .map(m -> m.getAnnotation(Bean.class)).findFirst();
        assertTrue(beanAnnotation.isPresent());
    }

    @Nested
    @SpringJUnitConfig(classes = {TrimmingEnabledConfig.class})
    class TrimmingEnabledTest {

        @Test
        @DisplayName("TrimmedAnnotationBeanPostProcessor trims String input params marked with @Trimmed")
        void trimmedAnnotationPostProcessorAnnotatedInputParams(@Autowired TrimmedService service) {
            String inputArgs = " Simba Bimba  ";
            String actual = service.getTheTrimmedString(inputArgs);

            assertEquals(inputArgs.trim(), actual);
        }

        @Test
        @DisplayName("TrimmedAnnotationBeanPostProcessor trims String input params marked with @Trimmed and ignore not annotated")
        void trimmedAnnotationPostProcessorAnnotatedAndNotAnnotatedInputParams(@Autowired TrimmedService service) {
            String inputArgs = " Simba Bimba  ";
            String inputArgs2 = " Timon Lemon  ";
            String actual = service.getTheTrimmedStringWithTwoArgs(inputArgs, inputArgs2);

            assertEquals(inputArgs.trim().concat(inputArgs2), actual);
        }

        @Test
        @DisplayName("TrimmedAnnotationBeanPostProcessor trims several String input params marked with @Trimmed and ignore not annotated")
        void trimmedAnnotationPostProcessorSeveralAnnotatedAndNotAnnotatedInputParams(@Autowired TrimmedService service) {
            String inputArgs = " Simba Bimba  ";
            String inputArgs2 = " Timon Lemon  ";
            String inputArgs3 = " Pumba Lumba  ";
            String actual = service.getTheTrimmedStringWithThreeArgs(inputArgs, inputArgs2, inputArgs3);

            assertEquals(inputArgs.trim().concat(inputArgs2).concat(inputArgs3.trim()), actual);
        }

        @Test
        void severalAnnotationPostProcessors(@Autowired TrimmedService service) {
            String inputArgs = " Simba Bimba  ";
            String actual = service.getTheTrimmedInteger2(inputArgs, 5);

            assertEquals(String.valueOf(25).concat(inputArgs.trim()), actual);
        }

        @Test
        @DisplayName("TrimmedAnnotationBeanPostProcessor not trims non-String input params marked with @Trimmed")
        void trimmedAnnotationPostProcessorAnnotatedNonStringInputParams(@Autowired TrimmedService service) {
            Integer inputArg = 1;
            assertDoesNotThrow(() -> service.getTheTrimmedInteger(inputArg), "Annotated parameter must ignore non-String types");
        }

        @Test
        @DisplayName("TrimmedAnnotationBeanPostProcessor not trims String input params that not marked by @Trimmed")
        void trimmedAnnotationPostProcessorNotAnnotatedInputParams(@Autowired TrimmedService service) {
            String inputArgs = "  Simba Bimba  ";
            String actual = service.getNotTrimmedString(inputArgs);

            assertEquals(inputArgs, actual);
        }
    }

    @Nested
    @SpringJUnitConfig(classes = TrimmingNotEnabledConfig.class)
    class TrimmingNotEnabledTest {

        @Test
        @DisplayName(
                "TrimmedAnnotationBeanPostProcessor does not trims String input parameters marked by " +
                        "@Trimmed if trimming is not enabled by @EnableStringTrimming")
        void trimmedAnnotationPostProcessorAnnotatedInputParams(@Autowired TrimmedService service) {
            String inputArgs = "   Mufasa LionKing  ";
            String actual = service.getTheTrimmedString(inputArgs);

            assertEquals(inputArgs, actual);
        }
    }

    private Optional<Class<?>> findClassInPackage(String className) {
        String packageName = TrimmedAnnotationBeanPostProcessor.class.getPackageName();
        Reflections reflections = new Reflections(packageName, new SubTypesScanner(false));
        return reflections.getSubTypesOf(Object.class)
                .stream()
                .filter(c -> c.getSimpleName().equals(className))
                .findFirst();
    }

    private Optional<Class<? extends Annotation>> findAnnotationInPackage(String annotationName) {
        String packageName = TrimmedAnnotationBeanPostProcessor.class.getPackageName();
        Reflections reflections = new Reflections(packageName, new SubTypesScanner(false));
        return reflections.getSubTypesOf(Annotation.class)
                .stream()
                .filter(a -> a.getSimpleName().equals(annotationName))
                .findFirst();
    }
}
