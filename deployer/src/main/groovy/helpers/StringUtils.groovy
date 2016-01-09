package helpers

/**
 * Created by nik on 09.01.16.
 */
class StringUtils {
    static int rfind(String string, char inp) {
        int index=-1
        for(int i = string.size()-1; i>=0; i--){
            if(string.getAt(i)==inp){
                index = i
                break;
            }
        }
        return index
    }

}
