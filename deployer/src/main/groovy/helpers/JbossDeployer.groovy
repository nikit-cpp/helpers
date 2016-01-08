package helpers

import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.SystemUtils

public class JbossDeployer {

    public static final String MAIN_SERVER_GROUP = "main-server-group"

    File jBossBin

    /**
     *
     * @param server Wilfdly сервер
     * @param createWilfdlyNameClosure замыкание, прнимающее файл артифакта "/path/to/jar-0.1.jar" и делающее из него
     * Wildfly displayName и runtimeName
     */
    public JbossDeployer(Server server, String jbossHome = null, Closure createArtifactNamesClosure = null) {
        jbossHome = (jbossHome == null ? getSystemJbossHome() : jbossHome)

        File jBossModules = new File(jbossHome, "jboss-modules.jar")
        File jbossModulesPath = new File(jbossHome, "modules")

        jBossBin = new File(jbossHome, 'bin')

        if (server.local) {
            commonCommand = [
                    'java',
                    "-Dlogging.configuration=file:${new File(jBossBin, 'jboss-cli-logging.properties').absolutePath}",
                    "-jar",
                    jBossModules,
                    "-mp",
                    jbossModulesPath,
                    "org.jboss.as.cli",
                    '-c',
            ].asImmutable()
        } else {
            commonCommand = [
                    'java',
                    "-Dlogging.configuration=file:${new File(jBossBin, 'jboss-cli-logging.properties').absolutePath}",
                    "-jar",
                    jBossModules,
                    "-mp",
                    jbossModulesPath,
                    "org.jboss.as.cli",
                    '-c',
                    "--controller=${server.hostname}:${server.port}",
                    "--user=${server.username}",
                    "--password=${server.password}",
            ].asImmutable()
        }

        this.server = server
        this.createArtifactNamesClosure = createArtifactNamesClosure

        println "Initialized JbossDeployer : ${server.domain ? 'domain' : 'standalone'}, ${server.local ? 'local' : 'remote ' + server.hostname}"
    }

    AbstractExecutor executor = new Executor()

    List<File> listToDeploy = []

    def commonCommand
    Server server
    Closure createArtifactNamesClosure

    def artifactsOnServer

    String getSystemJbossHome() {
        String jbossHome = System.getenv("WILDFLY_HOME")
        if (null == jbossHome) {
            jbossHome = System.getenv("JBOSS_HOME")
        }

        if (null == jbossHome) {
            throw new RuntimeException("jbossHome not found among WILDFLY_HOME and JBOSS_HOME")
        }
        return jbossHome
    }

    public void readFile(String pathToDeployList) {
        listToDeploy.clear()
        List<String> strings = FileUtils.readLines(new File(pathToDeployList))
        for (String s : strings) {
            s = s.trim()
            if (s.empty || s.startsWith('#')) {
                continue
            }
            listToDeploy.add(new File(s))
        }
    }


    String getDomainUndeployCommand(String nameInWildfly) {
        return "undeploy ${nameInWildfly} --all-relevant-server-groups"
    }

    String getDomainDeployCommand(String pathToArtifact, String nameInWildfly, String runtimeName) {
        return "deploy ${pathToArtifact} --disabled --name=${nameInWildfly} --runtime-name=${runtimeName}"
    }

    String getDomainAddToGroupCommand(String nameInWildfly, String serverGroupNames) {
        return "deploy --name=${nameInWildfly} --server-groups=${serverGroupNames}"
    }

    String getStandaloneDeployCommand(String pathToArtifact, String nameInWildfly, String runtimeName) {
        return "deploy ${pathToArtifact} --force --name=${nameInWildfly} --runtime-name=${runtimeName}"
    }

    String getStandaloneUndeployCommand(String nameInWildfly) {
        return "undeploy ${nameInWildfly}"
    }

    String escape(String file) {
        if (SystemUtils.IS_OS_WINDOWS){
            return "\"\"${file}\"\""
        } else {
            return file.replace(" ", "\\ ")
        }
    }

    String getArtifactsCommand(String serverGroup=null){
        if (server.domain && (server.domainServerGroups==null || server.domainServerGroups.empty)){
            serverGroup = MAIN_SERVER_GROUP
        }

        if (server.domain){
            return "deployment-info --server-group=${serverGroup}"
        } else {
            return "deployment-info"
        }
    }

    void refreshArfifactsFromServer(){
        artifactsOnServer = []
        List cmd = commonCommand.collect()
        cmd.add(getArtifactsCommand())
        ExecutorResult executorResult = executor.execute(commandWithArgs: cmd, workingDirectory: jBossBin, printOut: false)

        for (int i=1; i<executorResult.stdout.size(); ++i){
            String outString = executorResult.stdout.get(i)

            def splitted = outString.split('\\s+')
            Artifact artifact = new Artifact(displayName: splitted[0], runtimeName: splitted[1])
            artifactsOnServer << artifact
        }
    }

    Artifact findOne(Closure<Boolean> closure){
        if (artifactsOnServer==null || artifactsOnServer.size==0){
            refreshArfifactsFromServer()
        }

        for(Artifact m: artifactsOnServer){
            if(closure(m)){
                return m
            }
        }
        return null
    }

    void deploy(File artifact) {
        String runtimeName
        String displayName
        List<String> serverGroups = server.domainServerGroups.collect() // копируем список
        if (null != createArtifactNamesClosure) {
            Map out = createArtifactNamesClosure(this, artifact)
            runtimeName = out.runtimeName
            displayName = out.displayName
        } else {
            runtimeName = artifact.name
            displayName = artifact.name
        }

        String canonicalPath = artifact.canonicalPath
        println "Deploying ${displayName} [${canonicalPath}]..."

        canonicalPath = escape(canonicalPath)

        if (server.domain) {
            def deployCommand = commonCommand.collect()
            deployCommand.add("--command=${getDomainDeployCommand(canonicalPath, displayName, runtimeName)}")
            executor.execute(commandWithArgs: deployCommand, workingDirectory: jBossBin)

            def addToGroupCommand = commonCommand.collect()
            if (serverGroups.size() == 0) {
                serverGroups << MAIN_SERVER_GROUP
            }
            String allServerGroups = ""
            int i = 0
            for (String serverGroup : serverGroups) {
                if (i > 0) {
                    allServerGroups += ","
                }
                allServerGroups += serverGroup
                ++i
            }
            addToGroupCommand.add("--command=${getDomainAddToGroupCommand(displayName, allServerGroups)}")
            println "Adding ${displayName} to groups [${allServerGroups}]..."
            executor.execute(commandWithArgs: addToGroupCommand, workingDirectory: jBossBin)
        } else {
            def deployList = commonCommand.collect()
            deployList.add("--command=${getStandaloneDeployCommand(canonicalPath, displayName, runtimeName)}")
            executor.execute(commandWithArgs: deployList, workingDirectory: jBossBin)
        }
    }

    void undeploy(File artifact) {
        String displayName
        if (null != createArtifactNamesClosure) {
            displayName = createArtifactNamesClosure(this, artifact).undeployName
        } else {
            displayName = artifact.name
        }

        println "Undeploying ${displayName} ..."
        def undeployCommand = commonCommand.collect()
        if (server.domain) {
            undeployCommand.add("--command=${getDomainUndeployCommand(displayName)}")
        } else {
            undeployCommand.add("--command=${getStandaloneUndeployCommand(displayName)}")
        }
        executor.execute(commandWithArgs: undeployCommand, workingDirectory: jBossBin)
    }

    void deployList() {
        for (File f : listToDeploy) {
            deploy(f)
        }
    }

    void undeployList(boolean ignoreErrors = true) {
        for (int i = listToDeploy.size() - 1; i >= 0; --i) {
            File f = listToDeploy.get(i)
            try {
                undeploy(f)
            } catch (Exception e) {
                if (!ignoreErrors) {
                    throw e
                }
            }
        }
    }
}