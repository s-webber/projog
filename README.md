# [projog](http://www.projog.org/)
[![Build Status](https://travis-ci.org/s-webber/projog.png?branch=master)](https://travis-ci.org/s-webber/projog)
[![License](https://img.shields.io/badge/license-Apache%20v2.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)

## About

Projog provides an implementation of the Prolog programming language for the Java platform. Prolog is a declarative logic programming language where programs are represented as facts and rules.

## Resources

- [Frequently Asked Questions](http://projog.org/faq.html)
- [Getting Started](http://projog.org/getting-started.html)
- [Calling Prolog from Java](http://projog.org/calling-prolog-from-java.html)
- [Extending Prolog using Java](http://projog.org/extending-prolog-with-java.html)

## Quick Start Guide

The following commands will download Projog and start the console:

```sh
$ wget http://projog.org/downloads/projog-bin.zip
$ jar xvf projog-bin.zip
$ cd projog-0.1.0
$ chmod u+x projog-console.sh
$ ./projog-console.sh
```

## Directory structure

* `build.xml` - Ant build script.
* `etc` - Extra resources that get included in a release.
* `src/build-utils` - Java source code used by the build process for system-testing and website content generation.
* `src/build-utils-test` - JUnit tests for `src/build-utils`.
* `src/core` - Core Projog Java source code. Includes inference engine, built-in predicates and console application.
* `src/core-tests` - JUnit tests for `src/core`.
* `src/prolog` - Prolog scripts used for system-testing and documentation.
* `web` - Used to construct content of http://www.projog.org/
