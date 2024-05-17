package com.sfcockpit;

import java.io.IOException;
import com.sfcockpit.helper.LoginService.LoginServiceException;


public class Main {

    public static class ArgumentException extends Exception {
        public ArgumentException(String string) {
            super(string);
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException, LoginServiceException, ArgumentException {
        if (args.length == 0 ) {
            throw new ArgumentException("Missing arguments - pass <configFileName> and <eventName__e>");
        }
        String configFileName = args[0];
        String eventName = "/event/" + args[1];
        new PubSubGRPCClient(configFileName).subscribe(eventName);
    }

}

