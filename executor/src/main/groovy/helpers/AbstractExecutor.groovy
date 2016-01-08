package helpers

/**
 * Created by nik on 08.01.16.
 */
abstract class AbstractExecutor {
    public ExecutorResult execute(Map args) {
        return execute(args.commandWithArgs, args.inputSource, args.workingDirectory, args.toProcessInput, args.printOut)
    }

    public abstract ExecutorResult execute(List<String> commandWithArgs, File inputSource, File workingDirectory, String toProcessInput, boolean printOut)
}