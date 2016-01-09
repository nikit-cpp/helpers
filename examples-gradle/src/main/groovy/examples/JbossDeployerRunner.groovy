package examples

/**
 * Created by nik on 09.01.16.
 */
class JbossDeployerRunner {
    public static void main(String... args){
        helpers.Server server = new helpers.Server()
        helpers.JbossDeployer deployer = new helpers.JbossDeployer(server)
        deployer.refreshArfifactsFromServer()

        /*println "Getted"
        for(int i=0; i<deployer.artifactsOnServer.size; ++i){
            println deployer.artifactsOnServer.get(i)
        }*/

        Closure findPreviousVersionArtfactClosure = {
            helpers.Artifact a ->
                return a.displayName.contains("infin")
        }

        helpers.Artifact aa = deployer.findOne(findPreviousVersionArtfactClosure)
        println(aa)
    }
}
