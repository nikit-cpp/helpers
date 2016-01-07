package helpers

/**
 * Created by Nikita on 06.08.2015.
 */
class Executor {
    static int execute(List<String> commandWithArgs, File inputSource, File workingDirectory) {
        ProcessBuilder pb = new ProcessBuilder()
        pb.command(commandWithArgs as String[])
        if (null != inputSource) {
            pb.redirectInput(inputSource)
        }

        pb.directory(workingDirectory)

        Process process = pb.start()
        // TODO разобраться почему без этого mongorestore не закрывается
        // судя по ошибкам, которые видны при закрытии mongorestore через taskmanager в монге где-то генерятся _id, и из-за отсутствия чтения эга генерация происходит слишком быстро из-за чего дважды генерятся одинаковые id
        process.consumeProcessOutput(System.out, System.err)
        int exitCode = process.waitFor()

        if(0 != exitCode) {
            throw new Exception("Exitcode:" + exitCode + " StdIn:" + process.in.text + " StdErr:" + process.err.text)
        }
        return exitCode
    }

    static int execute(Map args) {
        return execute(args.commandWithArgs, args.inputSource, args.workingDirectory)
    }
}
