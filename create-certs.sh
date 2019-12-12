#!/bin/bash

# Generate CA key
# creates ca.crt ca.key
openssl req -new -x509 -keyout ca.key -out ca.crt -days 365 -subj '/CN=ca1.test.confluent.io/OU=TEST/O=CONFLUENT/L=PaloAlto/S=Ca/C=US' -passin pass:confluent -passout pass:confluent

i=kafka-1

# creates kafka.kafka-1.keystore.jks
# keystore now contains "kafka-1, Dec 10, 2019, PrivateKeyEntry"
keytool -genkey -noprompt \
				 -alias $i \
				 -dname "CN=ec2-54-202-36-111.us-west-2.compute.amazonaws.com,OU=TEST,O=CONFLUENT,L=PaloAlto,S=Ca,C=US" \
                                 -ext san=dns:$i \
				 -keystore kafka.$i.keystore.jks \
				 -keyalg RSA \
				 -storepass confluent \
				 -keypass confluent

# Create the certificate signing request (CSR)
# creates kafka-1.csr
keytool -keystore kafka.$i.keystore.jks -alias $i -certreq -file $i.csr -storepass confluent -keypass confluent

# Sign the host certificate with the certificate authority (CA)
# creates kafka-1-ca1-signed.crt
openssl x509 -req -CA ca.crt -CAkey ca.key -in $i.csr -out $i-ca1-signed.crt -days 9999 -CAcreateserial -passin pass:confluent

# Sign and import the CA cert into the keystore
keytool -noprompt -keystore kafka.$i.keystore.jks -alias CARoot -import -file ca.crt -storepass confluent -keypass confluent

# Sign and import the host certificate into the keystore
keytool -noprompt -keystore kafka.$i.keystore.jks -alias $i -import -file $i-ca1-signed.crt -storepass confluent -keypass confluent

# Create truststore and import the CA cert
keytool -noprompt -keystore kafka.$i.truststore.jks -alias CARoot -import -file ca.crt -storepass confluent -keypass confluent
# Save creds

echo "confluent" > ${i}_sslkey_creds
echo "confluent" > ${i}_keystore_creds
echo "confluent" > ${i}_truststore_creds
