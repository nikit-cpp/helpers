package helpers

/**
 * Created by Nikita on 13.08.2015.
 */
class MongoHelper {
    /**
     *
     * @param host Хост с монгой, на который будет восстановлен дамп
     * @param port
     * @param dbName Имя базы, в которую будет записан дамп
     * @param dumpFolder папка с дампом
     */
    public static dropAndRestore(String host, int port, String dbName, File dumpFolder, File... patches) {
        def commonPart = [
                'mongo',
                "--host",
                host,
                "--port",
                String.valueOf(port),
                dbName
        ].asImmutable();

        def drop = commonPart.collect()
        drop.add("--eval")
        drop.add("db.dropDatabase()")
        Executor.execute(commandWithArgs: drop);

        println "dropped mongo '${dbName}' at ${host}"

        if (null != dumpFolder) {
            println "Started mongo restoring"
            def command = [
                    'mongorestore',
                    "--host",
                    host,
                    "--port",
                    String.valueOf(port),
                    "--db",
                    dbName,
                    "--drop",
                    dumpFolder
            ]
            Executor.execute(commandWithArgs: command);
            println "mongo ${dbName} at ${host} successfully restored"
        }

        for (File file : patches) {
            def patchCommand = commonPart.collect()
            patchCommand.add(file.absolutePath)
            println "applying ${file}"
            Executor.execute(commandWithArgs: patchCommand);
        }
    }
}
