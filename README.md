[![Build Status][travis-badge]](https://travis-ci.com/spine-examples/hello) &nbsp;
[![license][license-badge]](http://www.apache.org/licenses/LICENSE-2.0)

[travis-badge]: https://travis-ci.com/spine-examples/hello.svg?branch=master
[license-badge]: https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat

## Hello World!

This is a simple Spine-based application which defines Hello Bounded Context, which has:

* The `Print` command.
  
* The `Console` Process Manager, which handles the command.
  
* The event `Printed` event emitted by the `Console`. 
 
To run the sample from the command line, please execute:

```bash
./gradlew :sayHello
```
