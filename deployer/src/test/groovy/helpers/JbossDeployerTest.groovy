package helpers

import org.testng.Assert
import org.testng.annotations.DataProvider
import org.testng.annotations.Test

/**
 * Created by nik on 08.01.16.
 */
class JbossDeployerTest {

    @DataProvider
    public Object[][] dataMethod() {
        JbossDeployer jbossDeployerDomain = new JbossDeployer(new Server(domain: true), '/path/to/jboss/home');
        jbossDeployerDomain.listToDeploy = [new File("/path/to/file.jar")]
        jbossDeployerDomain.executor = new StubExecutor()

        return [
                [
                        'localhost domain',
                        jbossDeployerDomain,
                        [
                                [
                                     'java',
                                     '-Dlogging.configuration=file:/path/to/jboss/home/bin/jboss-cli-logging.properties',
                                     '-jar',
                                     '/path/to/jboss/home/jboss-modules.jar',
                                     '-mp',
                                     '/path/to/jboss/home/modules',
                                     'org.jboss.as.cli',
                                     '-c',
                                     '--command=deploy /path/to/file.jar --disabled --name=file.jar --runtime-name=file.jar'
                             ],
                             [
                                     'java',
                                     '-Dlogging.configuration=file:/path/to/jboss/home/bin/jboss-cli-logging.properties',
                                     '-jar',
                                     '/path/to/jboss/home/jboss-modules.jar',
                                     '-mp',
                                     '/path/to/jboss/home/modules',
                                     'org.jboss.as.cli',
                                     '-c',
                                     '--command=deploy --name=file.jar --server-groups=main-server-group'
                             ]
                        ]
                ],


                //[ "two" ]
        ];
    }

    /*@Test(dataProvider = "dataMethod")
    public void testMethod(String param) {
        System.out.println("The parameter value is: " + param);
    }*/

    @Test
    void testDeployStandalone() {
        def expected =
                [
                        'java',
                        '-Dlogging.configuration=file:/path/to/jboss/home/bin/jboss-cli-logging.properties',
                        '-jar',
                        '/path/to/jboss/home/jboss-modules.jar',
                        '-mp',
                        '/path/to/jboss/home/modules',
                        'org.jboss.as.cli',
                        '-c',
                        '--command=deploy /path/to/file.jar --force --name=file.jar --runtime-name=file.jar'
                ]

        JbossDeployer jbossDeployer = new JbossDeployer(new Server(), '/path/to/jboss/home');
        jbossDeployer.listToDeploy = [new File("/path/to/file.jar")]
        jbossDeployer.executor = new StubExecutor()
        jbossDeployer.deployList()

        Assert.assertTrue(ListStringComparer.compare(jbossDeployer.executor.executedCommands.get(0), expected))
    }

    @Test(dataProvider = "dataMethod")
    void testDeployDomain(String testName, JbossDeployer jbossDeployer, List<List<String>> expecteds) {
        println("Testing \"${testName}\"")

        jbossDeployer.deployList()

        Assert.assertEquals(2, jbossDeployer.executor.executedCommands.size())

        println 'Executed'
        println(jbossDeployer.executor.executedCommands.get(0))
        println(jbossDeployer.executor.executedCommands.get(1))
        println()
        println 'Expected'
        println(expecteds.get(0))
        println(expecteds.get(1))

        Assert.assertTrue(ListStringComparer.compare(jbossDeployer.executor.executedCommands.get(0), expecteds.get(0)))
        Assert.assertTrue(ListStringComparer.compare(jbossDeployer.executor.executedCommands.get(1), expecteds.get(1)))
    }

    @Test
    void testDeployDomain3ServerGroups() {
        def expected1 =
                [
                        'java',
                        '-Dlogging.configuration=file:/path/to/jboss/home/bin/jboss-cli-logging.properties',
                        '-jar',
                        '/path/to/jboss/home/jboss-modules.jar',
                        '-mp',
                        '/path/to/jboss/home/modules',
                        'org.jboss.as.cli',
                        '-c',
                        '--command=deploy /path/to/file.jar --disabled --name=file.jar --runtime-name=file.jar'
                ]

        def expected2 =
                [
                        'java',
                        '-Dlogging.configuration=file:/path/to/jboss/home/bin/jboss-cli-logging.properties',
                        '-jar',
                        '/path/to/jboss/home/jboss-modules.jar',
                        '-mp',
                        '/path/to/jboss/home/modules',
                        'org.jboss.as.cli',
                        '-c',
                        '--command=deploy --name=file.jar --server-groups=g1,g2,g3'
                ]


        JbossDeployer jbossDeployer = new JbossDeployer(new Server(domain: true, domainServerGroups:['g1', 'g2', 'g3']), '/path/to/jboss/home');
        jbossDeployer.listToDeploy = [new File("/path/to/file.jar")]
        jbossDeployer.executor = new StubExecutor()
        jbossDeployer.deployList()

        Assert.assertEquals(2, jbossDeployer.executor.executedCommands.size())

        println 'Executed'
        println(jbossDeployer.executor.executedCommands.get(0))
        println(jbossDeployer.executor.executedCommands.get(1))
        println()
        println 'Expected'
        println(expected1)

        Assert.assertTrue(ListStringComparer.compare(jbossDeployer.executor.executedCommands.get(0), expected1))
        Assert.assertTrue(ListStringComparer.compare(jbossDeployer.executor.executedCommands.get(1), expected2))
    }

}

class ListStringComparer{
    static boolean compare(List list1, List list2){

        for(int i=0; i<list1.size(); ++i){
            if(!list1.get(i).toString().equals(list2.get(i).toString())){
                //println("" +list1.get(i) + " "+ i)
                return false
            }
        }
        return true
    }
}

class StubExecutor extends AbstractExecutor {
    List<List<String>> executedCommands = []

    @Override
    int execute(List<String> commandWithArgs_, File inputSource, File workingDirectory, String toProcessInput) {
        executedCommands.add commandWithArgs_
        return 0
    }
}
