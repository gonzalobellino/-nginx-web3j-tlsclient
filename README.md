# Using NGINX with GETH service JSON-RPC + TLS client auth

This simple project has an example of how you can establish JSON-RPC connection thru a reverse proxy with
mutual authentication using Java language and [Web3j](https://github.com/web3j/web3j).

## Pre requirements
   * Crypto materials --> see here for using pre made files or just create new ones https://github.com/gonzalobellino/nginx-web3js-tlsclient
   * Run Nginx --> see here  https://github.com/gonzalobellino/nginx-web3js-tlsclient
   * Run geth --> see here https://github.com/web3j/web3j or here https://www.trufflesuite.com/ganache


## Dependencies
   * Java 1.8 or above
   * [Web3j 4.6.1](https://github.com/web3j/web3j)  (use [OkHttpClient 4.3.1)](https://square.github.io/okhttp/4.x/okhttp/okhttp3/-ok-http-client/)
   * [hakky54 (sslcontext)](https://github.com/Hakky54/sslcontext-kickstart)
   * [logback](http://logback.qos.ch/)


## Build keystore with client cert and client key (the sample project includes sample crypto materials)

   * pack client cert & key to pkcs12 format
~~~    
openssl pkcs12 -export -in client.crt -inkey client.key -out client.p12   
~~~         

   * JKS output
~~~   
keytool -importkeystore -srckeystore client.p12 \
   -srcstoretype PKCS12 \
   -destkeystore client.jks \
   -deststoretype JKS
~~~

   * Import self CA into Keystore
~~~
keytool -import -keystore client.jks -file ca.crt  -alias clientCA
~~~   

   * Make a custom TrustStore

~~~   
keytool -import -file ca.crt -alias rootCA -keystore myTrustore.jks -storetype JKS
~~~   

** This example use JKS just for compatibility with java 1.8. Use PKCS12 for java 9 or above.
** It's important that the used server certificate be compatible with this rfc https://www.ietf.org/rfc/rfc2818.txt

## Running App

~~~
mvn exec:java -Dexec.mainClass="web3j.App"
~~~


## more info...
   https://www.ietf.org/rfc/rfc2818.txt
   https://docs.oracle.com/cd/E19509-01/820-3503/6nf1il6er/index.html
   https://dzone.com/articles/intro-to-blockchain-with-ethereum-web3j-and-spring
