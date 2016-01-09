[![Build Status](https://travis-ci.org/nikit-cpp/helpers.svg)](https://travis-ci.org/nikit-cpp/helpers)

As possible variant of usage, you can to write for example next stresstest:

```
vim forever_redeploy.groovy
```

Put
```groovy
@GrabResolver(name='nikita', root='https://dl.bintray.com/nikit007/mvn-repo/')
@Grapes(
    @Grab(group='com.github.nikit.cpp.helpers', module='deployer', version='1.0.6')
)

helpers.Server server = new helpers.Server()
helpers.JbossDeployer deployer = new helpers.JbossDeployer(server)
deployer.readFile 'deploy.list'

for(int i=0; ; ++i){
    println "round ${i}"
    deployer.undeployList()
    deployer.deployList()
}
```

Now create deploy list file:
```
vim deploy.list
```

Add:
```
#Comments and

# empty lines are allowed
server/build/libs/server.jar
client/build/libs/client.war
ws/build/libs/webserwice.war
```
So, firstly will be deployed server.jar, further client.war, ...

Or you can explicit specify deployments:
```groovy
deployer.listToDeploy = [new File('server/build/libs/server.jar'), new File('client/build/libs/client.war'), new File('ws/build/libs/webserwice.war')]
```

Execute:
```
groovy forever_redeploy.groovy
```
