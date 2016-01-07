package helpers

/**
 * Created by Nikita on 06.08.2015.
 */
class MysqlHelper {
    public static dropAndRestore(Mysql mysql){
        def ptchs = []
        if(null!=mysql.patches){
            for(String pp: mysql.patches) {
                ptchs.add(new File(pp))
            }
        }
        dropAndRestore(mysql.mysqlHost, mysql.user, mysql.pass, mysql.dbName, ptchs as File[])
    }

    public static dropAndRestore(String mysqlHost, String user, String pass, String dbName, File... patches) {
        def commonCommand = ['mysql']
        if(null != mysqlHost) {
            commonCommand << '-h'
            commonCommand << mysqlHost
        }
        commonCommand += ['-u', user, '-p' + pass, '--default-character-set=utf8']

        commonCommand = commonCommand.asImmutable()

        def clear = commonCommand.collect()
        clear.add("-e")
        clear.add("drop database IF EXISTS ${dbName}; create database ${dbName} DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;")
        Executor.execute(commandWithArgs:clear)
        println "dropped database ${dbName} at ${mysqlHost}"

        for(File file: patches) {
            def commandOnDb = commonCommand.collect()
            commandOnDb.add(dbName)
            println "applying ${file}"
            Executor.execute(commandWithArgs:commandOnDb, inputSource:file)
        }

        println "database ${dbName} at ${mysqlHost} successfully restored"
    }
}
