#!/usr/bin/env python
# Copyright 2014-2015 VMware, Inc.  All rights reserved.

import os
import sys
const= ";"

##### Import dashboards #####
if sys.platform.startswith('linux') == True:
	print("This is Linux")
	
	cmd = "pushd $VCOPS_BASE/tools/opscli"
	cmd = cmd + const + "$VMWARE_PYTHON_BIN ops-cli.py control redescribe --force"
	
#	print("starting importing dashboards")
	
#	for file in os.listdir("/usr/lib/vmware-vcops/user/plugins/inbound/ESightAdapter/conf/dashboards/"):
#		cmd = cmd + const + "$VMWARE_PYTHON_BIN ops-cli.py dashboard import admin $VCOPS_BASE/user/plugins/inbound/ESightAdapter/conf/dashboards/" + file + " --share all" + " --set 0" + " --create" + " --force" + " --retry 3"
#	os.system(cmd + const + "popd")
	
#	print("completed importing dashboards")
	
	viewcmd = "pushd $VCOPS_BASE/tools/opscli"
	
	print("starting importing views")
	
	for file in os.listdir("/usr/lib/vmware-vcops/user/plugins/inbound/ESightAdapter/conf/views/"):
		viewcmd = viewcmd + const + "$VMWARE_PYTHON_BIN ops-cli.py view import $VCOPS_BASE/user/plugins/inbound/ESightAdapter/conf/views/" + file + " --force"
	os.system(viewcmd + const + "popd")
	
	print("completed importing views")
	
#	reskndcmd = "pushd $VCOPS_BASE/tools/opscli"
#	
#	print("starting importing resource kind metrics")
#	
#	for file in os.listdir("/usr/lib/vmware-vcops/user/plugins/inbound/ESightAdapter/conf/reskndmetrics/"):
#		reskndcmd = reskndcmd + const + "$VMWARE_PYTHON_BIN ops-cli.py file import reskndmetric $VCOPS_BASE/user/plugins/inbound/ESightAdapter/conf/reskndmetrics/" + file + " --force"
#	os.system(reskndcmd + const + "popd")
#	
#	print("completed importing views")
#	
#	reportcmd = "pushd $VCOPS_BASE/tools/opscli"
#	print("starting importing reports")
#	
#	for file in os.listdir("/usr/lib/vmware-vcops/user/plugins/inbound/ESightAdapter/conf/reports/"):
#		reportcmd = reportcmd + const + "$VMWARE_PYTHON_BIN ops-cli.py report import $VCOPS_BASE/user/plugins/inbound/ESightAdapter/conf/reports/" + file + " --force"
#	os.system(reportcmd + const + "popd")
#	
#	print("completed importing reports")
	
	print("Completed Execution for Linux")

if sys.platform.startswith('win') == True:
	print("This is Windows")
	
	vcopsbase = os.getenv('VCOPS_BASE')
	vpythonbin = os.getenv('VMWARE_PYTHON_BIN')
	print vpythonbin
	vcopsclipath = vcopsbase + "\\tools\\opscli\\"
	cmd = "pushd " + vcopsclipath
	os.system(cmd)
	cmd = vpythonbin + " " + vcopsclipath + "ops-cli.py control redescribe --force"
	os.system(cmd)
	os.system("popd")
	
	vcopsdbpath = vcopsbase + "\\user\\plugins\\inbound\\ESightAdapter\\conf\\dashboard\\"
	
	for file in os.listdir(vcopsdbpath):
		cmd = "pushd " + vcopsclipath
		os.system(cmd)
		cmd = vpythonbin + " " + vcopsclipath + "ops-cli.py dashboard import admin " + vcopsdbpath + file + " --share all" + " --set 0" + " --create" + " --force" + " --retry 3"
		os.system(cmd)
		os.system("popd")
		
	print("Completed importing dashboards for Windows")
	
	vcopsviewpath = vcopsbase + "\\user\\plugins\\inbound\\ESightAdapter\\conf\\views\\"
	
	print("Starting importing views for Windows")
	
	for file in os.listdir(vcopsviewpath):
		viewcmd = "pushd " + vcopsclipath
		os.system(viewcmd)
		viewcmd = vpythonbin + " " + vcopsclipath + "ops-cli.py view import  " + vcopsviewpath + file + " --force"
		os.system(viewcmd)
		os.system("popd")
	print("Completed importing views for Windows")
	
	vcopsmetricpath = vcopsbase + "\\user\\plugins\\inbound\\ESightAdapter\\conf\\reskndmetrics\\"
	
	print("Starting importing resource kind metrics for Windows")
	
	for file in os.listdir(vcopsmetricpath):
		reskndcmd = "pushd " + vcopsclipath
		os.system(reskndcmd)
		reskndcmd = vpythonbin + " " + vcopsclipath + "ops-cli.py file import reskndmetric " + vcopsmetricpath + file + " --force"
		os.system(reskndcmd)
		os.system("popd")
	print("Completed importing resource kind metrics for Windows")
	
	vcopsreportpath = vcopsbase + "\\user\\plugins\\inbound\\ESightAdapter\\conf\\reports\\"
	print("Starting importing reports for Windows")
	
	for file in os.listdir(vcopsreportpath):
		reportcmd = "pushd " + vcopsclipath
		os.system(reportcmd)
		reportcmd = vpythonbin + " " + vcopsclipath + "ops-cli.py report import  " + vcopsreportpath + file + " --force"
		os.system(reportcmd)
		os.system("popd")
	print("Completed importing reports for Windows")
	
	print("Completed Execution for Windows")

print("Completed ESight Adapter post installation process")