package com.sfcockpit;

import java.io.IOException;
import com.sfcockpit.helper.LoginService.LoginServiceException;


public class Main {
    public static void main(String[] args) throws IOException, InterruptedException, LoginServiceException {
        new PubSubGRPCClient("exampleConfiguration.yaml").subscribe("/event/TestEvent__e");
    }

}