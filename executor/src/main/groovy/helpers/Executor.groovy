package helpers

import java.nio.charset.StandardCharsets

/**
 * Created by Nikita on 06.08.2015.
 */
class Executor extends AbstractExecutor {
    ExecutorResult execute(List<String> commandWithArgs, File inputSource, File workingDirectory, String toProcessInput, boolean printOut) {
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

        // TODO разобраться почему без этого mongorestore не закрывается
        // судя по ошибкам, которые видны при закрытии mongorestore через taskmanager в монге где-то генерятся _id, и из-за отсутствия чтения эга генерация происходит слишком быстро из-за чего дважды генерятся одинаковые id
        if (printOut) {
            process.consumeProcessOutput(System.out, System.err)
        }
        int exitCode = process.waitFor()

        if (0 != exitCode) {
            throw new Exception("Exitcode:" + exitCode + " StdIn:" + process.in.text + " StdErr:" + process.err.text)
        }
        return new ExecutorResult(exitcode: exitCode, stdout: process.in.text.split('\n'), stderr: process.err.text.split('\n'))
    }

    public static ExecutorResult execute(Map args) {
        Executor executor = new Executor()
        return executor.execute(args.commandWithArgs, args.inputSource, args.workingDirectory, args.toProcessInput, args.printOut)
    }

}
