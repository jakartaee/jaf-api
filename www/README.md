Jakarta Activation lets you take advantage of standard services to:
determine the type of an arbitrary piece of data; encapsulate access to
it; discover the operations available on it; and instantiate the
appropriate bean to perform the operation(s).

Jakarta Activation is used for data content handling by:
* [Jakarta Mail](https://jakarta.ee/specifications/mail/)
* [Jakarta XML Web Services](https://jakarta.ee/specifications/xml-web-services/)

<br/>

# Table of Contents
* [Latest News](#Latest_News)
* [API Documentation](#API_Documentation)
* [Help](#Help)
* [Bugs](#Bugs)
* [Development Releases](#Development_Releases)

<br/>

# <a name="Latest_News"></a>Latest News

## April 28, 2023 - Jakarta Activation 2.1.2 Final Release ##

The 2.1.2 release is the bug fix release which adds missing
javadoc description to `jakarta.activation.MimeTypeEntry` `get` methods
and fixes class file version in `package-info`.

## January 11, 2023 - Jakarta Activation 2.1.1 Final Release ##

The 2.1.1 release is the bug fix release which removes the need
for accessDeclaredMembers permissions when loading DataHandlers
and improves usability in OSGi environment.

## November 20, 2021 - Jakarta Activation 2.1.0 Final Release ##

The 2.1.0 release is the first release providing standalone API jar file.
The implementation, formerly JakartaActivation, is now standalone project -
[Eclipse Angus](https://eclipse-ee4j.github.io/angus-activation/).
This version of the specification is included
in the [Jakarta EE 10 Platform](https://jakarta.ee/specifications/platform/10/).

## April 6, 2021 - Jakarta Activation 2.0.1 Final Release ##

The 2.0.1 release is the bug fix release of 2.0.0.

## October 22, 2020 - Jakarta Activation 2.0.0 Final Release ##

The 2.0.0 release is the first release under the `jakarta` namespace.
It is also the first release containing full module metadata.

## July 25, 2019 - Jakarta Activation is the new name for JAF ##

The JavaBeans Activation Framework has been renamed to Jakarta Activation
in preparation for inclusion in a future version of Jakarta EE.

## November 26, 2018 - JavaBeans Activation Framework 1.2.1 Final Release ##

The 1.2.1 release is the first release of the Eclipse project for JAF
and includes no bug fixes or enhancements. It does include changes
to the Maven coordinates. The main jar file is now located at
[com.sun.activation:jakarta.activation](https://repo1.maven.org/maven2/com/sun/activation/jakarta.activation/1.2.1/jakarta.activation-1.2.1.jar).

This standalone release of JAF uses a
[Java Platform Module System](http://openjdk.java.net/projects/jigsaw/spec/)
"automatic" module name of `jakarta.activation`, which is different than the
module name used in JDK 9 and 10.
A future version will include full module metadata.

## September 14, 2018 - JavaBeans Activation Framework project moves to the Eclipse Foundation! ##

The JavaBeans Activation Framework project is now hosted at the Eclipse
Foundation as part of the
[EE4J project](https://projects.eclipse.org/projects/ee4j).

<br/>

# <a name="API_Documentation"></a>API Documentation

The JavaBeans Activation Framework 1.2 and earlier API is defined
through the Java Community Process as
[JSR 925](http://jcp.org/en/jsr/detail?id=925).

The Jakarta Activation API documentation is available
[here](https://jakartaee.github.io/jaf-api/api/).

The following documents summarize the API changes in each release of
the JavaBeans Activation Framework API specification:

-   [JAF 2.1](JAF-2.1-changes.txt)
-   [JAF 2.0](JAF-2.0-changes.txt)
-   [JAF 1.2](JAF-1.2-changes.txt)
-   [JAF 1.1](JAF-1.1-changes.txt)

<br/>

# <a name="Help"></a>Help

You can post questions to the
[jaf-dev mailing list](https://accounts.eclipse.org/mailing-list/jaf-dev).

<br/>

# <a name="Bugs"></a>Bugs

Jakarta Activation bugs are tracked in the
[GitHub Jakarta Activation project issue tracker](https://github.com/jakartaee/jaf-api/issues).

<br/>

# <a name="Development_Releases"></a>Development Releases

From time to time snapshot releases of the next version of JAF
under development are published to the
[Jakarta Sonatype OSS repository](http://jakarta.oss.sonatype.org).
These snapshot releases have received only minimal testing, but may
provide previews of bug fixes or new features under development.

For example, you can download the jakarta.activation.jar file from the Jakarta Activation
1.2.2-SNAPSHOT release
[here](https://jakarta.oss.sonatype.org/content/repositories/snapshots/com/sun/activation/jakarta.activation/1.2.2-SNAPSHOT/).
Be sure to scroll to the bottom and choose the jar file with the most
recent time stamp.

You'll need to add the following configuration to your Maven ~/.m2/settings.xml
to be able to use these with Maven:

```
    <profiles>
        <!-- to allow loading Jakarta snapshot artifacts -->
        <profile>
            <id>jakarta-snapshots</id>
            <pluginRepositories>
                <pluginRepository>
                    <id>jakarta-snapshots</id>
                    <name>Jakarta Snapshots</name>
                    <snapshots>
                        <enabled>true</enabled>
                        <updatePolicy>always</updatePolicy>
                        <checksumPolicy>fail</checksumPolicy>
                    </snapshots>
                    <url>https://jakarta.oss.sonatype.org/content/repositories/snapshots/</url>
                    <layout>default</layout>
                </pluginRepository>
            </pluginRepositories>
        </profile>
    </profiles>
```

And then when you build use `mvn -Pjakarta-snapshots ...`.

If you want the plugin repository to be enabled all the time so you don't need the -P, add:

```
    <activeProfiles>
        <activeProfile>jakarta-snapshots</activeProfile>
    </activeProfiles>
```

<br/>

By contributing to this project, you agree to these additional terms of
use, described in [CONTRIBUTING](CONTRIBUTING.md).

