/*
 * This file is a part of the Chameleon Framework, licensed under the MIT License.
 *
 * Copyright (c) 2021-2023 The Chameleon Framework Authors.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package dev.hypera.chameleon.annotations.processing;

import dev.hypera.chameleon.Chameleon;
import dev.hypera.chameleon.ChameleonPluginBootstrap;
import dev.hypera.chameleon.annotations.Plugin;
import dev.hypera.chameleon.annotations.exception.ChameleonAnnotationException;
import dev.hypera.chameleon.annotations.processing.generation.Generator;
import dev.hypera.chameleon.annotations.processing.generation.bukkit.BukkitGenerator;
import dev.hypera.chameleon.annotations.processing.generation.bungeecord.BungeeCordGenerator;
import dev.hypera.chameleon.annotations.processing.generation.folia.FoliaGenerator;
import dev.hypera.chameleon.annotations.processing.generation.nukkit.NukkitGenerator;
import dev.hypera.chameleon.annotations.processing.generation.sponge.SpongeGenerator;
import dev.hypera.chameleon.annotations.processing.generation.velocity.VelocityGenerator;
import dev.hypera.chameleon.platform.Platform;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Chameleon Annotation Processor.
 */
@SupportedAnnotationTypes({ "dev.hypera.chameleon.annotations.Plugin" })
public class ChameleonAnnotationProcessor extends AbstractProcessor {

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Plugin.class);
        if (!elements.isEmpty()) {
            if (elements.size() > 1) {
                throw new ChameleonAnnotationException("@Plugin cannot be used more than once");
            }

            Element element = elements.iterator().next();
            if (element.getKind() != ElementKind.CLASS || element.getModifiers().contains(Modifier.ABSTRACT)) {
                throw new ChameleonAnnotationException("@Plugin cannot be used on abstract classes");
            }

            TypeElement plugin = (TypeElement) element;
            Plugin data = plugin.getAnnotation(Plugin.class);

            // Plugin bootstrap
            TypeElement pluginBootstrap = retrieveBootstrap(plugin);

            // Platform "main class" generation
            for (String platform : data.platforms()) {
                Generator generator;
                switch (platform) {
                    case Platform.BUKKIT:
                        generator = new BukkitGenerator();
                        break;
                    case Platform.BUNGEECORD:
                        generator = new BungeeCordGenerator();
                        break;
                    case Platform.FOLIA:
                        generator = new FoliaGenerator();
                        break;
                    case Platform.NUKKIT:
                        generator = new NukkitGenerator();
                        break;
                    case Platform.SPONGE:
                        generator = new SpongeGenerator();
                        break;
                    case Platform.VELOCITY:
                        generator = new VelocityGenerator();
                        break;
                    default:
                        throw new IllegalStateException("Invalid or unknown platform: " + platform);
                }

                generator.generate(data, plugin, pluginBootstrap, processingEnv);
            }
        }

        return false;
    }

    private @Nullable TypeElement retrieveBootstrap(@NotNull TypeElement plugin) {
        TypeMirror bootstrapMirror = getBootstrapFromAnnotation(plugin);
        TypeElement bootstrapElement = bootstrapMirror != null ?
            (TypeElement) this.processingEnv.getTypeUtils().asElement(bootstrapMirror) : null;
        if (bootstrapElement != null && !bootstrapElement.toString().equals(ChameleonPluginBootstrap.class.getName())) {
            return bootstrapElement;
        }
        checkDefaultBootstrapConstructorPresent(plugin);
        return null;
    }

    private void checkDefaultBootstrapConstructorPresent(@NotNull TypeElement plugin) {
        // A plugin bootstrap class was not provided, make sure there is a public
        // constructor that has a single Chameleon parameter.
        Optional<ExecutableElement> constructor = plugin.getEnclosedElements()
            .parallelStream()
            .filter(e -> e.getKind() == ElementKind.CONSTRUCTOR && e.getModifiers().contains(Modifier.PUBLIC))
            .map(e -> (ExecutableElement) e)
            .filter(e -> e.getParameters().size() == 1)
            .filter(e -> this.processingEnv.getTypeUtils().asElement(e.getParameters().get(0).asType()).toString().equals(Chameleon.class.getName()))
            .findAny();
        if (constructor.isEmpty()) {
            throw new ChameleonAnnotationException(
                "If a plugin bootstrap is not provided to @Plugin, the annotated class must have a constructor with a single Chameleon parameter."
            );
        }
    }

    /**
     * Returns the bootstrap class type mirror from the Plugin annotation.
     *
     * @param typeElement Type element to read annotation on.
     *
     * @return bootstrap type mirror, if found, otherwise {@code null}.
     */
    private @Nullable TypeMirror getBootstrapFromAnnotation(@NotNull TypeElement typeElement) {
        AnnotationMirror mirror = getPluginAnnotationMirror(typeElement);
        if (mirror == null) {
            return null;
        }
        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : mirror.getElementValues().entrySet()) {
            if (entry.getKey().getSimpleName().toString().equals("bootstrap")) {
                return (TypeMirror) entry.getValue().getValue();
            }
        }
        return null;
    }

    private @Nullable AnnotationMirror getPluginAnnotationMirror(@NotNull TypeElement typeElement) {
        for (AnnotationMirror mirror : typeElement.getAnnotationMirrors()) {
            if (mirror.getAnnotationType().toString().equals(Plugin.class.getName())) {
                return mirror;
            }
        }
        return null;
    }

}
