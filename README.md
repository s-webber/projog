# [projog](http://projog.org "Prolog interpreter for Java")
[![Build Status](https://github.com/s-webber/projog/actions/workflows/github-actions.yml/badge.svg)](https://github.com/s-webber/projog/actions/)
[![Maven Central](https://img.shields.io/maven-central/v/org.projog/projog-core.svg)](https://search.maven.org/search?q=g:org.projog)
[![License](https://img.shields.io/badge/license-Apache%20v2.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)

## About

Projog provides an implementation of the [Prolog](https://en.wikipedia.org/wiki/Prolog) programming language for the Java platform. Prolog is a declarative logic programming language where programs are represented as facts and rules.

Projog can be used as a stand-alone console application or embedded in your Java applications as a Maven dependency.

## Resources

- [Frequently Asked Questions](http://projog.org/faq.html)
- [Getting Started](http://projog.org/getting-started.html)
- [Calling Prolog from Java](http://projog.org/calling-prolog-from-java.html)
- [Extending Prolog using Java](http://projog.org/extending-prolog-with-java.html)
- Example applications: [Prolog Expert System](https://github.com/s-webber/prolog-expert-system) and [Prolog Wumpus World](https://github.com/s-webber/prolog-wumpus-world)
- [Class diagrams](http://projog.org/class-diagrams.html) and [design decisions](http://projog.org/design-decisions.html)

## Quick Start Guide

The following commands will download Projog and start the console:

```sh
$ wget http://projog.org/downloads/projog-0.10.0.zip
$ jar xvf projog-0.10.0.zip
$ cd projog-0.10.0
$ chmod u+x projog-console.sh
$ ./projog-console.sh
```

When the console has started you can enter the following command:

```
W=X, X=1+1, Y is W, Z is -W.
```

Which should generate the following response:

```
W = 1 + 1
X = 1 + 1
Y = 2
Z = -2

yes
```

To exit the console type `quit.`

## Maven Artifacts

To include Projog within your project, just add this dependency to your `pom.xml` file:

```
<dependency>
   <groupId>org.projog</groupId>
   <artifactId>projog-core</artifactId>
   <version>0.10.0</version>
</dependency>
```

## Reporting Issues

We would be grateful for feedback. If you would like to report a bug, suggest an enhancement or ask a question then please [create a new issue](https://github.com/s-webber/projog/issues/new).
