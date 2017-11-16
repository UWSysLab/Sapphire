#!/usr/bin/python

import os, sys
import subprocess
from time import sleep
import atexit
import signal
from app import *
import getpass
import json
import os

ssh_cmd = "ssh"

# Change this path to your desired log output directory. This path must exist
# on each of the client/server/oms machines specified in 'servers.json.'
log_folder = "~/sapphire_logs"

def run_cmd(cmd):
    print reduce(lambda x, y: x + " " + y, cmd, "")
    return subprocess.Popen(cmd)

def parse_server_config(server_file):
    f = open(server_file,"r")
    config = json.JSONDecoder().decode(f.read())
    return config


def start_oms(oms, server_file, cp_app, cp_sapphire, p_log):
    hostname = oms["hostname"]
    port = oms["port"]

    print 'Starting OMS on '+hostname+":"+port
    cmd = [ssh_cmd, hostname]
    cmd += ['java']
    cmd += ['-Djava.util.logging.config.file=\"' + p_log + '\"']
    cmd += ['-cp ' + cp_app + ':' + cp_sapphire]
    cmd += ['sapphire.oms.OMSServerImpl', hostname, port]
    cmd += [app_class]
    cmd += [">", log_folder+"/oms-log."+hostname+"."+port, "2>&1 &"]
    run_cmd(cmd)

    sleep(2)

def start_servers(servers, oms, cp_app, cp_sapphire, p_log):
    for s in servers:
        hostname = s["hostname"]
        port = s["port"]
        print "Starting kernel server on "+hostname+":"+port

        cmd = [ssh_cmd, hostname]
        cmd += ['java']
        cmd += ['-Djava.util.logging.config.file=\"' + p_log + '\"']
        cmd += ['-cp ' + cp_app + ':' + cp_sapphire]
        cmd += ['sapphire.kernel.server.KernelServerImpl']
        cmd += [hostname, port, oms["hostname"], oms["port"]]
        cmd += [">", log_folder+"/log."+hostname+"."+port, "2>&1 &"]
        run_cmd(cmd)
    
    sleep(2)


def start_clients(clients, oms, cp_app, cp_sapphire, p_log):

    for client in clients:
        hostname = client["hostname"]
        port = client["port"]
        print 'Starting App on '+hostname+":"+port

        cmd = [ssh_cmd, hostname]
        cmd += ['java']
        cmd += ['-Djava.util.logging.config.file=\"' + p_log + '\"']
        cmd += ['-cp ' + cp_app + ':' + cp_sapphire]
        cmd += [app_client, oms["hostname"], oms["port"], hostname, port]
        cmd += [">", log_folder+"/client-log."+hostname+"."+port, "2>&1"]
        run_cmd(cmd)

if __name__ == '__main__':
    sapphire_home = os.path.normpath(os.path.join(os.path.realpath(__file__), '../..'))

    # /target/<app-name>-1.0-SNAPSHOT.jar is generated after you run 'mvn package' in the app directory
    # /target/sapphire-1.0-SNAPSHOT.jar is generated after you run 'mvn package' in the 'sapphire' directory
    cp_app =  sapphire_home + '/example_apps/' + app_name + '/target/' + app_name.lower() + '-1.0-SNAPSHOT.jar'
    cp_sapphire = sapphire_home + '/sapphire/target/sapphire-1.0-SNAPSHOT.jar'
    p_log = sapphire_home + '/sapphire/logging.properties'

    server_file = "servers.json"
    config = parse_server_config(server_file)
    start_oms(config["oms"], os.path.abspath(server_file), cp_app, cp_sapphire, p_log)
    start_servers(config["servers"], config["oms"], cp_app, cp_sapphire, p_log)
    start_clients(config["clients"], config["oms"], cp_app, cp_sapphire, p_log)
    
