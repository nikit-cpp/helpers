package examples

/**
 * Created by nik on 09.01.16.
 */
class MongoHelperRunner {
    public static void main(String... args){
        helpers.MongoHelper.dropAndRestore(new helpers.Mongo( mongoHost: '127.0.0.1', dbName:'test', port: 27017))
    }
}
