# jenkins-shared-library

![build](https://img.shields.io/endpoint?url=https://aboe026.github.io/shields.io-badge-results/badge-results/jenkins-shared-library/main/build.json)
![coverage](https://img.shields.io/endpoint?url=https://aboe026.github.io/shields.io-badge-results/badge-results/jenkins-shared-library/main/coverage.json)

Jenkins CI shared library files.

**Note**: When running `./gradlew` commands using Git Bash on Windows, may need to set environment variable `TERM=cygwin` to properly format control characters.

## Requirements

- [Gradle](https://gradle.org/)

## Lint

Linting code for programmatic and stylistic error detection is handled through [npm-groovy-lint](https://github.com/nvuillam/npm-groovy-lint).

If you have [NodeJS](https://nodejs.org/) installed, you can run

```sh
npx npm-groovy-lint --ignorepattern "**/bin/**,**/build/**,**/.gradle/**" --failon info
```

Otherwise if you have [docker](https://www.docker.com/) you can run

```sh
docker run --rm -w=/tmp/lint -v "$PWD":/tmp/lint nvuillam/npm-groovy-lint npm-groovy-lint --ignorepattern "**/bin/**,**/build/**,**/.gradle/**" --failon info
```

For Windows users with Git Bash, run

```sh
MSYS_NO_PATHCONV=1 docker run --rm -w=/tmp/lint -v /$(PWD):/tmp/lint nvuillam/npm-groovy-lint npm-groovy-lint --ignorepattern "**/bin/**,**/build/**,**/.gradle/**" --failon info
```

## Build

To build code, run

```sh
./gradlew build -x test
```

## Test

To execute unit tests, run

```sh
./gradlew test
```

**Note**: To run single test, add `--tests "*If params null, default returned"`

**Note**: To run single spec, add `--tests "*ShieldsIoBadges__colorSpec*"`

To execute tests in a docker container, run

```sh
docker run --rm -u gradle -v "$PWD":/home/gradle/project -w /home/gradle/project gradle ./gradlew test
```

For Windows users with Git Bash

```sh
MSYS_NO_PATHCONV=1 docker run --rm -u gradle -v /$(PWD):/home/gradle/project -w /home/gradle/project gradle ./gradlew test
```

## Coverage

To generage code coverage, run

```sh
./gradlew jacocoTestReport
```

To view the HTML coverage report in your default browser, run

```sh
./gradlew viewCoverage
```

## Print

In order to print output within calling Jenkinsfiles, you need to go through the `steps` that get passed into classes/methods like so

```groovy
this.steps.println 'Hello World!'
```

Calling `println` on its own will not produce any output.
