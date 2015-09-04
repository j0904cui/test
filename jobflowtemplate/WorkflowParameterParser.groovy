package com.gefk.hyperlane.common

/**
 * Parses the parameters of an oozie workflow and prints
 *  - markdown documentation and
 *  - jobflow template parameters
 * @author Frederic Schmaljohann
 *
 */
class WorkflowParameterParser {

    static main(args) {
        List<Param> requiredParameters = new LinkedList()
        List<Param> optionalParameters = new LinkedList()

        def list = new XmlSlurper().parse(new File(args[0]))
        list.parameters.property.each { it ->
            Param p = new Param();
            p.name = it.name.text().trim()
            String[] x = it.description.text().trim().split("\n", -1)
            boolean required = (x[0].contains("(Required)"))
            p.description = x[0].trim().replace("(Required)", "").replace("(Optional)", "").trim()
            p.example = x[1].trim().replaceAll("^example: ", "")
            p.type = x[2].trim().replaceAll("^type: ", "")

            if (required)
                requiredParameters << p
            else
                optionalParameters << p
        }

        println "### Required Parameters"
        printMarkdown(requiredParameters)

        println "### Optional Parameters"
        printMarkdown(optionalParameters)

        printJson(requiredParameters, false || optionalParameters.isEmpty())
        printJson(optionalParameters, true)
    }

    static void printMarkdown(List parameterList) {
        parameterList.each {
            it -> println (String.format("  * `%s` - %s Example: `%s`", it.name, it.description, it.example))
        }
    }

    static void printJson(List parameterList, boolean lastlist) {
        parameterList.each {
            println "{"
            println ("\t\"name\": \"" + it.name + "\",")
            println ("\t\"type\": \"" + it.type + "\",")
            println ("\t\"exampleValue\": \"" + it.example + "\",")
            println ("\t\"description\": \"" + it.description + "\"")
            if (!lastlist || !it.equals(parameterList[parameterList.size()-1]))
                println "},"
            else
                println "}"
        }
    }

    static class Param {
        def String name
        def String description
        def String example
        def String type
    }
}
