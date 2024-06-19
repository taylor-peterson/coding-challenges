# [coding-challenges](https://codingchallenges.fyi/challenges/challenge-wc/)

Learn by doing!

# Usage
Generic instructions for CLI-based tools; differences will be indicated in challenge READMEs.

```bash
cd <challenge dir>
sbt test # run unit tests
sbt nativeImage # produce executable (prints output location)
sbt IntegrationTest/test # run integration tests (will produce nativeImage first)
sbt "nativeImageRun <args>" # run native executable
```

Local validation of CI: run `act` from the repository root.
