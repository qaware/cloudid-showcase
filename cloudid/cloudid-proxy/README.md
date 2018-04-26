# Cloud Id Proxy

Very basic HTTP proxy built with Spring Boot.
Before forwarding a request, a custom header is being added/modified containing the SPIFFE Id call stack. This is 
being used for the demo page which is generated in Cloud Id Demo.

The address to forward to can be configured in [application.properties](src/main/resources/application.properties) by
setting `app.backend`. This property can be changed using a configmap.

Information about how servers and clients are being verified can be found in the 
[Readme](../cloudid-lib/README.md) for the Cloud Id lib. 

## Local startup

Refer to [Cloudid Demo Readme](../cloudid-demo/README.md)

`-Dserver.ssl.enabled=false` can be set for a running instance that you want to directly access from the Browser. It
will still be able to access instances with SSL enabled but won't be able to handle incoming https requests.

If Vault is not running/installed setting `-Dcloudid.disableAcl=true` disables ACL verification entirely.

## Troubleshooting

Logging can be enabled in [logback.xml](src/main/resources/logback.xml).

Remote Debugging in Kubernetes is possible by adding 
`-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005` to `JAVA_TOOL_OPTIONS` in the respective deployment
 file. Don't forget to use `kubectl port-forward <pod> 5005:5005` to make the pod accessible. Please consider the pod's 
 liveliness probe as it might restart your pod if suspended for too long.