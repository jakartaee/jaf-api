# Jakarta Activation

[![Build Status](https://github.com/jakartaee/jaf-api/actions/workflows/maven.yml/badge.svg?branch=master)](https://github.com/jakartaee/jaf-api/actions/workflows/maven.yml?branch=master)
[![Jakarta Staging (Snapshots)](https://img.shields.io/nexus/s/https/jakarta.oss.sonatype.org/jakarta.activation/jakarta.activation-api.svg)](https://jakarta.oss.sonatype.org/content/repositories/staging/jakarta/activation/jakarta.activation-api/)

Jakarta Activation lets you take advantage of standard services to:
determine the type of arbitrary piece of data; encapsulate access to
it; discover the operations available on it; and instantiate the
appropriate bean to perform the operation(s).

**IMPORTANT:** Implementation of the Jakarta Activation API, aka JakartaActivation
(formerly JavaActivation), is no longer part of this repository.
As part of breaking tight integration between Jakarta Activation API and the implementation,
implementation sources were moved to the new project - [Eclipse Angus](https://eclipse-ee4j.github.io/angus-activation/) -
and further development continues there. [Eclipse Angus](https://eclipse-ee4j.github.io/angus-activation/)
is direct accessor of JavaActivation/JakartaActivation.

See the
[Jakarta Activation web site](https://jakartaee.github.io/jaf-api/).

## License

* Most of the Jakarta Activation project source code is licensed
under the [Eclipse Distribution License (EDL) v1.0.](https://www.eclipse.org/org/documents/edl-v10.php);
see the license information at the top of each source file.
* The source code for the Jakarta Activation Specification project
is licensed under the [Eclipse Public License (EPL) v2.0](https://www.eclipse.org/legal/epl-2.0/)
and [GNU General Public License (GPL) v2 with Classpath Exception](https://www.gnu.org/software/classpath/license.html);
again, the license is in each source file.
* The binary jar files published to the Maven repository are licensed
under the same licenses as the corresponding source code;
see the file `META-INF/LICENSE.*` in each jar file.

You'll find the text of the licenses in the workspace in various `LICENSE.txt` or `LICENSE.md` files.
Don't let the presence of these license files in the workspace confuse you into thinking
that they apply to all files in the workspace.

You should always read the license file included with every download, and read
the license text included in every source file.

## Contributing

We use [contribution policy](CONTRIBUTING.md), which means we can only accept contributions under
the terms of [Eclipse Contributor Agreement](http://www.eclipse.org/legal/ECA.php).

## Links
* [Jakarta Activation web site](https://jakartaee.github.io/jaf-api/)
* [Jakarta Activation TCK project](https://github.com/jakartaee/jaf-tck)
* [Jakarta Activation API nightly build job](https://ci.eclipse.org/jaf/job/activation-build-snapshot/)
* [Mailing list](https://accounts.eclipse.org/mailing-list/jaf-dev)
* [Eclipse Angus Activation project](https://eclipse-ee4j.github.io/angus-activation/)
