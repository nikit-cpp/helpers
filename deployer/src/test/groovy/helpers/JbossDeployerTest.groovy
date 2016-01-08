package helpers

import org.junit.Test

//import org.testng.Assert
//import org.testng.annotations.Test

/**
 * Created by nik on 08.01.16.
 */
class JbossDeployerTest {

    List<String> commandWithArgs

    @Test
    void testDeploy() {
        JbossDeployer jbossDeployer = new JbossDeployer(new Server());
        jbossDeployer.listToDeploy = [new File("file")]
        jbossDeployer.executor = new StubExecutor()
        jbossDeployer.deployList()

        println(commandWithArgs)
        println("zxzxzxzxzxxxx")
        //throw  new RuntimeException()
    }

    class StubExecutor extends AbstractExecutor {
        @Override
        int execute(List<String> commandWithArgs_, File inputSource, File workingDirectory, String toProcessInput) {
            commandWithArgs = commandWithArgs_
            return 0
        }
    }
}

