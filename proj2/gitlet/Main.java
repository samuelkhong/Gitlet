package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Samuel
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        // TODO: what if args is empty?
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                Repository.init();
                break;

            case "add":
                Repository.add(args[1]);
                break;

            case "commit":
                Repository.commit(args[1]);
                break;

            case "rm":
                Repository.rm(args[1]);
                break;

            case "log":
                Repository.log();
                break;

            case "global-log":
                Repository.globeLog();
                break;

            case "status":
                Repository.status();
                break;

            case "find":
                Repository.find(args[1]);
                break;

            case "branch":
                Repository.branch(args[1]);
                break;

            case "checkout":
                // select branch input
                if (args.length == 2) {
                    Repository.checkoutBranch(args[1]);
                }
                // select checkout file
                else if (args.length == 3) {
                    Repository.checkoutFile(args[2]);
                }
                else if (args.length == 4) {
                    Repository.checkoutCommitFile(args[1], args[3]);
                }
                break;

            case "rm-branch":
                Repository.rmBranch(args[1]);
                break;

            case "reset":
                Repository.reset(args[1]);
                break;
        }
    }
}
