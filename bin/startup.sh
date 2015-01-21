#*******************************************************************************
#  Copyright 2014 Paxcel Technologies
#
#  Licensed under the Apache License, Version 2.0 (the "License");
#  you may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#
#  http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.
#*******************************************************************************
#!/bin/bash
#Get os Details
os=""
ls /proc/version > /dev/null 2>&1 
if [ $? = '0' ];then
grep "centos" /proc/version -i -q 
if [ $? = '0' ];then
os='centos'

fi
grep "redhat" /proc/version -i -q 
if [ $? = '0' ];then
os='redhat'

fi
grep "ubuntu" /proc/version -i -q 
if [ $? = '0' ];then
os='ubuntu'

fi
else

uname -a |grep Darwin > /dev/null 2>&1
if [ $? = '0' ];then
os='macos'

fi
echo $os  > /dev/null 2>&1
if [ -z "$os"  ];then
exit 1

fi
fi

#Check Java
java -version > /dev/null 2>&1
if [ $? -ne 0 ]; then
    echo "Please install java it is not installed"
		exit 1
fi

#Check os

case $os in "ubuntu")

#Check Oracle Virtual Box

dpkg -l |grep  virtualbox  > /dev/null 2>&1
if [ $? -ne 0 ]; then
    echo "Installing Oracle Virtual Box"
      	sudo sh -c "echo 'deb http://download.virtualbox.org/virtualbox/debian precise  contrib ' > /etc/apt/sources.list.d/virtualbox.list" && wget -q http://download.virtualbox.org/virtualbox/debian/oracle_vbox.asc -O-  | sudo apt-key add - && sudo apt-get update
		sudo apt-get install virtualbox-4.3 dkms
fi


if [ $? -ne 0 ]; then
exit 1
fi


#Check Docker
dpkg -l |grep  docker  >/dev/null 2>&1
if [ $? -ne 0 ]; then
    #Script runs on Ubuntu Trusty 14.04 
	echo "Installing Docker"
		sudo apt-get update
		sudo apt-get install docker.io
		sudo service docker restart
fi

if [ $? -ne 0 ]; then
      echo "Installing Docker package failed. Check network and start again."
      exit 1
fi

#Check Vagrant
dpkg -l |grep  vagrant  >/dev/null 2>&1
if [ $? -ne 0 ]; then
	echo "Installing Vagrant"
		sudo wget https://dl.bintray.com/mitchellh/vagrant/vagrant_1.6.5_x86_64.deb
		dpkg -i vagrant_1.6.5_x86_64.deb
fi

if [ $? -ne 0 ]; then
      echo "Installing vagrant package failed. Check network and start again."
      exit 1
fi
;;

"centos")

#Check Oracle Virttualbox
rpm -qa |grep -i virtualbox  >/dev/null 2>&1
if [ $? -ne 0 ]; then
	echo "Installing Oracle Virtual Box"
        sudo wget http://download.virtualbox.org/virtualbox/rpm/rhel/virtualbox.repo  >/dev/null 2>&1
        sudo mv virtualbox.repo /etc/yum.repos.d/  >/dev/null 2>&1
        sudo yum -y install VirtualBox-4.3
fi

if [ $? -ne 0 ]; then
      echo "Installing virtualbox package failed. Check network and start again."
      exit 1
fi

#Check Docker
rpm -qa |grep -i docker  >/dev/null 2>&1
if [ $? -ne 0 ]; then
	echo "Installing Docker"
        sudo yum -y install http://boxupp.com/data/docker-io-1.1.2-1.el6.x86_64.rpm
		cp -rf  docker /etc/sysconfig/docker
		service docker restart
fi

if [ $? -ne 0 ]; then
    echo "Installing docker package failed. Check network and start again."
		exit 1

fi

#Check Vagrant
rpm -qa |grep -i vagrant  >/dev/null 2>&1
if [ $? -ne 0 ]; then
	echo "Installing Vagrant"
        sudo yum -y install http://www.boxupp.com/data/vagrant.rpm
fi

if [ $? -ne 0 ]; then
    echo "Installing vagrant package failed. Check network and start again."
		exit 1

fi

;;

"redhat")

#Check Oracle Virtual Box 
rpm -qa |grep -i virtualbox  >/dev/null 2>&1
if [ $? -ne 0 ]; then

	echo "Installing Oracle Virtual Box"
        sudo wget http://download.virtualbox.org/virtualbox/rpm/rhel/virtualbox.repo  >/dev/null 2>&1
        sudo mv virtualbox.repo /etc/yum.repos.d/  >/dev/null 2>&1
        sudo yum -y install VirtualBox-4.3
fi

if [ $? -ne 0 ]; then
      echo "Installing virtualbox package failed. Check network and start again."
      exit 1
fi

#Check Docker
rpm -qa |grep -i docker  >/dev/null 2>&1
if [ $? -ne 0 ]; then
	echo "Installing Docker"
        sudo yum -y install http://boxupp.com/data/docker-io-1.1.2-1.el6.x86_64.rpm
		cp -rf  docker /etc/sysconfig/docker
		service docker restart
fi

if [ $? -ne 0 ]; then
    echo "Installing docker package failed. Check network and start again."
		exit 1

fi

#Check Vagrant

rpm -qa |grep -i vagrant  >/dev/null 2>&1
if [ $? -ne 0 ]; then
         echo "Installing Vagrant"
         sudo yum -y install http://www.boxupp.com/data/vagrant.rpm
fi

if [ $? -ne 0 ]; then
      echo "Installing vagrant package failed. Check network and start again."
      exit 1
fi

## check Docker

rpm -qa |grep - docker  >/dev/null 2>&1
if [ $? -ne 0 ]; then
	echo "Installing Docker"
     sudo yum -y  install http://mirror-fpt-telecom.fpt.net/fedora/epel/6/i386/epel-release-6-8.noarch.rpm
	 sudo yum -y  install docker-io
	 sudo cp -rf docker.txt /etc/sysconfig/docker
	 sudo  service docker start
fi

if [ $? -ne 0 ]; then
    echo "Installing docker package failed. Check network and start again."
		exit 1

fi

;;

"macos")

#Check Oracle Virtual Box
pkgutil --pkgs |grep virtualbox >/dev/null 2>&1
if [ $? -ne 0 ]; then
	echo "Installing Oracle Virtual Box"
        sudo curl -O http://dlc.sun.com.edgesuite.net/virtualbox/4.3.14/VirtualBox-4.3.14-95030-OSX.dmg  >/dev/null 2>&1
        sudo hdiutil mount VirtualBox-4.3.14-95030-OSX.dmg 
		sudo installer -pkg /Volumes/VirtualBox/VirtualBox.pkg -target /
        fi

if [ $? -ne 0 ]; then
	echo "Installing virtualbox package failed. Check network and start again."
        exit 1
        fi

#Check Vagrant
pkgutil --pkgs |grep vagrant  >/dev/null 2>&1
if [ $? -ne 0 ]; then
	echo "Installing Vagrant"
        sudo curl -O http://www.boxupp.com/data/vagrant_1.6.3.dmg
        sudo hdiutil mount vagrant_1.6.3.dmg
		sudo installer -pkg /Volumes/Vagrant/Vagrant.pkg -target /

fi
if [ $? -ne 0 ]; then
	echo "Installing vagrant package failed. Check network and start again."
        exit 1
        fi
;;
*)

esac

cd ..

echo $JAVA_HOME  >/dev/null 2>&1
echo $PWD     >/dev/null 2>&1
BOXUPP_HOME=$PWD

CLASSPATH=$BOXUPP_HOME/:$BOXUPP_HOME/lib/*:$BOXUPP_HOME/config/ 
export CLASSPATH   

PATH=$JAVA_HOME/bin:$PATH
export PATH

JAVA="$JAVA_HOME/bin/java"
# GC options
GC_OPTS="-verbose:gc -XX:+PrintGCTimeStamps -XX:+PrintGCDetails"
GC_MEM="-XX:NewSize=384m -XX:MaxNewSize=384m -XX:SurvivorRatio=4 -Xms512m -Xmx512m"

nohup java $CG_OPTS $GC_MEM -XX:+UseConcMarkSweepGC -XX:+UseParNewGC  -cp $CLASSPATH  com.boxupp.init.Boxupp >> logs/BoxUpp.log  2>&1 &
echo "Starting Boxupp"

if [ $? -ne 0 ]; then
	echo "Error starting BoxUpp Please check logs"
exit 1
fi
