# jenkins-shared-library

Jenkins CI shared library files.

## Lint

Linting code for programmatic and stylistic error detection is handled through [npm-groovy-lint](https://github.com/nvuillam/npm-groovy-lint).

If you have [NodeJS](https://nodejs.org/) installed, you can run

```sh
npx npm-groovy-lint
```

Otherwise if you have [docker](https://www.docker.com/) you can run

```sh
docker run --rm -v "$PWD":/tmp nvuillam/npm-groovy-lint
```

## Print

In order to print output within calling Jenkinsfiles, you need to go through the `steps` that get passed into classes/methods like so

```groovy
this.steps.println 'Hello World!'
```

Calling `println` on its own will not produce any output.
