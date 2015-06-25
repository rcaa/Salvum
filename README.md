#Salvum
Salvum is a new policy language designed to allow developers specifying constraints for code contributions and enforcing interfaces between existing code and new code contributions.

#Requirements
Java 7 Runtime Environment (JRE) or a Java 7 Development Kit (JDK)

#Building Salvum
Eclipse is recommended for development as the project settings are preconfigured.

1. Import all the projects to your workspace (link provided above)
2. WALA should compile normally. JOANA needs some additional configuration. See JOANA's [official repository](https://github.com/jgf/joana)
3. Add all JOANA and WALA projects previously imported to Salvum Java Build Path
4. Open /Salvum/launchersInfFlowExample.launch, set your own paths for PROGRAM_ARGUMENTS and run it. You can check the preconfigured policies on /Salvum/policies directory
