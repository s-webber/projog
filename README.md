# [projog](http://www.projog.org/)

Projog provides an implementation of the Prolog programming language for the Java platform. Prolog is a declarative logic programming language where programs are represented as facts and rules.

- [Frequently Asked Questions](http://projog.org/faq.html)
- [Getting Started](http://projog.org/getting-started.html)
- [Calling Prolog from Java](http://projog.org/calling-prolog-from-java.html)
- [Extending Prolog using Java](http://projog.org/extending-prolog-with-java.html)

## Directory structure

* `build.xml` - Ant build script.
* `etc` - Extra resources that get included in a release.
* `src/build-utils` - Java source code used by the build process for system-testing and website content generation.
* `src/build-utils-test` - JUnit tests for `src/build-utils`.
* `src/core` - Core Projog Java source code. Includes inference engine, built-in predicates and console application.
* `src/core-tests` - JUnit tests for `src/core`.
* `src/prolog` - Prolog scripts used for system-testing and documentation.
* `web` - Used to construct content of http://www.projog.org/

[![Build Status](https://travis-ci.org/s-webber/projog.png?branch=master)](https://travis-ci.org/s-webber/projog)

--------------------------------------

Copyright 2013 S Webber
  
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.