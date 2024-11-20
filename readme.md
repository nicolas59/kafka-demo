## On en parle 

- https://learn.conduktor.io/kafka/kafka-consumer-cli-tutorial/


## Pre requis

```shell
#!/bin/bash
sudo yum install -y https://s3.amazonaws.com/ec2-downloads-windows/SSMAgent/latest/linux_arm64/amazon-ssm-agent.rpm
sudo yum install -y git ansible
sudo yum install -y java-21-amazon-corretto
sudo ansible-galaxy install sleighzy.zookeeper sleighzy.kafka
```


## Création d'un topic

```shell
sudo /opt/kafka/bin/kafka-topics.sh \
         --create  \
         --bootstrap-server localhost:9092 \
         --topic test-demo
```


## Consommation
```shell
sudo /opt/kafka/bin/kafka-console-consumer.sh \
    --bootstrap-server localhost:9092     \
    --topic test-demo \
    --from-beginning \
    --property print.timestamp=true --property print.key=true \
    --property print.value=true \
    --property print.headers=true

```

