@GrabResolver(name='nikita', root='https://dl.bintray.com/nikit007/mvn-repo/')
@GrabResolver(name="local", root="~/.m2/repository/", m2Compatible=true)
// grapes are stored in ~/.groovy/grapes/
@Grapes(
        @Grab(group='com.github.nikit.cpp.helpers', module='deployer', version='1.0.6')
)

helpers.Server server = new helpers.Server()
helpers.JbossDeployer deployer = new helpers.JbossDeployer(server)
deployer.refreshArfifactsFromServer()

println "Getted"
for(int i=0; i<deployer.artifactsOnServer.size; ++i){
    println deployer.artifactsOnServer.get(i)
}
