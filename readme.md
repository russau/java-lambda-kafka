*With a layer*

```
zip truststore.zip kafka.kafka-1.truststore.jks
aws lambda publish-layer-version --layer-name certificates --zip-file fileb://~/Downloads/truststore.zip

aws lambda create-function \
--function-name ssl-lambda-layers \
--runtime java11 \
--role arn:aws:iam::009925882039:role/service-role/java-lambda-role-1jypc25q \
--handler "example.Kafka::myHandler" \
--zip-file fileb://build/distributions/java-lambda.zip \
--layers arn:aws:lambda:us-west-2:009925882039:layer:certificates:2 \
--timeout 600 \
--memory-size 512
```

*Built into the zip*

```
aws lambda create-function \
--function-name ssl-lambda-inbuilt \
--runtime java11 \
--role arn:aws:iam::009925882039:role/service-role/java-lambda-role-1jypc25q \
--handler "example.Kafka::myHandler" \
--zip-file fileb://build/distributions/java-lambda.zip \
--timeout 600 \
--memory-size 512
```