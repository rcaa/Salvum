#!/bin/bash
# run all analysis

java -Xms64g -Xmx64g -jar SDGGenerator.jar "/home/local/CIN/rcaa2/contributionExperiments/configFiles/blojsomSDG-zip2.properties";java -Xms64g -Xmx64g -jar MappingGenerator.jar "/home/local/CIN/rcaa2/contributionExperiments/configFiles/blojsomSDG-zip2.properties";java -Xms64g -Xmx64g -jar ClazzIFCAnalysis.jar "/home/local/CIN/rcaa2/contributionExperiments/configFiles/blojsomSDG2.properties"