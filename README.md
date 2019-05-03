[![Build Status](https://travis-ci.com/spine-examples/hello.svg?branch=master)](https://travis-ci.com/spine-examples/hello) &nbsp;
[![license](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0)

## Hello World!

This is a simple Spine-based application which defines Hello Bounded Context, which has:
 * The command `GreetWorld` defined in the `src/proto/hellow_world/commands.proto` file.
 
 * The `GreetingAggregate`, which handles the command producing the event `WorldGreeted`. See `events.proto` for the event definition. 
 
 * The [event subscriber](https://spine.io/core-java/javadoc/server/io/spine/server/event/AbstractEventSubscriber.html) 
   which listens to the event and prints to the console the greeting message.

To run the sample from the console, please execute:

```bash
./gradlew :sayHello
```
