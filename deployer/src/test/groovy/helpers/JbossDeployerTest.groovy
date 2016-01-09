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

        JbossDeployer jbossStandaloneDeployer = new JbossDeployer(new Server(), '/path/to/jboss/home');
        jbossStandaloneDeployer.listToDeploy = [new File("/path/to/file.jar")]
        jbossStandaloneDeployer.executor = new CaptureExecutor()

        JbossDeployer jbossStandaloneRemoteDeployer = new JbossDeployer(new Server(hostname: '1.2.3.4', port: 8888, username: 'user1', password: 'pass1'), '/path/to/jboss/home');
        jbossStandaloneRemoteDeployer.listToDeploy = [new File("/path/to/file.jar")]
        jbossStandaloneRemoteDeployer.executor = new CaptureExecutor()

        JbossDeployer jbossDeployerDomain = new JbossDeployer(new Server(domain: true), '/path/to/jboss/home');
        jbossDeployerDomain.listToDeploy = [new File("/path/to/file.jar")]
        jbossDeployerDomain.executor = new CaptureExecutor()

        JbossDeployer jbossDeployerRemoteDomain = new JbossDeployer(new Server(domain: true, hostname: '1.2.3.4', username: 'user1', password: 'pass1'), '/path/to/jboss/home');
        jbossDeployerRemoteDomain.listToDeploy = [new File("/path/to/file.jar")]
        jbossDeployerRemoteDomain.executor = new CaptureExecutor()

        JbossDeployer jbossDeployer3ServerGroups = new JbossDeployer(new Server(domain: true, domainServerGroups: ['g1', 'g2', 'g3']), '/path/to/jboss/home');
        jbossDeployer3ServerGroups.listToDeploy = [new File("/path/to/file1.jar")]
        jbossDeployer3ServerGroups.executor = new CaptureExecutor()


        return [
                [
                        'localhost standalone',
                        jbossStandaloneDeployer,
                        [
                                deploy: [
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
                                ],
                                undeploy: [
                                        [
                                                'java',
                                                '-Dlogging.configuration=file:/path/to/jboss/home/bin/jboss-cli-logging.properties',
                                                '-jar',
                                                '/path/to/jboss/home/jboss-modules.jar',
                                                '-mp',
                                                '/path/to/jboss/home/modules',
                                                'org.jboss.as.cli',
                                                '-c',
                                                '--command=undeploy file.jar'
                                        ]
                                ]
                        ]
                ],


                [
                        'remote standalone',
                        jbossStandaloneRemoteDeployer,
                        [
                                deploy: [
                                        [
                                                'java',
                                                '-Dlogging.configuration=file:/path/to/jboss/home/bin/jboss-cli-logging.properties',
                                                '-jar',
                                                '/path/to/jboss/home/jboss-modules.jar',
                                                '-mp',
                                                '/path/to/jboss/home/modules',
                                                'org.jboss.as.cli',
                                                '-c',
                                                '--controller=1.2.3.4:8888',
                                                '--user=user1',
                                                '--password=pass1',
                                                '--command=deploy /path/to/file.jar --force --name=file.jar --runtime-name=file.jar'
                                        ]
                                ]
                        ]
                ],

                [
                        'localhost domain',
                        jbossDeployerDomain,
                        [
                                deploy: [
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
                                ],
                                undeploy: [
                                        [
                                                'java',
                                                '-Dlogging.configuration=file:/path/to/jboss/home/bin/jboss-cli-logging.properties',
                                                '-jar',
                                                '/path/to/jboss/home/jboss-modules.jar',
                                                '-mp',
                                                '/path/to/jboss/home/modules',
                                                'org.jboss.as.cli',
                                                '-c',
                                                '--command=/server-group=main-server-group/deployment=file.jar:remove'
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
                                                '--command=undeploy file.jar'
                                        ]
                                ]
                        ]

                ],


                [
                        'remote domain',
                        jbossDeployerRemoteDomain,
                        [
                                deploy: [

                                        [
                                                'java',
                                                '-Dlogging.configuration=file:/path/to/jboss/home/bin/jboss-cli-logging.properties',
                                                '-jar',
                                                '/path/to/jboss/home/jboss-modules.jar',
                                                '-mp',
                                                '/path/to/jboss/home/modules',
                                                'org.jboss.as.cli',
                                                '-c',
                                                '--controller=1.2.3.4:9990',
                                                '--user=user1',
                                                '--password=pass1',
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
                                                '--controller=1.2.3.4:9990',
                                                '--user=user1',
                                                '--password=pass1',
                                                '--command=deploy --name=file.jar --server-groups=main-server-group'
                                        ]

                                ]
                        ]
                ],



                [
                        "3 Server Groups",
                        jbossDeployer3ServerGroups,
                        [
                                deploy: [

                                        [
                                                'java',
                                                '-Dlogging.configuration=file:/path/to/jboss/home/bin/jboss-cli-logging.properties',
                                                '-jar',
                                                '/path/to/jboss/home/jboss-modules.jar',
                                                '-mp',
                                                '/path/to/jboss/home/modules',
                                                'org.jboss.as.cli',
                                                '-c',
                                                '--command=deploy /path/to/file1.jar --disabled --name=file1.jar --runtime-name=file1.jar'
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
                                                '--command=deploy --name=file1.jar --server-groups=g1,g2,g3'
                                        ]

                                ],
                                undeploy: [
                                        [
                                                'java',
                                                '-Dlogging.configuration=file:/path/to/jboss/home/bin/jboss-cli-logging.properties',
                                                '-jar',
                                                '/path/to/jboss/home/jboss-modules.jar',
                                                '-mp',
                                                '/path/to/jboss/home/modules',
                                                'org.jboss.as.cli',
                                                '-c',
                                                '--command=/server-group=g1/deployment=file1.jar:remove'
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
                                                '--command=/server-group=g2/deployment=file1.jar:remove'
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
                                                '--command=/server-group=g3/deployment=file1.jar:remove'
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
                                                '--command=undeploy file1.jar'
                                        ]

                                ]

                        ]
                ]
        ]

    }


    @Test(dataProvider = "dataMethod")
    void testDeploy(String testName, JbossDeployer jbossDeployer, Map deployAndUndeployCommands) {
        List<List<String>> expectedDeployCommands = deployAndUndeployCommands.deploy
        List<List<String>> expectedUndeployCommands = deployAndUndeployCommands.undeploy

        println("="*20)
        println("Testing \"${testName}\"")

        exec(jbossDeployer, expectedDeployCommands, true)

        if(null!=expectedUndeployCommands) {
            exec(jbossDeployer, expectedUndeployCommands, false)
        }
        println("="*20)
        println()
    }

    private void exec(JbossDeployer jbossDeployer, List<List<String>> expectedDeployCommands, boolean deplFlag) {
        if(deplFlag) {
            jbossDeployer.deployList()
        } else {
            jbossDeployer.undeployList()
        }

        println 'Executed:'
        for (def cmd : jbossDeployer.executor.executedCommands)
            println(cmd)
        println()

        println 'Expected:'
        for (def cmd : expectedDeployCommands) {
            println(cmd)
        }

        for (int i = 0; i < expectedDeployCommands.size(); ++i) {
            Assert.assertTrue(ListStringComparer.compare(jbossDeployer.executor.executedCommands.get(i), expectedDeployCommands.get(i)))
        }
        println()

        Assert.assertEquals(expectedDeployCommands.size(), jbossDeployer.executor.executedCommands.size())

        jbossDeployer.executor.executedCommands = []
    }

}

class ListStringComparer {
    static boolean compare(List list1, List list2) {

        for (int i = 0; i < list1.size(); ++i) {
            if (!list1.get(i).toString().equals(list2.get(i).toString())) {
                //println("" +list1.get(i) + " "+ i)
                return false
            }
        }
        return true
    }
}

class CaptureExecutor extends AbstractExecutor {
    List<List<String>> executedCommands = []

    @Override
    ExecutorResult execute2(List<String> commandWithArgs_, File inputSource, File workingDirectory, String toProcessInput, boolean unused, boolean unused2) {
        executedCommands.add commandWithArgs_
        return null
    }

    @Override
    ExecutorResult execute2(Map args) {
        executedCommands.add args.commandWithArgs
        return null
    }
}
