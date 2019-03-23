import groovy.cli.commons.CliBuilder
import groovy.cli.commons.OptionAccessor
import java.text.SimpleDateFormat
import java.text.ParseException

class ArgsParser {

    private  CliBuilder cli
    private  OptionAccessor options

    ArgsParser() {
        this.cli = new CliBuilder(usage: "miningframework [options] [input] [output]",
                header: "the Mining Framework take an input csv file and a name for the output dir (default: output) \n Options: ")

        defParameters()
    }

    private defParameters() {
        this.cli.h(longOpt: 'help', 'Show help for executing commands.')
        this.cli.s(longOpt: 'since', args: 1,
                argName:'date', 'Use commits more recent than a specific date (format DD/MM/YYY).')
        this.cli.u(longOpt: 'until', args: 1,
                argName:'date', 'Use commits older than a specific date(format DD/MM/YYYY).')
        this.cli.i(longOpt: 'injector', args: 1,
                argName:'class', 'Specify the class name of the dependency injector(it has to be in the classpath). default: MiningModule')
        this.cli.p(longOpt: 'push', args: 1, argName: 'link', 'Specify a link to a remote git repository of your own to push the files analysed.')
    }


    private Arguments parse(args) {
        this.options = this.cli.parse(args)
        Arguments resultArgs = new Arguments()
        
        if (this.options.arguments().size() == 0 || this.options.h) {
            resultArgs.setHelp()
            this.cli.usage()
        } else {
            parseInputs(resultArgs)
            parseOptions(resultArgs)
        }

        return resultArgs
    }

    private void parseInputs(Arguments args) {
        if (this.options.arguments().size() > 2)
            throw new InvalidArgsException('Too many arguments passed')    

        String inputFile = this.options.arguments()[0]
        if (!inputFile.endsWith('.csv'))
            throw new InvalidArgsException('The input must be a csv file')
        
        if (!new File(inputFile).exists())
            throw new InvalidArgsException("Could not find input file: ${inputFile}")

        args.setInputPath(inputFile)
            
        if (this.options.arguments().size() > 1) {
            String outputPath = this.options.arguments()[1]
            String parsedOutputPath = outputPath.endsWith("/") ? outputPath.substring(0,dir.lastIndexOf("/")) : outputPath;
            args.setOutputPath(parsedOutputPath)
        }
    }

    private void parseOptions(Arguments args) {
        if (this.options.since) {
            if (!validDate(this.options.since))
                throw new InvalidArgsException('Invalid since date. You must specify it with the format DD/MM/YYYY')
            
            args.setSinceDate(this.options.since) 
        }

        if (this.options.until) {
            if (!validDate(this.options.until))
                throw new InvalidArgsException('Invalid since date. You must specify it with the format DD/MM/YYYY')
               
            args.setUntilDate(this.options.until) 
        }

        if (this.options.injector) {
            try {
                args.setInjector(Class.forName(this.options.injector));
            } catch (Exception e) {
                throw new InvalidArgsException('Invalid injector class. be sure it is in your classpath')
            }
        }

        if (this.options.push) {
            if(!repositoryExists(this.options.push))
                throw new InvalidArgsException('Inexistent remote git repository.')

            args.setResultsRemoteRepository(this.options.push)
        }
    }

    private boolean repositoryExists(String repositoryURL) {
        try {
            final URL url = new URL(repositoryURL)
            HttpURLConnection huc = (HttpURLConnection) url.openConnection()
            huc.setRequestMethod("HEAD")
            return huc.getResponseCode() == 200
        } catch (MalformedURLException e) {
                throw new InvalidArgsException('Invalid url.')
        }
    }

    private Class parseInjector() {
        try {
            return Class.forName(this.options.injector);
        } catch (Exception e) {
            throw new InvalidArgsException('Invalid class')
        }
    }

    private boolean validDate(String value) {
        try {
            new SimpleDateFormat("dd/mm/yyyy").parse(value);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
}