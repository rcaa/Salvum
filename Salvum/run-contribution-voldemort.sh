#!/bin/bash
# run all analysis

java -Xms64g -Xmx64g -jar SDGGenerator.jar "/home/local/CIN/rcaa2/contributionExperiments/configFiles/voldemortSDG-zip2.properties";java -Xms64g -Xmx64g -jar MappingGenerator.jar "/home/local/CIN/rcaa2/contributionExperiments/configFiles/voldemortSDG-zip2.properties" "contribution";java -Xms64g -Xmx64g -jar ContributionIFCAnalysis.jar "/home/local/CIN/rcaa2/contributionExperiments/configFiles/voldemortSDG2.properties"