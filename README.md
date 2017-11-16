Sapphire
========

## Organization

This directory contains all of the code for running Sapphire as a Java
library with your Android apps. We also include our example
applications, performance testing applications and scripts for
deploying applications across servers. 

- deployment: scripts for running Sapphire applications on
  servers. Starts the Sapphire servers for the "cloud side" and the
  OMS (called the OTS in the paper).
  
- example_apps: our to do list application and twitter-like
  application.

- generators: scripts for running our compiler for generating Sapphire
  object stubs and DM stubs.

- sapphire: the core Sapphire library. Contains the following packages
  (located in `app/src/main/java/sapphire`)
  - app: Application specific classes, like the starting point for bootstrapping a Sapphire app.
  - common: Basic data structures.
  - compiler: our compiler for generating Sapphire object and DM component stubs.
  - dms: some example deployment managers
  - kernel: the Sapphire kernel server that runs as a library on every node that runs a Sapphire app.
  - oms: the Object Management/Tracking Service. (called the OTS in the paper).
  - runtime: library functions for creating a Sapphire object (hack because we don't have sapphire keywords in the JVM).

- tests: performance testing apps. Good example simple example
  application with one Sapphire object.

## Building Sapphire for x86

1. Build the core Sapphire library:

        $ cd sapphire
        $ mvn package

2. Build an example app:

        $ cd ../example_apps/HanksTodo
        $ mvn package

3. Compile the stubs for the core Sapphire library:

        $ cd ../../generators
        $ python generate_policy_stubs.py

4. Compile the stubs for the example app:

        $ python generate_app_stubs.py

5. Build the core Sapphire library again so that it includes the stubs:

        $ cd ../sapphire
        $ mvn package

6. Build the example app again so that it includes the stubs:

        $ cd ../example_apps/HanksTodo
        $ mvn package

## Running Sapphire on x86

1. Place your servers in deployment/servers.json.

2. Place the config for the app that you want to run in
deployment/app.py. This file needs a starting point for the
server-side and the client-side of your Sapphire app.

3. Run deploy.py to run the app.

## Third-party licenses

The subdirectories `java`, `javax`, and `org` of `sapphire/app/src/main/java/`
contain source code from the Apache Harmony project (with slight modifications
to some files). These source files are distributed under the Apache License.
The full license is in the file `LICENSE-apache-harmony`.
