# AeroGear Services Android SDK

[![circle-ci](https://img.shields.io/circleci/project/github/aerogear/aerogear-android-sdk/master.svg)](https://circleci.com/gh/aerogear/aerogear-android-sdk)
[![License](https://img.shields.io/badge/-Apache%202.0-blue.svg)](https://opensource.org/s/Apache-2.0)

|                 | Project Info                                                     |
| --------------- | ---------------------------------------------------------------- |
| License:        | Apache License, Version 2.0                                      |
| Build:          | Gradle                                                           |
| Documentation:  | https://aerogear.org/android/                                    |
| Issue tracker:  | https://issues.jboss.org/browse/AGDROID                          |
| Mailing lists:  | [aerogear-users](http://aerogear-users.1116366.n5.nabble.com/)   | 
|                 | [aerogear-dev](https://groups.google.com/forum/#!forum/aerogear) |

## Documentation

1. [End User Getting Started Guide](./docs/modules/getting-started/pages/getting-started.adoc)
1. [Self Defence Checks](./docs/modules/getting-started/pages/auth-self-defence-checks.adoc)
1. [Certificate Pinning](./docs/modules/getting-started/pages/certificate-pinning.adoc)
1. [Troubleshooting](./docs/troubleshooting.adoc)
1. [Service Contributor Guide](./docs/contributing-guide.adoc)

### List of SDKs

AeroGear Services SDK consist of set of separate SDKs

- [Core](./docs/modules/getting-started/pages/core.adoc): Common base for all SDKs
- [Auth](./docs/modules/getting-started/pages/auth.adoc):  Mobile authentication SDK

## License 

 See [LICENSE file](./LICENSE)

## Development

If you would like to help develop AeroGear you can join our [developer's mailing list](https://groups.google.com/forum/#!forum/aerogear), join #aerogear on Freenode, or shout at us on Twitter @aerogears.

Also takes some time and skim the [contributor guide](CONTRIBUTING.md)

## Testing

We're using [Gradle](https://gradle.org/) for running the tests from command line.

### Unit tests

For running the unit tests, simly run

`./gradlew testDebug --tests *.UnitTestSuite`

### Integration tests

Integration tests are designed to be triggered when PR is created, but with easy configuration it's also possible to run them on your local machine (described below).

To trigger the integration tests together with creation of PR, select the `test/integration` label in the right column.

**Metrics integration test**

This includes testing of communication between Android SDK Metrics module and [AeroGear App Metrics service](https://github.com/aerogear/aerogear-app-metrics) (part of [Metrics-APB](https://github.com/aerogearcatalog/metrics-apb))

To run it locally:

1. Edit [Metrics URL](https://github.com/aerogear/aerogear-android-sdk/blob/master/core/src/test/assets/integration-test-mobile-services.json#L11) with valid URL pointing to `/metrics` endpoint, e.g. https://app-metrics.example.com/metrics
2. Run the test: `./gradlew :core:testDebug --tests *.IntegrationTestSuite`


## Contributing

See [General Contributing Guide](./CONTRIBUTING.md)

## Questions?

Join our [user mailing list](https://groups.google.com/forum/#!forum/aerogear) for any questions or help! We really hope you enjoy app development with AeroGear!

## Found a bug?

If you found a bug please create a ticket for us on [Jira](https://issues.jboss.org/browse/AGDROID) with some steps to reproduce it.
