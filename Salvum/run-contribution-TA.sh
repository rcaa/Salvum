#!/bin/bash
# run all analysis

java -Xms64g -Xmx64g -jar SDGGenerator.jar "/home/local/CIN/rcaa2/contributionExperiments/configFiles/TesteAuthenticationSDG-zip.properties";java -Xms64g -Xmx64g -jar MappingGenerator.jar "/home/local/CIN/rcaa2/contributionExperiments/configFiles/TesteAuthenticationSDG-zip.properties" "contribution";java -Xms64g -Xmx64g -jar ContributionIFCAnalysis.jar "/home/local/CIN/rcaa2/contributionExperiments/configFiles/TesteAuthentication.properties"