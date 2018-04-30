# Cloud Id Showcase Java Kubernetes deployments


When accessing the demo page from the browser the request passes the following stages:

1. Namespace outer: Ingress -> Service -> CloudId-Proxy instance "http-proxy"
2. Namespace middle: Service -> CloudId-Proxy instance "proxy"
3. Namespace inner:  Service -> CloudId-Demo instance "server" (generates the demo html page)

These files can't be directly applied to Kubernetes because some of them contain placeholders which get automatically 
replaced by the [Makefile](../Makefile).
