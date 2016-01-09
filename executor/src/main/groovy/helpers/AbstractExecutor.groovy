package helpers

/**
 * Created by nik on 08.01.16.
 */
abstract class AbstractExecutor {
    public abstract ExecutorResult execute2(List<String> commandWithArgs, File inputSource, File workingDirectory, String toProcessInput, boolean printOut)

    public abstract ExecutorResult execute2(Map args)
}