# Cloud Id

Contains the Java parts of the showcase as a Gradle project.

## Cloud Id Library (cloudid-lib)

Contains the Cloud Id library including the custom Java Security API (JSA) and ACL retrieval using Vault.

## Cloud Id Demo

Spring Boot application serving a simple HTML page with some info about the workload Id and call trace.

## Cloud Id Proxy

Spring Boot application that forwards request and response. Can be configured to serve HTTP or HTTPs.

## Build

Run `./gradlew build`
