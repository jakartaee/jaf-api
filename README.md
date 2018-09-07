# JavaBeans Activation Framework (JAF)

The JavaBeans Activation Framework (JAF) is a standard extension to the
Java platform that lets you take advantage of standard services to:
determine the type of an arbitrary piece of data; encapsulate access to
it; discover the operations available on it; and instantiate the
appropriate bean to perform the operation(s).

JAF is used by [JavaMail](https://github.com/eclipse-ee4j/javamail)
and [JAX-WS](https://github.com/eclipse-ee4j/jax-ws-api) for data content handling.

JAF is included in Java SE 6 and later.

This standalone release of JAF uses a
[Java Platform Module System](http://openjdk.java.net/projects/jigsaw/spec/)
"automatic" module name of `java.activation`, to match the module name
used in JDK 9.  A future version will include full module metadata.
