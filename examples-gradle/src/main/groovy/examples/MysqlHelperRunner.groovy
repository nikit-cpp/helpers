package examples

/**
 * Created by nik on 09.01.16.
 */
class MysqlHelperRunner {
    public static void main(String... args){
        helpers.MysqlHelper.dropAndRestore(new helpers.Mysql(user:'root', pass:'1488', dbName:'msp_email'))

    }
}
