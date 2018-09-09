# [projog](http://www.projog.org/)
[![Maven Central](https://img.shields.io/maven-central/v/org.projog/projog-core.svg)](https://search.maven.org/search?q=g:org.projog)
[![Build Status](https://travis-ci.org/s-webber/projog.png?branch=master)](https://travis-ci.org/s-webber/projog)
[![License](https://img.shields.io/badge/license-Apache%20v2.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)

## About

Projog provides an implementation of the [Prolog](https://en.wikipedia.org/wiki/Prolog) programming language for the Java platform. Prolog is a declarative logic programming language where programs are represented as facts and rules.

## Resources

- [Frequently Asked Questions](http://projog.org/faq.html)
- [Getting Started](http://projog.org/getting-started.html)
- [Calling Prolog from Java](http://projog.org/calling-prolog-from-java.html)
- [Extending Prolog using Java](http://projog.org/extending-prolog-with-java.html)

## Quick Start Guide

The following commands will download Projog and start the console:

```sh
$ wget http://www.projog.org/downloads/projog-0.2.1.zip
$ jar xvf projog-0.2.1.zip
$ cd projog-0.2.1
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
   <version>0.2.1</version>
</dependency>
```

## Reporting Issues

We would be grateful for feedback. If you would like to report a bug, suggest an enhancement or ask a question then please [create a new issue](https://github.com/s-webber/projog/issues/new).
