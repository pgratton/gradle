/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.api.internal.tasks.scala;

import org.gradle.api.file.FileCollection;
import org.gradle.language.base.internal.compile.Compiler;
import org.gradle.language.base.internal.compile.CompilerFactory;
import org.gradle.workers.WorkerExecutor;

import java.io.File;
import java.util.Set;

public class ScalaCompilerFactory implements CompilerFactory<ScalaJavaJointCompileSpec> {
    private final WorkerExecutor workerExecutor;
    private FileCollection scalaClasspath;
    private FileCollection zincClasspath;
    private final File gradleUserHomeDir;

    public ScalaCompilerFactory(WorkerExecutor workerExecutor, FileCollection scalaClasspath, FileCollection zincClasspath, File gradleUserHomeDir) {
        this.workerExecutor = workerExecutor;
        this.scalaClasspath = scalaClasspath;
        this.zincClasspath = zincClasspath;
        this.gradleUserHomeDir = gradleUserHomeDir;
    }

    public Compiler<ScalaJavaJointCompileSpec> newCompiler(ScalaJavaJointCompileSpec spec) {
        Set<File> scalaClasspathFiles = scalaClasspath.getFiles();
        Set<File> zincClasspathFiles = zincClasspath.getFiles();

        // currently, we leave it to ZincScalaCompiler to also compile the Java code
        Compiler<ScalaJavaJointCompileSpec> scalaCompiler = new DaemonScalaCompiler<ScalaJavaJointCompileSpec>(
            new ZincScalaCompiler(scalaClasspathFiles, zincClasspathFiles, gradleUserHomeDir),
            workerExecutor, zincClasspathFiles);
        return new NormalizingScalaCompiler(scalaCompiler);
    }
}
