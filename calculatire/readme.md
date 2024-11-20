

## Configuration

Modifier le fichier /etc/kafka/server.properties

```txt
listeners=PLAINTEXT://0.0.0.0:9092
advertised.listeners=PLAINTEXT://<ip_de_la_vm>:9092
```