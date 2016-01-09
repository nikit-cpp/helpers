package helpers

import java.nio.charset.StandardCharsets

/**
 * Created by Nikita on 06.08.2015.
 */
class Executor extends AbstractExecutor {
    ExecutorResult execute2(List<String> commandWithArgs, File inputSource, File workingDirectory, String toProcessInput, boolean printOut) {
        ProcessBuilder pb = new ProcessBuilder()
        pb.command(commandWithArgs as String[])
        if (null != inputSource) {
            pb.redirectInput(inputSource)
        }

        pb.directory(workingDirectory)

        Process process = pb.start()

        if (null != toProcessInput) {
            def inputToProcess = process.outputStream
            inputToProcess.write(toProcessInput.getBytes(StandardCharsets.UTF_8))
            inputToProcess.flush()
            inputToProcess.close()
        }

        if (printOut) {
            process.consumeProcessOutput(System.out, System.err)
        }
        int exitCode = process.waitFor()

        if (0 != exitCode) {
            throw new Exception("Exitcode:" + exitCode + " StdIn:" + process.in.text + " StdErr:" + process.err.text)
        }
        return new ExecutorResult(exitcode: exitCode, stdout: process.in.text.split('\n'), stderr: process.err.text.split('\n'))
    }

    public ExecutorResult execute2(Map args) {
        return execute2(args.commandWithArgs, args.inputSource, args.workingDirectory, args.toProcessInput, args.printOut)
    }

    /**
     * Compatibility method
     * @param args
     * @return
     */
    public static ExecutorResult execute(Map args) {
        Executor executor = new Executor()
        return executor.execute2(args.commandWithArgs, args.inputSource, args.workingDirectory, args.toProcessInput, (args.printOut==null) ? true : args.printOut)
    }
}
