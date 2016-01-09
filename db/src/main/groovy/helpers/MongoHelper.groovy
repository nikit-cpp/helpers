package helpers

/**
 * Created by Nikita on 13.08.2015.
 */
class MongoHelper {
    public static dropAndRestore(Mongo mongo) {
        def ptchs = []
        if (null != mongo.patches) {
            for (String pp : mongo.patches) {
                ptchs.add(new File(pp))
            }
        }
        File dump = null
        if (null!=mongo.dumpFolder){
            dump = new File(mongo.dumpFolder)
        }
        dropAndRestore(mongo.mongoHost, mongo.port, mongo.dbName, dump, ptchs as File[])
    }

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

        if(null == patches){
            return
        }
        for (File file : patches) {
            def patchCommand = commonPart.collect()
            patchCommand.add(file.absolutePath)
            println "applying ${file}"
            Executor.execute(commandWithArgs: patchCommand);
        }
    }
}
