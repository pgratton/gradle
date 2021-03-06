## New and noteworthy

Here are the new features introduced in this Gradle release.

<!--
IMPORTANT: if this is a patch release, ensure that a prominent link is included in the foreword to all releases of the same minor stream.
Add-->

### Public type for representing lazily evaluated properties

Because Gradle's build lifecycle clearly distinguishes between configuration phase and execution phase the evaluation of property
 values has to be deferred under certain conditions to properly capture end user input. A typical use case is the mapping of
 extension properties to custom task properties as part of a plugin implementation. In the past, many plugin developers were forced to solve evaluation order problems by using the concept of convention mapping, an internal API in Gradle subject to change.

This release of Gradle introduces a mutable type to the public API representing a property with state. The relevant interface is called [`PropertyState`](javadoc/org/gradle/api/provider/PropertyState.html). An instance of this type can be created through the method [`Project.property(Class)`](javadoc/org/gradle/api/Project.html#property-java.lang.Class-).

The following example demonstrates how to use the property state API to map an extension property to a custom task property without
running into evaluation ordering issues:

    apply plugin: GreetingPlugin

    greeting {
        message = 'Hi from Gradle'
        outputFiles = files('a.txt', 'b.txt')
    }

    class GreetingPlugin implements Plugin<Project> {
        void apply(Project project) {
            // Add the 'greeting' extension object
            def extension = project.extensions.create('greeting', GreetingPluginExtension, project)
            // Add a task that uses the configuration
            project.tasks.create('hello', Greeting) {
                message = extension.messageProvider
                outputFiles = extension.outputFiles
            }
        }
    }

    class GreetingPluginExtension {
        final PropertyState<String> message
        final ConfigurableFileCollection outputFiles

        GreetingPluginExtension(Project project) {
            message = project.property(String)
            setMessage('Hello from GreetingPlugin')
            outputFiles = project.files()
        }

        String getMessage() {
            message.get()
        }

        Provider<String> getMessageProvider() {
            message
        }

        void setMessage(String message) {
            this.message.set(message)
        }

        FileCollection getOutputFiles() {
            outputFiles
        }

        void setOutputFiles(FileCollection outputFiles) {
            this.outputFiles.setFrom(outputFiles)
        }
    }

    class Greeting extends DefaultTask {
        final PropertyState<String> message = project.property(String)
        final ConfigurableFileCollection outputFiles = project.files()

        @Input
        String getMessage() {
            message.get()
        }

        void setMessage(String message) {
            this.message.set(message)
        }

        void setMessage(Provider<String> message) {
            this.message.set(message)
        }

        FileCollection getOutputFiles() {
            outputFiles
        }

        void setOutputFiles(FileCollection outputFiles) {
            this.outputFiles.setFrom(outputFiles)
        }

        @TaskAction
        void printMessage() {
            getOutputFiles().each {
                it.text = getMessage()
            }
        }
    }

### Build Cache honors `--offline` 

When running with `--offline`, Gradle will disable the remote build cache.

### Default Zinc compiler upgraded from 0.3.7 to 0.3.13

This will take advantage of performance optimizations in the latest [Zinc](https://github.com/typesafehub/zinc) releases.

<!--
### Example new and noteworthy
-->

## Promoted features

Promoted features are features that were incubating in previous versions of Gradle but are now supported and subject to backwards compatibility.
See the User guide section on the “[Feature Lifecycle](userguide/feature_lifecycle.html)” for more information.

The following are the features that have been promoted in this Gradle release.

<!--
### Example promoted
-->

## Fixed issues

## Deprecations

Features that have become superseded or irrelevant due to the natural evolution of Gradle become *deprecated*, and scheduled to be removed
in the next major Gradle version (Gradle 4.0). See the User guide section on the “[Feature Lifecycle](userguide/feature_lifecycle.html)” for more information.

The following are the newly deprecated items in this Gradle release. If you have concerns about a deprecation, please raise it via the [Gradle Forums](https://discuss.gradle.org).

<!--
### Example deprecation
-->

## Potential breaking changes

<!--
### Example breaking change
-->

### Changes to previously deprecated APIs

- The `JacocoPluginExtension` methods `getLogger()`, `setLogger(Logger)` are removed.
- The `JacocoTaskExtension` methods `getClassDumpFile()`, `setClassDumpFile(File)`, `getAgent()` and `setAgent(JacocoAgentJar)` are removed.
- Removed constructor `AccessRule(Object, Object)`.
- Removed constructor `ProjectDependency(String, String)` and the methods `getGradlePath()`, `setGradlePath(String)`.
- Removed constructor `WbDependentModule(Object)`.
- Removed constructor `WbProperty(Object)`.
- Removed constructor `WbResource(Object)`.
- Removed constructor `JarDirectory(Object, Object)`.
- Removed constructor `Jdk(Object, Object, Object, Object)`.
- Removed constructor `ModuleDependency(Object, Object)`.
- Moved classes `RhinoWorkerHandleFactory` and `RhinoWorkerUtils` into internal package.
- Removed `RhinoWorker`.
- Removed constructor `EarPluginConvention(Instantiator)`.

The deprecated `jetty` plugin has been removed. We recommend using the [Gretty plugin](https://github.com/akhikhl/gretty) for developing Java web applications.

## External contributions

We would like to thank the following community members for making contributions to this release of Gradle.

- [Ion Alberdi](https://github.com/yetanotherion) - Fix lazy evaluation of parent/grand-parent pom's properties ([gradle/gradle#1192](https://github.com/gradle/gradle/pull/1192))
- [Guillaume Delente](https://github.com/GuillaumeDelente) - Fix typo in user guide ([gradle/gradle#1562](https://github.com/gradle/gradle/pull/1562))
- [Guillaume Le Floch](https://github.com/glefloch) - Support of compileOnly scope in buildInit plugin ([gradle/gradle#1536](https://github.com/gradle/gradle/pull/1536))
- [Eitan Adler](https://github.com/grimreaper) - Remove some some duplicated words from documentation ([gradle/gradle#1513](https://github.com/gradle/gradle/pull/1513))
- [Eitan Adler](https://github.com/grimreaper) - Remove extraneous letter in documentation ([gradle/gradle#1459](https://github.com/gradle/gradle/pull/1459))
- [Pierre Noel](https://github.com/petersg83) - Add missing comma in `FileReferenceFactory.toString()` ([gradle/gradle#1440](https://github.com/gradle/gradle/pull/1440))
- [Hugo Bijmans](https://github.com/HugooB) - Fixed some typos and spelling in the JavaPlugin user guide ([gradle/gradle#1514](https://github.com/gradle/gradle/pull/1514))
- [Andy Wilkinson](https://github.com/wilkinsona) - Copy resolution listeners when a configuration is copied ([gradle/gradle#1603](https://github.com/gradle/gradle/pull/1603))
- [Tim Hunt](https://github.com/mitnuh) - Allow the use of single quote characters in Javadoc task options header and footer ([gradle/gradle#1288](https://github.com/gradle/gradle/pull/1288))
- [Jenn Strater](https://github.com/jlstrater) - Add groovy-application project init type ([gradle/gradle#1480](https://github.com/gradle/gradle/pull/1480))
- [Jacob Ilsoe](https://github.com/jacobilsoe) - Update Zinc to 0.3.13 ([gradle/gradle#1463](https://github.com/gradle/gradle/issues/1463))
- [Shintaro Katafuchi](https://github.com/hotchemi) - Issue: #952 Make project.sync() a public API ([gradle/gradle#1137](https://github.com/gradle/gradle/pull/1137))
- [Lari Hotari](https://github.com/lhtorai) - Issue: #1730 Memory leak in Gradle daemon
 ([gradle/gradle#1736](https://github.com/gradle/gradle/pull/1736))


We love getting contributions from the Gradle community. For information on contributing, please see [gradle.org/contribute](https://gradle.org/contribute).

## Known issues

Known issues are problems that were discovered post release that are directly related to changes made in this release.
