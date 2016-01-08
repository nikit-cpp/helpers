package helpers

/**
 * Created by nik on 08.01.16.
 */
class Artifact {
    String displayName
    String runtimeName

    @Override
    public String toString() {
        return "Artifact{" +
                "displayName='" + displayName + '\'' +
                ", runtimeName='" + runtimeName + '\'' +
                '}';
    }
}
