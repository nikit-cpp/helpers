package helpers

/**
 * Created by nik on 08.01.16.
 */
abstract class AbstractExecutor {
    public int execute(Map args) {
        return execute(args.commandWithArgs, args.inputSource, args.workingDirectory, args.toProcessInput)
    }

    public
    abstract int execute(List<String> commandWithArgs, File inputSource, File workingDirectory, String toProcessInput)
}
