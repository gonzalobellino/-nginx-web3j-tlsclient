package web3j;

import java.io.IOException;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthBlockNumber;
import org.web3j.protocol.core.methods.response.EthGasPrice;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;

import nl.altindag.sslcontext.SSLFactory;
import okhttp3.OkHttpClient;
import okhttp3.internal.tls.OkHostnameVerifier;

public class App {
   private static Logger logger = LoggerFactory.getLogger(App.class);

   public static void main(String[] args) throws IOException {
      logger.info("Connecting to Ethereum ...");

      //Java Keystore usage for testing purpose on Java 1.8
      //PKCS12 is the standard way
      Path clientJks = new java.io.File("./src/main/resources/client.jks").toPath();
      Path trustStoreJks = new java.io.File("./src/main/resources/myTrustore.jks").toPath();

      //using nl.altindag SSLFactory, see here for more details --> https://github.com/Hakky54/sslcontext-kickstart
      SSLFactory sslFactory = SSLFactory.builder()
            .withIdentityMaterial(clientJks, "password".toCharArray(), "JKS")
            // .withDefaultTrustMaterial()
            .withTrustMaterial(trustStoreJks, "password".toCharArray(), "JKS")
            //Using java 11 with backward compatibility with 1.8 anyway we can use TLS v1.3
            .withProtocol("TLSv1.2").build();

      OkHttpClient client = new OkHttpClient.Builder()
            .sslSocketFactory(sslFactory.getSslContext().getSocketFactory(), sslFactory.getTrustManager().get())
            // ALT_DNS Required --> https://www.ietf.org/rfc/rfc2818.txt
            .hostnameVerifier(OkHostnameVerifier.INSTANCE).build();

      //Using a reverse proxy with tls client check
      HttpService service = new HttpService("https://localhost", client);
      Web3j web3 = Web3j.build(service);
      logger.info("Successfuly connected to Ethereum...");

      // web3_clientVersion returns the current client version.
      Web3ClientVersion clientVersion = web3.web3ClientVersion().send();
      // eth_blockNumber returns the number of most recent block.
      EthBlockNumber blockNumber = web3.ethBlockNumber().send();
      // eth_gasPrice, returns the current price per gas in wei.
      EthGasPrice gasPrice = web3.ethGasPrice().send();

      logger.info("Client version: " + clientVersion.getWeb3ClientVersion());
      logger.info("Block number: " + blockNumber.getBlockNumber());
      logger.info("Gas price: " + gasPrice.getGasPrice());
      service.close();
      System.exit(0);
   }
}