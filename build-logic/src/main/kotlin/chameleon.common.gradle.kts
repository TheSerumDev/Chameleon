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
plugins {
    id("chameleon.base")
    id("chameleon.publishing")
    jacoco
}

/* Apply JUnit and setup */
val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

dependencies {
    /* JUnit */
    testImplementation(platform(libs.findLibrary("test-junit-bom").get()))
    testImplementation(libs.findLibrary("test-junit-api").get())
    testImplementation(libs.findLibrary("test-junit-engine").get())
    testImplementation(libs.findLibrary("test-junit-params").get())
    testRuntimeOnly(libs.findLibrary("test-junit-launcher").get())

    /* Truth */
    testImplementation(libs.findLibrary("test-truth").get())
    testImplementation(libs.findLibrary("test-truth-java8").get())

    /* Mockito */
    testImplementation(platform(libs.findLibrary("test-mockito-bom").get()))
    testImplementation(libs.findLibrary("test-mockito-core").get())
    testImplementation(libs.findLibrary("test-mockito-junit").get())
}

tasks {
    test {
        useJUnitPlatform()
        finalizedBy(jacocoTestReport)
    }

    jacocoTestReport {
        dependsOn(test)

        reports {
            html.required.set(true)
            csv.required.set(false)
            xml.required.set(true)
            xml.outputLocation.set(layout.buildDirectory.dir("reports/jacoco").get().file("report.xml"))
        }
    }
}
