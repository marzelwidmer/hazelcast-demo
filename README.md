# Deploy 

## Login in Cluster and Docker Registry
```
oc login
docker login -u `oc whoami`  -p `oc whoami -t` registry.apps.c3smonkey.ch
```

## Skaffold
```
skaffolt run
```

# Check Deployment
`kgp -w | grep hazelcast`

```
kubectl get pods -w | grep hazelcast
hazelcast-demoservice-656844bb79-647p4         1/1     Running   0          1m
```
# Call API
`http --verify=no  https://hazelcast-demo-dev.app-dev.apps.c3smonkey.ch/api/foo`

```
http --verify=no  https://hazelcast-demo-dev.app-dev.apps.c3smonkey.ch/api/foo
HTTP/1.1 200
Cache-control: private
Content-Type: application/json
Date: Sun, 04 Oct 2020 09:16:41 GMT
Set-Cookie: 69a893b11fd0df893d26c8f24219c8d7=fbef179261427cdc8b204e32149151fd; path=/; HttpOnly; Secure
Transfer-Encoding: chunked

"8993c2b2-dac9-4e76-a693-e987d2028426"
```

# Scale Deployment 
`k scale --replicas=2 deployment hazelcast-demoservice`
```
kubectl scale --replicas=2 deployment hazelcast-demoservice
```

# Stern check logs
Check Logs for ` Members {size:2, ver:2}` with `stern`
```
stern hazelcast


hazelcast-demoservice-656844bb79-647p4 hazelcast-demo
hazelcast-demoservice-656844bb79-647p4 hazelcast-demo Members {size:2, ver:2} [
hazelcast-demoservice-656844bb79-647p4 hazelcast-demo 	Member [10.128.0.164]:5701 - eeafcde7-797f-476d-a5be-b1bcf1d96ee6 this
hazelcast-demoservice-656844bb79-647p4 hazelcast-demo 	Member [10.128.0.165]:5701 - 657c4343-2de3-49f2-888e-69f47b991f8b
hazelcast-demoservice-656844bb79-647p4 hazelcast-demo ]
hazelcast-demoservice-656844bb79-647p4 hazelcast-demo
```

# Management Center
```
kustomize build k8s/installs/dev/management-center | kaf -
```

## Configure Cluster
Go to `http://management-center-dev.apps.c3smonkey.ch/hazelcast-mancenter` upload the `k8s/installs/dev/management-center/hazelcast.yaml` cluster configuration.
After you create a Login User.

See also: 
https://docs.hazelcast.org/docs/management-center/4.2020.08/manual/html/index.html#managing-clusters 

Dashboard: 
http://management-center-dev.apps.c3smonkey.ch/dev
