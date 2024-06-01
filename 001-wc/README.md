# Overview

Clone of the Unix command line tool `wc` per [Build Your Own wc Tool](https://codingchallenges.fyi/challenges/challenge-wc/).

# Usage

```bash
sbt test # run unit tests
sbt nativeImage # produce executable (prints output location)
sbt IntegrationTest/test # run integration tests (will produce nativeImage first)
sbt "nativeImageRun <args>" # run native executable
```
