#!/bin/bash
# run all analysis

java -Xms64g -Xmx64g -jar SDGGenerator.jar "/home/local/CIN/rcaa2/contributionExperiments/configFiles/personalBlogSDG2-zip.properties";java -Xms64g -Xmx64g -jar MappingGenerator.jar "/home/local/CIN/rcaa2/contributionExperiments/configFiles/personalBlogSDG2-zip.properties";java -Xms64g -Xmx64g -jar ClazzIFCAnalysis.jar "/home/local/CIN/rcaa2/contributionExperiments/configFiles/personalBlogSDG2.properties"