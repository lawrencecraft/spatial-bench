echo 10.131.65.110    stormcluster-1 >> /etc/hosts
echo 10.131.68.120    stormcluster-2 >> /etc/hosts
echo 10.131.69.50     stormcluster-3 >> /etc/hosts
echo 10.131.105.44    kafkab1 >> /etc/hosts

apt install -y python
apt install -y openjdk-8-jre-headless

cd /opt
wget https://www-eu.apache.org/dist/storm/apache-storm-1.2.2/apache-storm-1.2.2.tar.gz
tar -xzvf apache-storm*gz
ln -s apache-storm*/ storm
