#!/usr/bin/python

import os, sys
import subprocess

if __name__ == '__main__':

    sapphire_home = os.path.normpath(os.path.join(os.path.realpath(__file__), '../..'))
   
    inFolder = sapphire_home + '/sapphire/target/classes/sapphire/dms/'
    package = 'sapphire.dms'
    outFolder = sapphire_home + '/sapphire/app/src/main/java/sapphire/dms/stubs/'
    
    cp_sapphire = sapphire_home + '/sapphire/target/sapphire-1.0-SNAPSHOT.jar'
 
    cmd = ['java', '-cp', cp_sapphire, 'sapphire.compiler.StubGenerator', inFolder, package, outFolder]

    print cmd

    p1 = subprocess.Popen(cmd)
    p1.wait()
    print "Done!"
