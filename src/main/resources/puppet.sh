#!/bin/bash

#Get os

os=""
grep "centos" /etc/issue -i -q
if [ $? = '0' ];then
os='centos'
fi
grep "redhat" /etc/issue -i -q
if [ $? = '0' ];then
os='redhat'
fi
grep "ubuntu" /etc/issue -i -q
if [ $? = '0' ];then
os='ubuntu'
fi
echo $os  > /dev/null 2>&1
if [ -z "$os"  ];then
	exit 1
fi


#Check os
case $os in "ubuntu")
	#Check puppetmaster
	dpkg -l |grep  puppetmaster  > /dev/null 2>&1
	if [ $? -ne 0 ]; then
        	echo "Installing puppetmaster"
    		sudo wget https://apt.puppetlabs.com/puppetlabs-release-precise.deb && sudo dpkg -i puppetlabs-release-precise.deb && apt-get update 
		 sudo sudo apt-get install -y puppetmaster
	fi

	if [ $? -ne 0 ]; then
    		echo "Installing puppetmaster package failed. Check network and start again."
    		exit 1
	fi

;;

"centos")

	#Check puppet-server 

	rpm -qa |grep -i puppet-server"  >/dev/null 2>&1
	if [ $? -ne 0 ]; then
        	echo "Installing puppet-server"
        	sudo rpm -ivh https://yum.puppetlabs.com/el/6.5/products/x86_64/puppetlabs-release-6-10.noarch.rpm >/dev/null 2>&1
        	sudo yum install -y puppet-server  >/dev/null 2>&1
        	sudo /etc/init.d/iptables stop

	fi

	if [ $? -ne 0 ]; then
    		echo "Installing puppet-server package failed. Check network and start again."
    		exit 1
	fi

;;

"redhat")


	#Check puppet-server 

	rpm -qa |grep -i puppet-server"  >/dev/null 2>&1
	if [ $? -ne 0 ]; then
        	echo "Installing puppet-server"
        	sudo rpm -ivh https://yum.puppetlabs.com/el/6.5/products/x86_64/puppetlabs-release-6-10.noarch.rpm >/dev/null 2>&1
        	sudo yum install -y puppet-server  >/dev/null 2>&1
        	sudo /etc/init.d/iptables stop

	fi

	if [ $? -ne 0 ]; then
    		echo "Installing puppet-server package failed. Check network and start again."
    		exit 1
	fi
;;
*)
esac

