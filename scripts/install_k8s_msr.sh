#!/bin/bash

######### ** FOR MASTER NODE ** #########

hostname k8s-msr-01
echo "k8s-msr-01" > /etc/hostname

export AWS_ACCESS_KEY_ID=<Put_Your_Key>
export AWS_SECRET_ACCESS_KEY=<Put_Your_Key>
export AWS_DEFAULT_REGION=us-east-1


apt update
apt install apt-transport-https ca-certificates curl software-properties-common -y
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu focal stable"

#Installing Docker
apt update
apt-cache policy docker-ce
apt install docker-ce -y
apt install awscli -y
#Be sure to understand, if you follow official Kubernetes documentation, in Ubuntu 20 it does not work, that is why, I did modification to script
#Adding Kubernetes repositories

#Next 2 lines are different from official Kubernetes guide, but the way Kubernetes describe step does not work
curl -s https://packages.cloud.google.com/apt/doc/apt-key.gpg | apt-key add
echo "deb https://packages.cloud.google.com/apt kubernetes-xenial main" > /etc/apt/sources.list.d/kurbenetes.list

#Turn off swap
swapoff -a

#Installing Kubernetes tools
apt update
apt install kubelet kubeadm kubectl -y

#next line is getting EC2 instance IP, for kubeadm to initiate cluster
#we need to get EC2 internal IP address- default ENI is eth0
export ipaddr=`ip address|grep eth0|grep inet|awk -F ' ' '{print $2}' |awk -F '/' '{print $1}'`
export pubip=`dig +short myip.opendns.com @resolver1.opendns.com`

# the kubeadm init won't work entel remove the containerd config and restart it.
rm /etc/containerd/config.toml
systemctl restart containerd

#Kubernetes cluster init
#You can replace 172.16.0.0/16 with your desired pod network
kubeadm init --apiserver-advertise-address=$ipaddr --pod-network-cidr=172.16.0.0/16 --apiserver-cert-extra-sans=$pubip > /tmp/restult.out
cat /tmp/restult.out

#to get join commdn
tail -2 /tmp/restult.out > /tmp/join_command.sh;
aws s3 cp /tmp/join_command.sh s3://k8sinstalletion;
#this adds .kube/config for root account, run same for ubuntu user, if you need it
mkdir -p /root/.kube;
cp -i /etc/kubernetes/admin.conf /root/.kube/config;
cp -i /etc/kubernetes/admin.conf /tmp/admin.conf;
chmod 777 /tmp/admin.conf
#to copy kube config file to s3
# aws s3 cp /etc/kubernetes/admin.conf s3://k8sinstalletion

#Uncomment next line if you want calico Cluster Pod Network
curl -o /root/calico.yaml https://docs.projectcalico.org/v3.16/manifests/calico.yaml
sleep 5
kubectl --kubeconfig /root/.kube/config apply -f /root/calico.yaml
systemctl restart kubelet