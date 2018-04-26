# Cloud Id Demo

Spring Boot application which uses the Cloud Id library and serves a simple HTML page.

Information about how servers and clients are being verified can be found in the 
[Readme](../cloudid-lib/README.md) for the Cloud Id lib. 


## Local start without a SPIRE server

You need to set the following VM options:
- `-Dcloudid.idManagerClass=de.qaware.cloudid.lib.spire.DebugIdManager` which allows you to use a
normal key store file.
- `-Dcloudid.debug.keystore.location` with a path to a key store like the 
[provided test key store](src/test/resources/spire_test_keystore_ec.jks). More settings for the key store can be
found in the [Cloud Id Config](../cloudid-lib/src/main/java/de/qaware/cloudid/lib/Config.java).

More options like the server port can be found in [application.properties](src/main/resources/application.properties).
Those options can be set as VM options, using `-D` switches.

Set `de.qaware.cloudid.demo.DemoServerApplication` as main class in your run configuration.

You may want to set `-Dserver.ssl.enabled=false` if you just want to access the page directly using a browser.

## Deployment in Kubernetes

A sample deployment can be found [here](../k8s).

## Troubleshooting

Logging can be enabled in [logback.xml](src/main/resources/logback.xml).

Remote debugging in Kubernetes is possible by adding 
`-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005` to `JAVA_TOOL_OPTIONS` in the respective deployment
 file. Don't forget to use `kubectl port-forward <pod> 5005:5005` to make the pod accessible. Please consider the pods 
 liveliness probe as it might restart your pod if suspended for too long.